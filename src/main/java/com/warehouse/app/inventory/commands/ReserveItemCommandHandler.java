
package com.warehouse.app.inventory.commands;

import org.springframework.stereotype.Component;

import com.warehouse.cqs.CommandHandler;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Component
public class ReserveItemCommandHandler implements CommandHandler<ReserveItemResult, ReserveItemCommand> {

	@PersistenceContext
	private EntityManager em;

	public ReserveItemResult handle(ReserveItemCommand command) {
		var entitiesUpdated = em.createQuery("""
				    UPDATE InventoryItem i
				    SET i.available = i.available - :qty,
				        i.reserved = i.reserved + :qty,
				        i.version = i.version + 1
				    WHERE i.sku = :sku AND i.version = :version
				""")
				.setParameter("qty", command.getQty())
				.setParameter("sku", command.getSku())
				.setParameter("version", command.getExpectedVersion())
				.executeUpdate();
		em.clear();

		if (entitiesUpdated == 1) {
			var result = new ReserveItemResult();
			result.setSuccess(true);
			return result;
		} else {
			var result = new ReserveItemResult();
			return result;
		}

	}
}
