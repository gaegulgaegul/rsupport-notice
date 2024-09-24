package com.project.core.file;

import org.springframework.http.HttpStatus;

import com.project.core.exception.ErrorProvider;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
enum FileErrorCode implements ErrorProvider {
	NOT_UPLOAD_FILE(HttpStatus.INTERNAL_SERVER_ERROR, "E501", "예기치 못한 에러로 파일 업로드가 되지 않았습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
