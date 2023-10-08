package com.nhanpq.inventoryservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.nhanpq.inventoryservice.entity.Inventory;
import com.nhanpq.inventoryservice.repository.InventoryRepository;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository){
		return args ->{
			Inventory inventory = Inventory.builder()
			.skuCode("iphone_13")
			.quantity(100)
			.build();

			Inventory inventory1 = Inventory.builder()
			.skuCode("iphone_13_red")
			.quantity(0)
			.build();



			inventoryRepository.save(inventory);
			inventoryRepository.save(inventory1);

		};
	}

}
