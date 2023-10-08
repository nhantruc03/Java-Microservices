package com.nhanpq.productservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nhanpq.productservice.entity.Product;

@Repository
public interface ProductRepository extends MongoRepository<Product,String>{
    
}
