package com.nhanpq.inventoryservice.service;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhanpq.inventoryservice.dto.InventoryResponse;
import com.nhanpq.inventoryservice.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    @SneakyThrows
    public List<InventoryResponse> isInStock(List<String> skuCode) {

        return inventoryRepository.findBySkuCodeIn(skuCode).stream().map(
            e-> InventoryResponse.builder()
            .skuCode(e.getSkuCode())
            .isInStock(e.getQuantity() > 0)
            .build()
        ).toList();
    }

}
