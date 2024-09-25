package com.project.core.support.crypto;

import org.springframework.http.HttpStatus;

import com.project.core.exception.ErrorProvider;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
enum CryptoErrorCode implements ErrorProvider {
	NOT_ENCRYPT(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "암호화 되지 않았습니다."),
	NOT_DECRYPT(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "복호화 되지 않았습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
