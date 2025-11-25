package com.warehouse.app;

import org.springframework.stereotype.Service;

@Service
class InventoryService {
	InventoryRepository inventoryRepository;
	DomainEventService eventService;

	InventoryService(InventoryRepository inventoryRepository, DomainEventService eventService) {
		this.inventoryRepository = inventoryRepository;
		this.eventService = eventService;
	}

	void reserve(String sku, int qty) {
		InventoryItem item = inventoryRepository.findBySku(sku)
				.orElseThrow(() -> new RuntimeException("Item not found"));

		item.reserve(qty);

		inventoryRepository.save(item);
		DomainEventDto event = new DomainEventDto(item.getSku(), "InventoryReserved",
				"{\"sku\":\"" + sku + "\", \"qty\":" + qty + "}");
		eventService.publishEvent(event);
	}
}
