
package com.warehouse.app.inventory.commands;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.warehouse.cqs.CommandHandler;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import jakarta.transaction.Transactional;

@Component
public class ReserveItemCommandHandler implements CommandHandler<ReserveItemResult, ReserveItemCommand> {

	private final JdbcTemplate jdbcTemplate;
	private static final String UPDATE_SQL = """
			    UPDATE inventory_item
			    SET available = available - ?,
			        reserved = reserved + ?,
			        version = version + 1
			    WHERE sku = ? AND version = ?
			""";

	public ReserveItemCommandHandler(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public ReserveItemResult handle(ReserveItemCommand command) {
		int entitiesUpdated = jdbcTemplate.update(UPDATE_SQL,
				command.getQty(),
				command.getQty(),
				command.getSku(),
				command.getExpectedVersion());

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
