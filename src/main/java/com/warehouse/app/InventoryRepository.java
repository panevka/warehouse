package com.warehouse.app;

import java.util.Optional;

interface InventoryRepository {
	Optional<InventoryItem> findBySku(String sku);
}
