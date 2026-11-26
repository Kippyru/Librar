package com.dsf.librar.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "tax_rate",
        indexes = {
                @Index(
                        name = "idx_tax_rate_tax_validity",
                        columnList = "tax_id, valid_from, valid_until"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tax_id", nullable = false)
    private Tax tax;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal value;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    //valid_until significa que la tasa no tiene una fecha de finalización definida.
    @Column(name = "valid_until")
    private LocalDate validUntil;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
