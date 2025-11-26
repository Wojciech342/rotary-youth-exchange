package org.rotary.exchange.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coordinator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String email;

    private String phone;
    private String passwordHash;
    private String profilePictureUrl;

    @Column(columnDefinition = "TEXT")
    private String description;
}