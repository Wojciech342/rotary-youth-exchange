package org.rotary.exchange.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

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

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;
    
    @Column(nullable = false)
    private String passwordHash;
    
    private String profilePictureUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "coordinator_roles",
            joinColumns = @JoinColumn(name = "coordinator_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // Constructor for registration
    public Coordinator(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }
}