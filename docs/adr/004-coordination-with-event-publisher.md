# Decision record


## Title: Coordination with event publisher

## Status: ACCEPTED

## Context

The client requires the ability to dipatch actions with an event publishing system.

In this case, when an inventory item is reserved, an ItemReservedEvent must be published. The challenge lies in ensuring that the event is published only if the reservation operation is successful, and that the event is not lost in case of failures.

- @Transactional was prohibited for pesimistic locking for concurrent update of inventory item but it can be used here to ensure atomicity between inventory update and event publishing.

## Decision

- Event publishing & inventory update should be done in a single transaction to ensure atomicity. If the inventory update fails, the event should not be published.

## Consequences

- Using @Transactional ensures that both the inventory update and event publishing are treated as a single atomic operation. If either operation fails, the entire transaction is rolled back, maintaining data consistency.

- Operation could be hardcoded to SQL query but @Transactional is more readable and maintainable. Allows decoupling of inventory persitence from event publishing mechanism.

