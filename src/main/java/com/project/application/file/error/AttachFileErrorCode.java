package com.project.application.file.error;

import org.springframework.http.HttpStatus;

import com.project.core.exception.ErrorProvider;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AttachFileErrorCode implements ErrorProvider {
	NO_CONTENT(HttpStatus.NO_CONTENT, "AF001", "해당하는 정보가 없습니다."),
	NO_OWNER(HttpStatus.UNAUTHORIZED, "AF002", "파일 소유자가 아닙니다."),
	;

	private final HttpStatus status;
	private final String code;
	private final String message;
}
