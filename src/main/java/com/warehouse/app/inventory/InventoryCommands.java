package com.warehouse.app.inventory;

public interface InventoryCommands {
	int reserve(String sku, int qty, long expectedVersion);
}
