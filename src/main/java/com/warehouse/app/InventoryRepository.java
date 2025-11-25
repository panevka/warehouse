package com.warehouse.app;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface InventoryRepository extends CrudRepository<InventoryItem, String> {
	Optional<InventoryItem> findBySku(String sku);
}
