package org.rotary.exchange.backend.service;

import lombok.RequiredArgsConstructor;
import org.rotary.exchange.backend.exception.ResourceNotFoundException;
import org.rotary.exchange.backend.model.*;
import org.rotary.exchange.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DistrictStatusService {

    private final CampDistrictStatusRepository statusRepo;
    private final CampInstanceRepository instanceRepo;
    private final DistrictRepository districtRepo;

    public CampDistrictStatus getStatus(Integer campId, Integer districtId) {
        return statusRepo.findByCampInstanceIdAndDistrictId(campId, districtId)
                .orElseGet(() -> createDefaultStatus(campId, districtId));
    }

    @Transactional
    public CampDistrictStatus updateLocalStatusManual(Integer campId, Integer districtId, CampStatus desiredStatus) {
        CampInstance camp = instanceRepo.findById(campId)
                .orElseThrow(() -> new ResourceNotFoundException("Camp", "id", campId));
        CampStatus globalStatus = camp.getGlobalStatus();

        if (!isCompatible(globalStatus, desiredStatus)) {
            throw new IllegalArgumentException("Cannot set status " + desiredStatus +
                    " because Global Status is " + globalStatus);
        }

        CampDistrictStatus ds = getStatus(campId, districtId);
        ds.setLocalStatus(desiredStatus);
        return statusRepo.save(ds);
    }

    @Transactional
    public void recalculateAllLocalStatuses(Integer campId, CampStatus newGlobalStatus) {
        List<CampDistrictStatus> allStatuses = statusRepo.findByCampInstanceId(campId);

        for (CampDistrictStatus ds : allStatuses) {
            CampStatus currentLocal = ds.getLocalStatus();
            CampStatus calculated = calculateIntersection(newGlobalStatus, currentLocal);

            if (calculated != currentLocal) {
                ds.setLocalStatus(calculated);
            }
        }
        
        if (!allStatuses.isEmpty()) {
            statusRepo.saveAll(allStatuses);
        }
    }

    private CampDistrictStatus createDefaultStatus(Integer campId, Integer districtId) {
        // Validate that camp and district exist
        if (!instanceRepo.existsById(campId)) {
            throw new ResourceNotFoundException("Camp", "id", campId);
        }
        if (!districtRepo.existsById(districtId)) {
            throw new ResourceNotFoundException("District", "id", districtId);
        }

        CampDistrictStatus ds = new CampDistrictStatus();
        ds.setCampInstance(instanceRepo.getReferenceById(campId));
        ds.setDistrict(districtRepo.getReferenceById(districtId));
        ds.setLocalStatus(CampStatus.OPEN);
        return statusRepo.save(ds);
    }

    private boolean isCompatible(CampStatus global, CampStatus local) {
        return calculateIntersection(global, local) == local;
    }

    private CampStatus calculateIntersection(CampStatus global, CampStatus local) {
        // Handle ARCHIVED and CLOSED
        if (global == CampStatus.ARCHIVED || global == CampStatus.CLOSED) {
            return global;
        }
        
        boolean allowMale = allowsMale(global) && allowsMale(local);
        boolean allowFemale = allowsFemale(global) && allowsFemale(local);

        if (allowMale && allowFemale) return CampStatus.OPEN;
        if (!allowMale && allowFemale) return CampStatus.ONLY_FEMALE;
        if (allowMale && !allowFemale) return CampStatus.ONLY_MALE;
        return CampStatus.CLOSED;
    }

    private boolean allowsMale(CampStatus s) {
        return s == CampStatus.OPEN || s == CampStatus.ONLY_MALE;
    }

    private boolean allowsFemale(CampStatus s) {
        return s == CampStatus.OPEN || s == CampStatus.ONLY_FEMALE;
    }
}
