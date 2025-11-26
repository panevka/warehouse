package com.warehouse.app.inventory;

public class ConcurrentUpdatesException extends RuntimeException {
	public ConcurrentUpdatesException() {
		super("Concurrent updates detected");
	}

	public ConcurrentUpdatesException(String message) {
		super(message);
	}

	public ConcurrentUpdatesException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConcurrentUpdatesException(Throwable cause) {
		super(cause);
	}
}
