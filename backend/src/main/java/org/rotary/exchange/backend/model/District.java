package org.rotary.exchange.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.security.SecureRandom;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class District {

    private static final String CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final int ACCESS_CODE_LENGTH = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String code;

    /**
     * Access code for student camp viewing.
     * This code is used in URLs to prevent students from guessing other districts.
     */
    @Column(unique = true, nullable = false, length = 12)
    private String accessCode;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    /**
     * Generates a random access code for this district.
     * Should be called before persisting a new district.
     */
    @PrePersist
    public void generateAccessCodeIfMissing() {
        if (this.accessCode == null || this.accessCode.isEmpty()) {
            this.accessCode = generateRandomCode();
        }
    }

    private static String generateRandomCode() {
        StringBuilder sb = new StringBuilder(ACCESS_CODE_LENGTH);
        for (int i = 0; i < ACCESS_CODE_LENGTH; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
