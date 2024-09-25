package com.project.application.notice.endpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.application.account.vo.Account;
import com.project.application.notice.dto.request.NoticeModifyRequest;
import com.project.application.notice.service.NoticeModifier;
import com.project.core.support.annotation.Authorization;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "공지사항 API")
@RestController
@RequiredArgsConstructor
class NoticeModifyEndpoint {
	private final NoticeModifier noticeModifier;

	@Authorization
	@Operation(summary = "공지사항 수정")
	@PutMapping("/api/notices/{noticeId}")
	ResponseEntity<Void> modifyNotice(Account account, @PathVariable Long noticeId, @RequestBody @Valid NoticeModifyRequest request) {
		noticeModifier.modify(noticeId, request, account);
		return ResponseEntity.ok().build();
	}
}
