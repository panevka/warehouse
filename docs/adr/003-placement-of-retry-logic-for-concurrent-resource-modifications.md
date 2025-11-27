# Decision record


## Title: Placement of retry logic for concurrent resource modifications

## Status: ACCEPTED

## Context

The client requires the ability to handle concurrent modifications to resources such as inventory item. 
When multiple clients attempt to modify the same resource simultaneously, conflicts may arise, leading to failed operations. 
To ensure reliability, it is essential to implement retry logic that can gracefully handle these conflicts.

This decision record explores two primary options for placing the retry logic:
- Service Layer Retry Logic: Implementing retry logic within the service layer that interacts with the data store.
- Repository Layer Retry Logic: Implementing retry logic within the repository layer that directly communicates with the database.

## Decision

After evaluating the options, it was decided that the retry logic for handling concurrent resource modifications will be implemented in the Service Layer.

## Consequences

- Putting retry logic in the service prevents from tying repository  to a particular business flow. This would break the separation of concerns. When other services use the same repository, they may not want the same retry behavior.

- Repository should ideally fail fast if an update conflicts (throw OptimisticLockingException or equivalent). The service layer can then decide how to handle that failure, including whether to retry.

- Service layer has better context about the business operation being performed, allowing it to implement more intelligent retry strategies e.g. exponential backoff, maximum retry attempts.
