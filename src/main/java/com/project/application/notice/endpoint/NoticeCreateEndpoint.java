package com.project.application.notice.endpoint;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.application.account.vo.Account;
import com.project.application.notice.dto.request.NoticeCreateRequest;
import com.project.application.notice.dto.response.NoticeCreateResponse;
import com.project.application.notice.service.NoticeCreator;
import com.project.core.support.annotation.Authorization;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "공지사항 API")
@RestController
@RequiredArgsConstructor
class NoticeCreateEndpoint {
	private final NoticeCreator noticeCreator;

	@Authorization
	@Operation(summary = "공지사항 생성")
	@PostMapping("/api/notices")
	ResponseEntity<Void> createNotice(Account account, @RequestBody @Valid NoticeCreateRequest request) {
		NoticeCreateResponse response = noticeCreator.create(request, account);
		return ResponseEntity.created(URI.create("/api/notices" + response.noticeId())).build();
	}
}
