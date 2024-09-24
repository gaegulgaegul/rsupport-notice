package com.project.application.notice.endpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.application.notice.service.NoticeSearcher;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "공지사항 API")
@RestController
@RequiredArgsConstructor
class NoticeSearchEndpoint {
	private final NoticeSearcher noticeSearcher;

	@Operation(summary = "공지사항 목록 조회")
	@GetMapping("/api/notices")
	ResponseEntity<?> searchNotice() {
		return ResponseEntity.ok(noticeSearcher.read());
	}
}
