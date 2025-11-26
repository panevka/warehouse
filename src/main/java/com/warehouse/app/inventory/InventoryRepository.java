package com.warehouse.app.inventory;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface InventoryRepository extends CrudRepository<InventoryItem, String> {
	Optional<InventoryItem> findBySku(String sku);
}
