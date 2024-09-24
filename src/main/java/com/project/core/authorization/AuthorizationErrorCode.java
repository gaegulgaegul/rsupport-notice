package com.project.core.authorization;

import org.springframework.http.HttpStatus;

import com.project.core.exception.ErrorProvider;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthorizationErrorCode implements ErrorProvider {
	NO_SIGN_IN(HttpStatus.UNAUTHORIZED, "AZ001", "로그인이 필요합니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
