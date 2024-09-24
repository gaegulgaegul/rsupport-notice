package com.project.core.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
class ExceptionAdvice {

	@ExceptionHandler(ApplicationException.class)
	ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException e) {
		return ResponseEntity.badRequest()
			.body(ErrorResponse.of(e.code()));
	}

	@ExceptionHandler(RuntimeException.class)
	ResponseEntity<?> handleRunTimeException(RuntimeException e) {
		log.error(e.getMessage());
		return ResponseEntity.badRequest()
			.body(ErrorResponse.of(ErrorCode.BAD_REQUEST));
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<?> handleException(Exception e) {
		log.error(e.getMessage());
		return ResponseEntity.internalServerError()
			.body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
	}

}
