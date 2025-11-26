package com.warehouse.app.inventory;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class InventoryCommandsJpa implements InventoryCommands {

	@PersistenceContext
	private EntityManager em;

	@Override
	public int reserve(String sku, int qty, long expectedVersion) {
		return em.createQuery("""
				    UPDATE InventoryItem i
				    SET i.available = i.available - :qty,
				        i.reserved = i.reserved + :qty,
				        i.version = i.version + 1
				    WHERE i.sku = :sku AND i.version = :version
				""")
				.setParameter("qty", qty)
				.setParameter("sku", sku)
				.setParameter("version", expectedVersion)
				.executeUpdate();
	}
}
