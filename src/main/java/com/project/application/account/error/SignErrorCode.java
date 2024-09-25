package com.project.application.account.error;

import org.springframework.http.HttpStatus;

import com.project.core.exception.ErrorProvider;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SignErrorCode implements ErrorProvider {
	INVALID(HttpStatus.BAD_REQUEST, "S001", "이메일 또는 비밀번호가 잘못되었습니다."),
	NO_SESSION(HttpStatus.INTERNAL_SERVER_ERROR, "S002", "서버와 연결이 끊겼습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
