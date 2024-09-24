package com.project.application.notice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record NoticeFileRequest(
	@NotNull Long fileId,
	@NotEmpty String fileName
) {

}
