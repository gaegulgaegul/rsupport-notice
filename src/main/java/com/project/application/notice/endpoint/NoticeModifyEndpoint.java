package com.project.application.notice.endpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "공지사항 API")
@RestController
@RequiredArgsConstructor
class NoticeModifyEndpoint {
	private final NoticeModifier noticeModifier;

	@Operation(summary = "공지사항 수정")
	@PutMapping("/api/notices/{noticeId}")
	ResponseEntity<?> modifyNotice(@PathVariable Long noticeId) {
		return ResponseEntity.ok(noticeModifier.modify());
	}
}
