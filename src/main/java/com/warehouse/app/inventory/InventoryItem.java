package com.warehouse.app.inventory;

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

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public int getAvailable() {
		return available;
	}

	public void setAvailable(int available) {
		this.available = available;
	}

	public int getReserved() {
		return reserved;
	}

	public void setReserved(int reserved) {
		this.reserved = reserved;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}
}
