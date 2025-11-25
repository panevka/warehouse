package com.warehouse.app;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class InventoryItem {
	@Id
	String sku;
	@Column
	int available;
	@Column
	int reserved;
	@Column
	long version;

	public void reserve(int qty) {

		if (0 >= qty) {
			throw new RuntimeException("Invalid quantity requested.");
		}

		if (qty > available) {
			throw new RuntimeException("Insufficient stock available.");
		}

		this.available -= qty;
		this.reserved += qty;
	}
}
