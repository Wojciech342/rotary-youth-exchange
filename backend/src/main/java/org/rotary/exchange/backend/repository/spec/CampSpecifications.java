package org.rotary.exchange.backend.repository.spec;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.rotary.exchange.backend.dto.CampSearchCriteria;
import org.rotary.exchange.backend.model.CampDistrictStatus;
import org.rotary.exchange.backend.model.CampInstance;
import org.rotary.exchange.backend.model.CampStatus;
import org.rotary.exchange.backend.model.CampTemplate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CampSpecifications {

    public static Specification<CampInstance> withCriteria(CampSearchCriteria criteria, boolean publicViewOnly) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. JOIN with Template (Because Name/Desc are in the parent table)
            Join<CampInstance, CampTemplate> templateJoin = root.join("campTemplate");

            // 2. PUBLIC VIEW LOGIC (Hide drafts/archives for students)
            if (publicViewOnly) {
                // For public view, filter by district's local status
                if (criteria.getDistrictId() != null) {
                    // Join with CampDistrictStatus to filter by district
                    Join<CampInstance, CampDistrictStatus> statusJoin = root.join("districtStatuses", JoinType.INNER);
                    predicates.add(cb.equal(statusJoin.get("district").get("id"), criteria.getDistrictId()));
                    
                    // Check local status for this district (show only available camps)
                    predicates.add(statusJoin.get("localStatus").in(
                            CampStatus.OPEN, 
                            CampStatus.ONLY_MALE, 
                            CampStatus.ONLY_FEMALE
                    ));
                } else {
                    // Fallback: filter by global status (shouldn't happen for students)
                    predicates.add(root.get("globalStatus").in(
                            CampStatus.OPEN, 
                            CampStatus.ONLY_MALE, 
                            CampStatus.ONLY_FEMALE
                    ));
                }
            } else if (criteria.getCoordinatorId() != null) {
                // DASHBOARD VIEW: Only show camps for this coordinator
                predicates.add(cb.equal(root.get("coordinator").get("id"), criteria.getCoordinatorId()));
            }

            // 3. FILTER: Text Search (Name or Description)
            if (criteria.getQuery() != null && !criteria.getQuery().isEmpty()) {
                String likePattern = "%" + criteria.getQuery().toLowerCase() + "%";
                Predicate nameMatch = cb.like(cb.lower(templateJoin.get("name")), likePattern);
                Predicate descMatch = cb.like(cb.lower(templateJoin.get("description")), likePattern);
                predicates.add(cb.or(nameMatch, descMatch));
            }

            // 4. FILTER: Age (Check if student fits in range)
            if (criteria.getAge() != null) {
                // Camp Min <= Student Age AND Camp Max >= Student Age
                predicates.add(cb.lessThanOrEqualTo(templateJoin.get("ageMin"), criteria.getAge()));
                predicates.add(cb.greaterThanOrEqualTo(templateJoin.get("ageMax"), criteria.getAge()));
            }

            // 5. FILTER: Price
            if (criteria.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), criteria.getMaxPrice()));
            }

            // 6. FILTER: Dates
            if (criteria.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateStart"), criteria.getDateFrom()));
            }
            if (criteria.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateEnd"), criteria.getDateTo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}