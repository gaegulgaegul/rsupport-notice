package com.project.application.notice.endpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "공지사항 API")
@RestController
@RequiredArgsConstructor
class NoticeReadEndpoint {

	private final NoticeReader noticeReader;

	@Operation(summary = "공지사항 조회")
	@GetMapping("/api/notices/{noticeId}")
	ResponseEntity<?> readNotice(@PathVariable Long noticeId) {
		return ResponseEntity.ok(noticeReader.read(noticeId));
	}
}
