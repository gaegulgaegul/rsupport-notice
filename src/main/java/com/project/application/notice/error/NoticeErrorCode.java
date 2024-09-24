package com.project.application.notice.error;

import org.springframework.http.HttpStatus;

import com.project.core.exception.ErrorProvider;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NoticeErrorCode implements ErrorProvider {
	NO_CONTENT(HttpStatus.NO_CONTENT, "N411", "해당하는 정보가 없습니다."),

	DURATION(HttpStatus.BAD_REQUEST, "N431", "공지 기간이 잘못 입력 되었습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
