package com.warehouse.app;

interface DomainEventRepository {
	Event save(Event event);
}
