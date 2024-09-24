package com.project.application.account.error;

import org.springframework.http.HttpStatus;

import com.project.core.exception.ErrorProvider;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountErrorCode implements ErrorProvider {
	NO_SIGN_IN(HttpStatus.NO_CONTENT, "A001", "이메일 또는 비밀번호가 잘못되었습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
