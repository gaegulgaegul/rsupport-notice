package com.project.application.notice.endpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.project.application.account.vo.Account;
import com.project.application.notice.dto.response.NoticeReadResponse;
import com.project.application.notice.service.NoticeReader;
import com.project.core.support.annotation.Authorization;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "공지사항 API")
@RestController
@RequiredArgsConstructor
class NoticeReadEndpoint {
	private final NoticeReader noticeReader;

	@Authorization
	@Operation(summary = "공지사항 조회")
	@GetMapping("/api/notices/{noticeId}")
	ResponseEntity<NoticeReadResponse> readNotice(@Parameter(hidden = true) Account account, @PathVariable Long noticeId) {
		return ResponseEntity.ok(noticeReader.read(noticeId, account));
	}
}
