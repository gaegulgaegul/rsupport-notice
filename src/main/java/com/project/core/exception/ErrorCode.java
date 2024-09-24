package com.project.core.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
enum ErrorCode implements ErrorProvider {
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "E400", "잘못된 요청이 있습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E500", "예기치 못한 에러가 발생했습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
