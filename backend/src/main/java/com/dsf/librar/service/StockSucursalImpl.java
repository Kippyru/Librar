package com.dsf.librar.service;

import com.dsf.librar.dto.StockSucursalRequestDto;
import com.dsf.librar.dto.StockSucursalResponseDto;
import com.dsf.librar.entity.Product;
import com.dsf.librar.entity.StockSucursal;
import com.dsf.librar.entity.Sucursal;
import com.dsf.librar.mapper.StockSucursalMapper;
import com.dsf.librar.repository.ProductRepository;
import com.dsf.librar.repository.StockSucursalRepository;
import com.dsf.librar.repository.SucursalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockSucursalImpl implements StockSucursalService {
    private final StockSucursalRepository stockSucursalRepository;
    private final ProductRepository productRepository;
    private final SucursalRepository sucursalRepository;
    private final StockSucursalMapper stockSucursalMapper;

    @Override
    @Transactional
    public StockSucursalResponseDto createStockSucursal(StockSucursalRequestDto dto) {
        Product product = productRepository.findById(dto.getProduct())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Sucursal sucursal = sucursalRepository.findById(dto.getSucursal())
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));

        StockSucursal stock = stockSucursalMapper.toEntity(dto);
        stock.setProduct(product);
        stock.setSucursal(sucursal);
        StockSucursal saved = stockSucursalRepository.save(stock);

        return stockSucursalMapper.toDto(saved);
    }

    @Override
    @Transactional
    public StockSucursalResponseDto findById(Long id) {
        StockSucursal stock = stockSucursalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock no encontrado"));
        return stockSucursalMapper.toDto(stock);
    }

    @Override
    @Transactional
    public List<StockSucursalResponseDto> findAll() {
        return stockSucursalMapper.listStockSucursal(stockSucursalRepository.findAll());
    }

    @Override
    @Transactional
    public StockSucursalResponseDto update(
            Long id,
            StockSucursalRequestDto dto) {
        StockSucursal stock = stockSucursalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock no encontrado"));

        stockSucursalMapper.updateStockSucursal(dto, stock);
        return stockSucursalMapper.toDto(stockSucursalRepository.save(stock));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        StockSucursal stock = stockSucursalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock no encontrado"));
        stockSucursalRepository.delete(stock);
    }

    @Override
    @Transactional
    public void decreaseStock(
            Long product,
            Long sucursal,
            int amount
    ) {
        StockSucursal stock = stockSucursalRepository
                .findByProductIdAndSucursalId(product, sucursal)
                .orElseThrow(() -> new RuntimeException("Stock no encontrado"));

        if (stock.getAmount() < amount) {
            throw new RuntimeException("Stock insuficiente");
        }

        stock.setAmount(stock.getAmount() - amount);
    }


    @Override
    @Transactional
    public List<StockSucursalResponseDto> findLowStock() {
        List<StockSucursal> stocks =
                stockSucursalRepository.findLowStock();

        return stockSucursalMapper.listStockSucursal(stocks);
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkLowStock() {
        List<StockSucursal> stocks =
                stockSucursalRepository.findLowStock();

        for (StockSucursal stock : stocks) {
            System.out.println(
                    "LOW STOCK - Producto: "
                            + stock.getProduct().getId()
                            + " | Sucursal: "
                            + stock.getSucursal().getId()
                            + " | Cantidad: "
                            + stock.getAmount()
                            + " | Mínimo: "
                            + stock.getProduct().getMinimumStock()
            );
        }
    }


}
