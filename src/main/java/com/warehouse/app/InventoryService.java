package com.warehouse.app;

import org.springframework.stereotype.Service;

@Service
class InventoryService {
	InventoryRepository inventoryRepository;

	public InventoryService(InventoryRepository inventoryRepository) {
		this.inventoryRepository = inventoryRepository;
	}

	void reserve(String sku, int qty) {
		InventoryItem item = inventoryRepository.findBySku(sku)
				.orElseThrow(() -> new RuntimeException("Item not found"));

		item.reserve(qty);
	}
}
