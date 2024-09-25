package com.project.application.notice.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record NoticeModifyRequest(
	@NotEmpty
	@Schema(description = "제목", example = "2024년 하반기 재공지")
	String title,
	@NotEmpty
	@Schema(description = "내용", example = "보안 준수")
	String content,
	@NotNull
	@Schema(description = "공지 시작일시", example = "2024-09-01T00:00:00")
	LocalDateTime from,
	@NotNull
	@Schema(description = "공지 종료일시", example = "2024-12-31T00:00:00")
	LocalDateTime to,
	@Valid
	@Schema(description = "첨부 파일 ID 목록")
	List<Long> fileIds
) {

}
