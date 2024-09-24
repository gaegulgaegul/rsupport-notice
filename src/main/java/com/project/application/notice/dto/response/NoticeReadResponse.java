package com.project.application.notice.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

@Builder
public record NoticeReadResponse(
	Long noticeId,
	String title,
	String content,
	Integer viewCount,
	LocalDateTime createdAt,
	Long createdBy,
	LocalDateTime lastModifiedAt,
	Long lastModifiedBy,
	List<NoticeFileResponse> files
) {
}
