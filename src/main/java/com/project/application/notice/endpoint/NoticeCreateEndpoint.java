package com.project.application.notice.endpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "공지사항 API")
@RestController
@RequiredArgsConstructor
class NoticeCreateEndpoint {
	private final NoticeCreator noticeCreator;

	@Operation(summary = "공지사항 생성")
	@PostMapping("/api/notices")
	ResponseEntity<?> createNotice() {
		return ResponseEntity.ok(noticeCreator.create());
	}
}
