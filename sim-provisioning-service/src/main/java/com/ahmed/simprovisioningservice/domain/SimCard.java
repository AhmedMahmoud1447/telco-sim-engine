package com.ahmed.simprovisioningservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sim_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String iccid;

    @Column(unique = true, length = 15)
    private String msisdn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SimStatus status;

    @Column(unique = true, length = 15)
    private String imsi;

    private LocalDateTime activatedAt;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = SimStatus.PENDING;
        }
    }
}