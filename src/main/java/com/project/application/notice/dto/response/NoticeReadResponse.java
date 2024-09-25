package com.project.application.notice.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Builder;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
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
) implements Serializable {
	public static final Long serialVersionUID = 1L;
}
