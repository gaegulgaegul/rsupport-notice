package com.project.application.notice.endpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.project.application.notice.service.NoticeDeleter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "공지사항 API")
@RestController
@RequiredArgsConstructor
class NoticeDeleteEndpoint {
	private final NoticeDeleter noticeDeleter;

	@Operation(summary = "공지사항 삭제")
	@DeleteMapping("/api/notices/{noticeId}")
	ResponseEntity<Void> deleteNotice(@PathVariable Long noticeId) {
		noticeDeleter.delete(noticeId);
		return ResponseEntity.noContent().build();
	}
}
