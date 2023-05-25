package com.example.ec.exceptions;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ResourceExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationError> validation(MethodArgumentNotValidException e, HttpServletRequest request) {

		var err = new ValidationError(System.currentTimeMillis(), HttpStatus.UNPROCESSABLE_ENTITY.value(),
				"Validation error", "Formulário inválido", request.getRequestURI());
		e.getBindingResult().getFieldErrors()
				.forEach(x -> err.addError(x.getField(), x.getDefaultMessage()));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
	}

	@ExceptionHandler(ObjectNotFoundException.class)
	public ResponseEntity<StandardError> objectNotFound(ObjectNotFoundException e, HttpServletRequest request) {
		var err = StandardError.builder()
				.timestamp(System.currentTimeMillis())
				.status(HttpStatus.NOT_FOUND.value())
				.error("Not found")
				.message(e.getMessage())
				.path(request.getRequestURI())
				.build();
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
	}

	@ExceptionHandler(QuantityException.class)
	public ResponseEntity<StandardError> quantityException(QuantityException e, HttpServletRequest request) {
		var err = StandardError.builder()
				.timestamp(System.currentTimeMillis())
				.status(HttpStatus.BAD_REQUEST.value())
				.error("Bad Request")
				.message(e.getMessage())
				.path(request.getRequestURI())
				.build();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
	}
}
