package com.project.application.notice.endpoint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.application.notice.dto.response.NoticeSearchResponse;
import com.project.application.notice.service.NoticeSearcher;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "공지사항 API")
@RestController
@RequiredArgsConstructor
class NoticeSearchEndpoint {
	private final NoticeSearcher noticeSearcher;

	@Operation(
		summary = "공지사항 목록 조회",
		parameters = {
			@Parameter(name = "page", in = ParameterIn.QUERY, description = "페이지 순번", example = "0"),
			@Parameter(name = "size", in = ParameterIn.QUERY, description = "페이지 사이즈", example = "10"),
			@Parameter(name = "sort", in = ParameterIn.QUERY, description = "정렬 조건", example = "createdAt,desc")
		}
	)
	@GetMapping("/api/notices")
	ResponseEntity<Page<NoticeSearchResponse>> searchNotice(Pageable pageable) {
		return ResponseEntity.ok(noticeSearcher.read(pageable));
	}
}
