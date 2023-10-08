package com.nhanpq.productservice;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhanpq.productservice.dto.ProductRequest;
import com.nhanpq.productservice.entity.Product;
import com.nhanpq.productservice.repository.ProductRepository;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

	@Container
	static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.4.2"));

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ProductRepository productRepository;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Test
	void shouldCreateProduct() throws Exception {
		// ProductRequest product = getProductRequest();
		// String productString = objectMapper.writeValueAsString(product);
		// mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
		// 		.contentType(MediaType.APPLICATION_JSON)
		// 		.content(productString)).andExpect(MockMvcResultMatchers.status().isCreated());

		Assertions.assertEquals(1, productRepository.findAll().size());
	}

	@Test
	void shouldGetProduct() throws Exception {
		ProductRequest productRequest = getProductRequest();
		String productString = objectMapper.writeValueAsString(productRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productString)).andExpect(MockMvcResultMatchers.status().isCreated());

		mockMvc.perform(MockMvcRequestBuilders.get("/api/product"))
				.andExpect(MockMvcResultMatchers.status().isOk());

		List<Product> listProduct = productRepository.findAll();

		Assertions.assertEquals(1, listProduct.size());

		Product product = listProduct.get(0);

		ProductRequest temp = getProductRequest();

		Assertions.assertEquals(temp, ProductRequest.builder()
				.name(product.getName())
				.description(product.getDescription())
				.price(product.getPrice())
				.build());

	}

	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.name("product name 1")
				.description("description product 1")
				.price(BigDecimal.valueOf(2000000l))
				.build();
	}

}
