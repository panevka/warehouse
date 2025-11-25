package com.warehouse.app.event;

interface DomainEventRepository {
	Event save(Event event);
}
