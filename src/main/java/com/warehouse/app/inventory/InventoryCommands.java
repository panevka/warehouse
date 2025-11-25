package com.warehouse.app.inventory;

import org.springframework.stereotype.Repository;

public interface InventoryCommands {
	int reserve(String sku, int qty, long expectedVersion);
}
