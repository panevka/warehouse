package com.warehouse.app.event;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface JpaDomainEventRepository extends CrudRepository<Event, Long>, DomainEventRepository {
}
