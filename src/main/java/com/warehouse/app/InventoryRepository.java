package com.warehouse.app;

import java.util.Optional;

import org.springframework.data.repository.Repository;

interface InventoryRepository extends Repository<InventoryItem, String> {
	Optional<InventoryItem> findBySku(String sku);
}
