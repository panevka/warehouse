package com.warehouse.app;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface JpaInventoryRepository extends CrudRepository<InventoryItem, String>, InventoryRepository {
	Optional<InventoryItem> findBySku(String sku);
}
