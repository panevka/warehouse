package com.warehouse.app.event;

public class DomainEventDto {

	private String sku;
	private EventType type;
	private String payload;

	public DomainEventDto(String sku, EventType type, String payload) {
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

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}
}
