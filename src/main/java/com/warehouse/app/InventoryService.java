package com.warehouse.app;

import org.springframework.stereotype.Service;

@Service
class InventoryService {
	InventoryRepository inventoryRepository;
	DomainEventService eventService;
	InventoryCommands inventoryCommands;

	InventoryService(InventoryRepository inventoryRepository, DomainEventService eventService,
			InventoryCommands inventoryCommands) {
		this.inventoryRepository = inventoryRepository;
		this.eventService = eventService;
		this.inventoryCommands = inventoryCommands;
	}

	void reserve(String sku, int qty) {
		InventoryItem item = inventoryRepository.findBySku(sku)
				.orElseThrow(() -> new RuntimeException("Item not found"));

		int available = item.getAvailable();

		if (0 >= qty) {
			throw new RuntimeException("Invalid quantity requested.");
		}

		if (qty > available) {
			throw new RuntimeException("Insufficient stock available.");
		}

		int updated = inventoryCommands.reserve(sku, qty, item.getVersion());

		if (updated == 0) {
			throw new RuntimeException("Optimistic lock failure");
		}

		DomainEventDto event = new DomainEventDto(item.getSku(), "InventoryReserved",
				"{\"sku\":\"" + sku + "\", \"qty\":" + qty + "}");

		eventService.publishEvent(event);
	}
}
