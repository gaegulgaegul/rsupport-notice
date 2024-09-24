package com.project.application.notice.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import com.project.application.notice.dto.NoticeFileDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record NoticeCreateRequest(
	@NotEmpty
	@Schema(description = "제목", example = "2024년 하반기 공지사항")
	String title,
	@NotEmpty
	@Schema(description = "내용", example = "출퇴근 기록 준수")
	String content,
	@NotNull
	@Schema(description = "공지 시작일시", example = "2024-07-01 00:00:00")
	LocalDateTime startDateTime,
	@NotNull
	@Schema(description = "공지 종료일시", example = "2024-12-31 00:00:00")
	LocalDateTime endDateTime,
	@Valid List<NoticeFileDTO> files
) {

}
