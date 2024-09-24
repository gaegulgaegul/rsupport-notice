package com.project.application.notice.error;

import org.springframework.http.HttpStatus;

import com.project.core.exception.ErrorProvider;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NoticeErrorCode implements ErrorProvider {
	DURATION(HttpStatus.BAD_REQUEST, "N401", "공지 기간이 잘못 입력 되었습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
