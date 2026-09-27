package com.dsf.librar.service;

import com.dsf.librar.dto.ProductRequestDto;
import com.dsf.librar.dto.ProductResponseDto;
import com.dsf.librar.entity.Product;
import com.dsf.librar.mapper.ProductMapper;
import com.dsf.librar.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public void importExcel(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            // takes the FIRST excel page
            Sheet sheet = workbook.getSheetAt(0);
            List<Product> products = new ArrayList<>();

            for (Row row : sheet) {
                // when it reads the first row, it skips it
                if (row.getRowNum() == 0) {
                    continue;
                }

                Product product = new Product();

                // column map: A=Código, B=Nombre, C=PrecioCompra, D=PrecioVenta, E=StockMinimo
                // if the C/B/E cell is/are empty, we automatically assign 0 to it.

                product.setBarcode(getStringValue(row.getCell(0)));
                product.setName(getStringValue(row.getCell(1)));
                product.setPurchasePrice(getBigDecimalValue(row.getCell(2)));
                product.setSellingPrice(getBigDecimalValue(row.getCell(3)));
                product.setMinimumStock(getNumericValue(row.getCell(4)).intValue());

                product.setActive(true);
                products.add(product);
            }

            // saving in batch
            productRepository.saveAll(products);

        } catch (Exception e) {
            throw new RuntimeException("Error procesando el Excel: " + e.getMessage());
        }
    }

    // --- Métodos Helper para evitar NullPointerExceptions y problemas de formato de POI ---

    private String getStringValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue();
        if (cell.getCellType() == CellType.NUMERIC) return String.valueOf((long) cell.getNumericCellValue());
        return null;
    }

    private BigDecimal getBigDecimalValue(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) return BigDecimal.ZERO;
        return BigDecimal.valueOf(cell.getNumericCellValue());
    }

    private Double getNumericValue(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) return 0.0;
        return cell.getNumericCellValue();
    }

    @Override
    public void createProduct(ProductRequestDto productRequestDto) {
        Product product = productMapper.toEntity(productRequestDto);
        productRepository.save(product);
    }

    @Override
    public List<ProductResponseDto> listProducts() {
        return productMapper.listProduct(productRepository.findAll());
    }

    @Override
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return productMapper.toDto(product);
    }

    @Override
    public void updateProduct(Long id, ProductRequestDto productRequestDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productMapper.updateProduct(productRequestDto, product);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setActive(false);
        productRepository.save(product);
    }

    @Override
    public void restoreProducts(Long id) {
        productRepository.restoreById(id);
    }

}