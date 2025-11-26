package com.warehouse.app.inventory.commands;

import com.warehouse.cqs.Command;

public class ReserveItemCommand implements Command<ReserveItemResult> {
	private String sku;
	private int qty;
	private long expectedVersion;

	public String getSku() {
		return sku;
	}

	public int getQty() {
		return qty;
	}

	public long getExpectedVersion() {
		return expectedVersion;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	public void setExpectedVersion(long expectedVersion) {
		this.expectedVersion = expectedVersion;
	}

}
