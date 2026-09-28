package com.dsf.librar.controller;

import com.dsf.librar.dto.ProductRequestDto;
import com.dsf.librar.dto.ProductResponseDto;
import com.dsf.librar.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/import")

    public ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo está vacío");
        }

        productService.importExcel(file);
        return ResponseEntity.status(HttpStatus.CREATED).body("Productos importados exitosamente");
    }

    @PostMapping
    public ResponseEntity<String> createProduct(@Valid @RequestBody ProductRequestDto productRequestDto) {
        productService.createProduct(productRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Productos creados exitosamente");
    }

    @GetMapping("/list")
    public List<ProductResponseDto> listProducts() {
        return productService.listProducts();
    }

    @GetMapping("/list/{id}")
    public ProductResponseDto getProduct(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDto>> search(
            @RequestParam String q) {

        return ResponseEntity.ok(
                productService.search(q)
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequestDto productRequestDto) {
        productService.updateProduct(id, productRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Productos editados exitosamente");
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<String> restoreProduct(@PathVariable Long id) {
        productService.restoreProducts(id);
        return ResponseEntity.status(HttpStatus.CREATED).body("Productos restaurado exitosamente");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.CREATED).body("Productos eliminados exitosamente");
    }
}