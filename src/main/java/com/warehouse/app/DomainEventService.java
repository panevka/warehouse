package com.warehouse.app;

import java.time.Instant;

import org.springframework.stereotype.Service;

@Service
class DomainEventService {

	DomainEventRepository domainEventRepository;

	public DomainEventService(DomainEventRepository domainEventRepository) {
		this.domainEventRepository = domainEventRepository;
	}

	public void publishEvent(DomainEventDto event) {
		Instant now = Instant.now();
		domainEventRepository
				.save(new Event(event.getSku(), event.getType(), event.getPayload(), now.toString()));
	}
}
