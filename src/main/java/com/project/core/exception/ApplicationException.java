package com.project.core.exception;

import lombok.Getter;

public class ApplicationException extends RuntimeException {
	@Getter
	private ErrorProvider throwable;

	public ApplicationException(ErrorProvider throwable) {
		this.throwable = throwable;
	}

	public ErrorProvider code() {
		return throwable;
	}

}
