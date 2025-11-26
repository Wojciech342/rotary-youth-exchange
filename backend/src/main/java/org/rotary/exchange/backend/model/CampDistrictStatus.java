package org.rotary.exchange.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"camp_instance_id", "district_id"})
})
public class CampDistrictStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "camp_instance_id")
    private CampInstance campInstance;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    @Enumerated(EnumType.STRING)
    private CampStatus localStatus;
}
