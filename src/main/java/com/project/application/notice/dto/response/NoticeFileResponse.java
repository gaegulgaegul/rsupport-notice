package com.project.application.notice.dto.response;

import lombok.Builder;

@Builder
public record NoticeFileResponse(
	Long fileId,
	String filename
) {

}
