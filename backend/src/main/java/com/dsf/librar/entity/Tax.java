package com.dsf.librar.entity;

import com.dsf.librar.enums.TaxCalculationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "tax",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_tax_code",
                        columnNames = "code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tax {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    // VAT, EXCISE, INTERNAL, OTHER, etc.
    // String para permitir clasificaciones nuevas sin modificar un enum.
    @Column(length = 40)
    private String classification;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TaxCalculationType calculationType;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
