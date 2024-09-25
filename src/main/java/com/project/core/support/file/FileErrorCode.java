package com.project.core.support.file;

import org.springframework.http.HttpStatus;

import com.project.core.exception.ErrorProvider;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
enum FileErrorCode implements ErrorProvider {
	NOT_ATTACH(HttpStatus.BAD_REQUEST, "F401", "첨부된 파일이 없습니다."),
	NOT_EXTENSION(HttpStatus.BAD_REQUEST, "F402", "첨부된 파일의 확장자가 올바르지 않습니다."),
	NOT_UPLOADED_FILE(HttpStatus.INTERNAL_SERVER_ERROR, "F501", "예기치 못한 에러로 파일 업로드가 되지 않았습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
