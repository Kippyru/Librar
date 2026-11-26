package com.dsf.librar.service.impl;

import com.dsf.librar.dto.request.TaxAssignmentRequestDto;
import com.dsf.librar.dto.response.TaxAssignmentResponseDto;
import com.dsf.librar.entity.Category;
import com.dsf.librar.entity.Product;
import com.dsf.librar.entity.Tax;
import com.dsf.librar.entity.TaxAssignment;
import com.dsf.librar.mapper.TaxAssignmentMapper;
import com.dsf.librar.repository.CategoryRepository;
import com.dsf.librar.repository.ProductRepository;
import com.dsf.librar.repository.TaxAssignmentRepository;
import com.dsf.librar.repository.TaxRepository;
import com.dsf.librar.service.TaxAssignmentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.dsf.librar.enums.TaxAssignmentScope.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaxAssignmentServiceImpl implements TaxAssignmentService {

    private final TaxRepository taxRepository;
    private final TaxAssignmentRepository taxAssignmentRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final TaxAssignmentMapper taxAssignmentMapper;

    @Override
    @Transactional
    public TaxAssignmentResponseDto create(TaxAssignmentRequestDto dto) {

        Tax tax = findTax(dto.taxId());

        if (!tax.isActive()) {
            throw new IllegalStateException(
                    "No se puede asignar un impuesto inactivo"
            );
        }

        validateAssignment(dto);
        validateDates(dto.validFrom(), dto.validUntil());

        validateNoDuplicate(dto);

        TaxAssignment assignment = new TaxAssignment();

        assignment.setTax(tax);
        assignment.setScope(dto.scope());
        assignment.setOperation(dto.operation());
        assignment.setEffect(dto.effect());
        assignment.setPriority(dto.priority());
        assignment.setValidFrom(dto.validFrom());
        assignment.setValidUntil(dto.validUntil());
        assignment.setActive(true);

        if (dto.scope() == PRODUCT) {
            assignment.setProduct(findProduct(dto.productId()));
        }

        if (dto.scope() == CATEGORY) {
            assignment.setCategory(findCategory(dto.categoryId()));
        }

        return taxAssignmentMapper.toResponseDto(taxAssignmentRepository.save(assignment));
    }

    @Override
    public TaxAssignmentResponseDto getById(Long id) {
        return taxAssignmentMapper.toResponseDto(findAssignment(id));
    }

    @Override
    public List<TaxAssignmentResponseDto> findByTax(Long taxId) {

        findTax(taxId);

        return taxAssignmentRepository.findByTaxId(taxId)
                .stream()
                .map(taxAssignmentMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<TaxAssignmentResponseDto> findByProduct(Long productId) {

        findProduct(productId);

        return taxAssignmentRepository.findByProductId(productId)
                .stream()
                .map(taxAssignmentMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<TaxAssignmentResponseDto> findByCategory(Long categoryId) {

        findCategory(categoryId);

        return taxAssignmentRepository.findByCategoryId(categoryId)
                .stream()
                .map(taxAssignmentMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public TaxAssignmentResponseDto update(
            Long id,
            TaxAssignmentRequestDto dto
    ) {

        TaxAssignment assignment = findAssignment(id);

        Tax tax = findTax(dto.taxId());

        if (!tax.isActive()) {
            throw new IllegalStateException(
                    "No se puede asignar un impuesto inactivo"
            );
        }

        validateAssignment(dto);
        validateDates(dto.validFrom(), dto.validUntil());
        validateNoDuplicate(dto, id);

        assignment.setTax(tax);
        assignment.setScope(dto.scope());
        assignment.setOperation(dto.operation());
        assignment.setEffect(dto.effect());
        assignment.setPriority(dto.priority());
        assignment.setValidFrom(dto.validFrom());
        assignment.setValidUntil(dto.validUntil());

        assignment.setProduct(dto.scope() == PRODUCT ? findProduct(dto.productId()) : null);

        assignment.setCategory(dto.scope() == CATEGORY ? findCategory(dto.categoryId()) : null);

        return taxAssignmentMapper.toResponseDto(taxAssignmentRepository.save(assignment));
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        TaxAssignment assignment = findAssignment(id);
        assignment.setActive(false);
        taxAssignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public void activate(Long id) {

        TaxAssignment assignment = findAssignment(id);

        if (!assignment.getTax().isActive()) {throw new IllegalStateException(
                "No se puede activar una asignación cuyo impuesto está inactivo"
            );
        }

        validateDates(assignment.getValidFrom(), assignment.getValidUntil());

        validateTarget(assignment);

        assignment.setActive(true);
        taxAssignmentRepository.save(assignment);
    }

    private void validateAssignment(TaxAssignmentRequestDto dto) {

        if (dto.taxId() == null ||
                dto.scope() == null ||
                dto.operation() == null ||
                dto.effect() == null) {

            throw new IllegalArgumentException(
                    "Los datos principales de la asignación son obligatorios"
            );
        }

        if (dto.priority() == null || dto.priority() < 0) {throw new IllegalArgumentException(
                    "La prioridad debe ser igual o mayor a cero"
            );
        }

        switch (dto.scope()) {

            case GLOBAL -> {
                if (dto.productId() != null || dto.categoryId() != null) {throw new IllegalArgumentException(
                            "Una asignación GLOBAL no admite producto ni categoría"
                    );
                }
            }

            case CATEGORY -> {
                if (dto.categoryId() == null || dto.productId() != null) {throw new IllegalArgumentException(
                            "Una asignación CATEGORY necesita categoría y no admite producto"
                    );
                }
            }

            case PRODUCT -> {
                if (dto.productId() == null || dto.categoryId() != null) {throw new IllegalArgumentException(
                            "Una asignación PRODUCT necesita producto y no admite categoría"
                    );
                }
            }
        }
    }

    private void validateDates(LocalDate validFrom, LocalDate validUntil) {

        if (validFrom == null) {throw new IllegalArgumentException(
                    "La fecha de inicio es obligatoria"
            );
        }

        if (validUntil != null && validUntil.isBefore(validFrom)) {throw new IllegalArgumentException(
                    "La fecha de finalización no puede ser anterior a la fecha de inicio"
            );
        }
    }

    private void validateTarget(TaxAssignmentRequestDto dto) {

        if (dto.scope() == PRODUCT) {findProduct(dto.productId());}

        if (dto.scope() == CATEGORY) {findCategory(dto.categoryId());}
    }

    private void validateTarget(TaxAssignment assignment) {

        if (assignment.getScope() == PRODUCT) {
            if (assignment.getProduct() == null ||
                    !productRepository.existsById(
                            assignment.getProduct().getId()
                    )) {throw new EntityNotFoundException(
                        "El producto asociado a la asignación no existe"
                );
            }
        }

        if (assignment.getScope() == CATEGORY) {
            if (assignment.getCategory() == null ||
                    !categoryRepository.existsById(
                            assignment.getCategory().getId()
                    )) {throw new EntityNotFoundException(
                        "La categoría asociada a la asignación no existe"
                );
            }
        }
    }

    private void validateNoDuplicate(TaxAssignmentRequestDto dto) {
        validateNoDuplicate(dto, null);
    }

    private void validateNoDuplicate(TaxAssignmentRequestDto dto, Long ignoredId) {

        boolean duplicate = taxAssignmentRepository
                .findByTaxId(dto.taxId())
                .stream()
                .filter(existing -> ignoredId == null ||
                        !existing.getId().equals(ignoredId))
                .filter(existing -> existing.getScope() == dto.scope())
                .filter(existing -> existing.getOperation() == dto.operation())
                .filter(existing -> existing.getEffect() == dto.effect())
                .filter(existing -> sameTarget(existing, dto))
                .anyMatch(existing -> overlaps(
                        existing.getValidFrom(),
                        existing.getValidUntil(),
                        dto.validFrom(),
                        dto.validUntil()
                ));

        if (duplicate) {throw new IllegalStateException(
                    "Ya existe una asignación equivalente para ese período"
            );
        }
    }

    private boolean sameTarget(TaxAssignment existing, TaxAssignmentRequestDto dto) {

        return switch (dto.scope()) {
            case GLOBAL -> true;

            case CATEGORY -> existing.getCategory() != null &&
                    existing.getCategory().getId().equals(dto.categoryId());

            case PRODUCT -> existing.getProduct() != null &&
                    existing.getProduct().getId().equals(dto.productId());
        };
    }

    private boolean overlaps(
            LocalDate startA,
            LocalDate endA,
            LocalDate startB,
            LocalDate endB
    ) {

        boolean startsBeforeEndB = endB == null || !startA.isAfter(endB);

        boolean endsAfterStartB = endA == null || !endA.isBefore(startB);

        return startsBeforeEndB && endsAfterStartB;
    }

    private Tax findTax(Long id) {
        return taxRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el impuesto con ID: " + id));
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el producto con ID: " + id));
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la categoría con ID: " + id));
    }

    private TaxAssignment findAssignment(Long id) {
        return taxAssignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la asignación con ID: " + id));
    }

}
