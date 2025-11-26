package com.warehouse.app.inventory;

import org.springframework.http.ResponseEntity;
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
	ResponseEntity<String> reserveItem(@PathVariable String sku,
			@RequestBody ReserveItemRequestDto reserveItemDto) {

		ReserveItemDto reserveItemDtoFull = new ReserveItemDto();
		reserveItemDtoFull.setQty(reserveItemDto.getQty());
		reserveItemDtoFull.setSku(sku);

		inventoryService.reserve(reserveItemDtoFull);

		return ResponseEntity.ok().body("ok");
	}

}
