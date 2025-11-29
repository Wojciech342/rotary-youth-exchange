package org.rotary.exchange.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "template_id")
    private CampTemplate campTemplate;

    @ManyToOne
    @JoinColumn(name = "coordinator_id")
    private Coordinator coordinator;

    private LocalDate dateStart;
    private LocalDate dateEnd;
    private Integer price;
    private Integer edition;

    @Enumerated(EnumType.STRING)
    private CampStatus globalStatus;

    private Integer limitTotal;
    private Integer limitMale;
    private Integer limitFemale;

    @OneToMany(mappedBy = "campInstance", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CampDistrictStatus> districtStatuses = new ArrayList<>();
}
