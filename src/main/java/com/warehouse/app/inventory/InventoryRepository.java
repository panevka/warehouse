package com.warehouse.app.inventory;

import java.util.Optional;

import org.springframework.data.repository.Repository;

public interface InventoryRepository extends Repository<InventoryItem, String> {
	Optional<InventoryItem> findBySku(String sku);
}
