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

---

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

---

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

---

## Code layout and relationships (concrete)

Files and responsibilities (follow the code):

* `com.warehouse.app.AppApplication`
  Standard Spring Boot entry point.

* **Controller**

  * `com.warehouse.app.inventory.InventoryController`
    Exposes `POST /inventory/{sku}/reserve`. Converts HTTP body to DTO and calls `InventoryService.reserve`.

* **Service**

  * `com.warehouse.app.inventory.InventoryService`
    Orchestrates the reserve operation:

    1. Loads the `InventoryItem` from `InventoryRepository`.
    2. Validates `qty` and `available`.
    3. Builds `ReserveItemCommand` with `expectedVersion` = item.version.
    4. Calls `ReserveItemCommandHandler.handle(...)` which performs the atomic SQL update.
    5. If the update `UPDATE ... WHERE version = :version` reports `1` row updated, it publishes a domain event via `DomainEventService`.
    6. If update failed (version mismatch), it retries up to 3 times. After 3 failed attempts it throws `ConcurrentUpdatesException`.

  Note: `InventoryService.reserve` is annotated `@Transactional` to ensure the inventory update and the domain event save happen in a single transaction (so event is not persisted unless update succeeded). The retry loop is outside the repository and re-reads the row each attempt.

* **Repository**

  * `com.warehouse.app.inventory.InventoryRepository`
    A Spring Data `CrudRepository` used to read `InventoryItem` by SKU. It does **not** handle optimistic locking or retries.

* **Command handler (CAS)**

  * `com.warehouse.app.inventory.commands.ReserveItemCommandHandler`
    Uses `JdbcTemplate` to run an `UPDATE inventory_item SET available = available - ?, reserved = reserved + ?, version = version + 1 WHERE sku = ? AND version = ?`.
    Returns success if `jdbcTemplate.update(...) == 1`. This is the compare-and-swap (CAS).

* **Events**

  * `com.warehouse.app.event.DomainEventService` and `com.warehouse.app.event.JpaDomainEventRepository` (`Event` JPA entity)
    `DomainEventService.publishEvent` creates an `Event` entity and saves it via JPA. The `event` table stores immutable domain events with `payload` as JSON, `type`, `sku`, and `created_at`.

* **Error handling**

  * `com.warehouse.app.common.GlobalExceptionHandler` maps `ConcurrentUpdatesException` to `409 Conflict`.

---

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

---

## Important implementation points (explicit)

* **Version field** is manually managed (`long version` on `InventoryItem`). No `@Version`, no JPA optimistic locking.
* **CAS** is implemented with a single SQL `UPDATE ... WHERE sku = :sku AND version = :version`. This avoids explicit DB locks.

---

## Where to look in the code (quick pointers)

* Controller: `src/main/java/com/warehouse/app/inventory/InventoryController.java`
* Service + retry: `src/main/java/com/warehouse/app/inventory/InventoryService.java`
* CAS SQL: `src/main/java/com/warehouse/app/inventory/commands/ReserveItemCommandHandler.java`
* Domain event: `src/main/java/com/warehouse/app/event/*`
* Exception mapping (409): `src/main/java/com/warehouse/app/common/GlobalExceptionHandler.java`
* ADRs: `./docs/adr` (contains design decisions and reasoning)

---

## ADRs

All decision records are in: `./docs/adr`
Notable ones:

* `001-division-of-responsibilities.md`
* `002-inventory-item-primary-key.md`
* `003-placement-of-retry-logic-for-concurrent-resource-modifications.md`
* `004-coordination-with-event-publisher.md`
