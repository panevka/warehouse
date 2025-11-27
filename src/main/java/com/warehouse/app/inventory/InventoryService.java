package com.warehouse.app.inventory;

import org.springframework.stereotype.Service;

import com.warehouse.app.event.DomainEventDto;
import com.warehouse.app.event.DomainEventService;
import com.warehouse.app.event.EventType;
import com.warehouse.app.inventory.commands.ReserveItemCommand;
import com.warehouse.app.inventory.commands.ReserveItemCommandHandler;

import jakarta.transaction.Transactional;

@Service
class InventoryService {
	InventoryRepository inventoryRepository;
	DomainEventService eventService;
	ReserveItemCommandHandler reserveItemCommandHandler;

	InventoryService(InventoryRepository inventoryRepository, DomainEventService eventService,
			ReserveItemCommandHandler reserveItemCommandHandler) {
		this.inventoryRepository = inventoryRepository;
		this.eventService = eventService;
		this.reserveItemCommandHandler = reserveItemCommandHandler;
	}

	@Transactional // transaction is not used for retry & optimistic locking, but for consistency
			// with event publishing
	void reserve(ReserveItemDto dto) throws ConcurrentUpdatesException {

		if (0 >= dto.getQty()) {
			throw new RuntimeException("Invalid quantity requested.");
		}

		for (int i = 0; i < 3; i++) {

			InventoryItem item = inventoryRepository.findBySku(dto.getSku())
					.orElseThrow(() -> new RuntimeException("Item not found"));

			var command = new ReserveItemCommand();
			command.setSku(dto.getSku());
			command.setQty(dto.getQty());
			command.setExpectedVersion(item.getVersion());

			if (dto.getQty() > item.getAvailable()) {
				throw new RuntimeException("Insufficient stock available.");
			}

			// also works without @Transactional & is atomic
			var result = reserveItemCommandHandler.handle(command);
			boolean success = result.isSuccess();

			if (!success)
				continue;

			DomainEventDto event = new DomainEventDto(item.getSku(), EventType.ITEM_RESERVED,
					"{\"sku\":\"" + dto.getSku() + "\", \"qty\":" + dto.getQty() + "}");

			eventService.publishEvent(event);

			return;
		}

		throw new ConcurrentUpdatesException();
	}

}
