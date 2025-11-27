## What this is

A tiny Spring Boot service that lets you reserve stock for items while handling concurrent updates and writing immutable domain events to a separate table.

It implements the rules below, constraints given by the external party requesting the service.

* `InventoryItem` has `sku`, `available`, `reserved`, `version` (manual).
* `POST /inventory/{sku}/reserve` accepts `{ "qty": N }`.
* Reserve succeeds only if `qty > 0` and `available >= qty`.
* Update is done with a custom SQL compare-and-swap `WHERE sku = :sku AND version = :version`.
* Retry up to 3 times on version mismatch, otherwise return `409 CONFLICT`.
* Domain events are persisted in a separate immutable table (no Spring `ApplicationEventPublisher`).

Project files live in the repo root. ADRs are under: `./docs/adr` (read the decision records there).

## Setup (exact steps)

1. JDK: install Java 21.
2. Build tool: Maven (repo includes Maven wrapper).

   * Run with the wrapper:
     `./mvnw clean package` (Linux/macOS) or `mvnw.cmd clean package` (Windows)
3. Database: PostgreSQL (the app is configured for Postgres in `application.yaml`).

   * Default connection in `src/main/resources/application.yaml`:

     ```
     jdbc:postgresql://localhost:5432/warehouse_db
     username: postgres
     password: postgres
     ```
   * Create DB and user or adjust the `application.yaml`.
4. Migrations: apply migrations from `./migrations/` to create the required tables.
5. Run:

   ```
   ./mvnw spring-boot:run
   ```

   or run the packaged jar:

   ```
   java -jar target/app-*.jar
   ```

## How to call the service — example

Reserve 5 units of SKU `ABC123`:

```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"qty": 5}' \
  http://localhost:8080/inventory/ABC123/reserve
```

Responses:

* `200 OK` and body `ok` — reservation succeeded.
* `409 CONFLICT` — concurrent modification after 3 retries.
* `400/500` — invalid request or server error (insufficient stock -> 500 currently, see notes).


## Core functionality and flow (step-by-step)

1. HTTP `POST /inventory/{sku}/reserve` with `{ "qty": N }`.
2. Controller builds `ReserveItemDto` and calls `InventoryService.reserve`.
3. `InventoryService.reserve`:

   * validates qty > 0.
   * reads the current `InventoryItem` from the DB (via `InventoryRepository.findBySku`).
   * if `qty > available` -> throw (insufficient stock).
   * create a `ReserveItemCommand` with `expectedVersion = item.version`.
   * call `ReserveItemCommandHandler.handle(command)`.
4. `ReserveItemCommandHandler.handle`:

   * executes single-row `UPDATE ... WHERE sku = ? AND version = ?`.
   * if `update` returned `1`: success.
   * if `0`: version mismatch (someone else updated first).
5. Back in `InventoryService`, if `success`:

   * create `DomainEventDto` and call `DomainEventService.publishEvent` to persist event row.
   * return `200 OK`.
6. If `update` failed, the service re-reads the item and retries up to 3 attempts; after 3 failures returns `409`.

This is a read-then-conditional-write pattern using SQL CAS, with retries in the service layer.

## ADRs

All decision records are in: `./docs/adr`
Notable ones:

* `001-division-of-responsibilities.md`
* `002-inventory-item-primary-key.md`
* `003-placement-of-retry-logic-for-concurrent-resource-modifications.md`
* `004-coordination-with-event-publisher.md`
