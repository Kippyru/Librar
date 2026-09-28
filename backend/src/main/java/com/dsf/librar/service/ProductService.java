package com.dsf.librar.service;

import com.dsf.librar.dto.ProductRequestDto;
import com.dsf.librar.dto.ProductResponseDto;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    void importExcel(MultipartFile file);
    void createProduct(ProductRequestDto productRequestDto);
    List<ProductResponseDto> listProducts();
    ProductResponseDto getProductById(Long id);
    List<ProductResponseDto> search(String query);
    void updateProduct(Long id, ProductRequestDto productRequestDto);
    void deleteProduct(Long id);
    void restoreProducts(Long id);
}