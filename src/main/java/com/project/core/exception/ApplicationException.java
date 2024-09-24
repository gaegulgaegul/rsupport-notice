package com.project.core.exception;

import lombok.Getter;

public class ApplicationException extends RuntimeException {
	@Getter
	private ErrorProvider throwable;

	public ApplicationException() {
		this.throwable = ErrorCode.BAD_REQUEST;
	}

	public ApplicationException(ErrorProvider throwable) {
		this.throwable = throwable;
	}

	public ErrorProvider code() {
		return throwable;
	}

}
