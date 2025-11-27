# Decision record

## Title: Division of responsibilities

## Status: ACCEPTED

## Context

The client requires that the system will:
   - handle reservations of inventory items (store availability, reserved items etc.)
   - store all domain events that happened in the system for future processing by other systems (e.g. analytics, reporting etc.)

## Decision

Decision is to split the responsibilities into two distinct services:

1. Inventory Service: Responsible for managing inventory items, handling reservations, and ensuring data consistency within the inventory domain.

2. Event Store Service: Dedicated to storing and managing domain events, providing an

## Consequences

- Improved maintainability: clear separation of concerns allows for easier maintenance and updates to each service without affecting the other.

- Improved reusability: other systems can leverage the event store service for event sourcing without needing to interact with the inventory service.

- Integration complexity: additional effort is required to ensure seamless communication and data consistency between the two services.

- This level of complexity is not justified at this point in time, there are not many benefits to having two separate services but this will pay off in the future when the system scales and more services need to interact with the event store.
