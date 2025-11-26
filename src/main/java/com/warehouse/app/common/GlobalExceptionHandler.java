package com.warehouse.app.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.warehouse.app.inventory.ConcurrentUpdatesException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({ ConcurrentUpdatesException.class })
	public ResponseEntity<Object> handleConcurrentUpdatesException(ConcurrentUpdatesException exception) {
		return ResponseEntity
				.status(HttpStatus.CONFLICT)
				.body(exception.getMessage());
	}

	@ExceptionHandler({ RuntimeException.class })
	public ResponseEntity<Object> handleRuntimeException(RuntimeException exception) {
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(exception.getMessage());
	}
}
