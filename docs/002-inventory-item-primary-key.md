
# Decision record

## Title: Inventory Item Primary Key

## Status: ACCEPTED

## Context

The client requires that the system will store an Inventory Item entity with a unique identifier Sku (Stock Keeping Unit). The Sku is a string that uniquely identifies each inventory item in the business domain.

## Decision

Decision is to use an auto-generated integer Id as the primary key for the Inventory Item entity in the database. The sku will not serve as a primary key in the database, but rather as a unique business identifier.

## Consequences

- Improved performance: faster reads and writes due to the efficiency of integer primary keys.
- Simplified database schema: easier to manage relationships and foreign keys with integer primary keys.
- Potential for data inconsistency: additional logic is required to ensure that the SKU remains unique and in sync with the Inventory Item entity.
- Increased complexity in application logic: the application must handle the mapping between the integer Id and the SKU for business operations.
