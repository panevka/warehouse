package com.warehouse.app;

class DomainEventDto {

	String sku;
	String type;
	String payload;

	public DomainEventDto(String sku, String type, String payload) {
		this.sku = sku;
		this.type = type;
		this.payload = payload;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}
}
