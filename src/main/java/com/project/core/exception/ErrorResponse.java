package com.project.core.exception;

record ErrorResponse(
	int status,
	String code,
	String message
) {
	public static ErrorResponse of(ErrorProvider e) {
		return new ErrorResponse(e.getStatus().value(), e.getCode(), e.getMessage());
	}
}
