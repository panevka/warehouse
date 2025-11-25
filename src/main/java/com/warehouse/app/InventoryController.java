package com.warehouse.app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/inventory")
class InventoryController {

	InventoryService inventoryService;

	public InventoryController(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	@RequestMapping("/{sku}/reserve")
	void reserveItem(@PathVariable String sku, @RequestBody ReserveItemDto dto) {
		int quantity = dto.getQty();
		inventoryService.reserve(sku, quantity);
	}

}
