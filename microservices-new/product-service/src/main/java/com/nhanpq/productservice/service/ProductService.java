package com.nhanpq.productservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nhanpq.productservice.dto.ProductRequest;
import com.nhanpq.productservice.dto.ProductResponse;
import com.nhanpq.productservice.entity.Product;
import com.nhanpq.productservice.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct(ProductRequest productRequest) {

        Product product = Product.builder().name(productRequest.getName()).description(productRequest.getDescription())
                .price(productRequest.getPrice()).build();

        productRepository.save(product);
        log.info("Product {} is saved!", product.getId());
    }

    public List<ProductResponse> getAll(){
        List<Product> list =  productRepository.findAll();

        return list.stream().map(this::mapToProductResponse).toList();
        
    }

    private ProductResponse mapToProductResponse(Product e){
         return ProductResponse.builder()
            .id(e.getId())
            .name(e.getName())
            .description(e.getDescription())
            .price(e.getPrice()).build();
    }

}
