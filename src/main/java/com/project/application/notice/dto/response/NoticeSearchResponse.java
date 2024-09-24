package com.project.application.notice.dto.response;

import java.time.LocalDateTime;

public record NoticeSearchResponse(
	Long noticeId,
	String title,
	String content,
	LocalDateTime createdAt,
	Long viewCount,
	String authorName
) {
}
