package com.project.application.notice.endpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.project.application.account.vo.Account;
import com.project.application.notice.service.NoticeDeleter;
import com.project.core.support.annotation.Authorization;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "공지사항 API")
@RestController
@RequiredArgsConstructor
class NoticeDeleteEndpoint {
	private final NoticeDeleter noticeDeleter;

	@Authorization
	@Operation(summary = "공지사항 삭제")
	@DeleteMapping("/api/notices/{noticeId}")
	ResponseEntity<Void> deleteNotice(Account account, @PathVariable Long noticeId) {
		noticeDeleter.delete(noticeId, account);
		return ResponseEntity.noContent().build();
	}
}
