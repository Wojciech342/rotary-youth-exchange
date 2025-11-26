package org.rotary.exchange.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

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
}
