package com.project.core.exception;

import org.springframework.http.HttpStatus;

public interface ErrorProvider {

	HttpStatus getStatus();

	String getCode();

	String getMessage();
}
