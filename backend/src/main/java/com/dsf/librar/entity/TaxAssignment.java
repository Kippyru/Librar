package com.dsf.librar.entity;

import com.dsf.librar.enums.TaxAssignmentEffect;
import com.dsf.librar.enums.TaxAssignmentScope;
import com.dsf.librar.enums.TaxOperation;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "tax_assignment",
        indexes = {
                @Index(
                        name = "idx_tax_assignment_tax",
                        columnList = "tax_id"
                ),
                @Index(
                        name = "idx_tax_assignment_product",
                        columnList = "product_id"
                ),
                @Index(
                        name = "idx_tax_assignment_category",
                        columnList = "category_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tax_id", nullable = false)
    private Tax tax;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaxAssignmentScope scope;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaxOperation operation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaxAssignmentEffect effect;

    @Column(nullable = false)
    @Builder.Default
    private Integer priority = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    private LocalDate validFrom;

    private LocalDate validUntil;
}
