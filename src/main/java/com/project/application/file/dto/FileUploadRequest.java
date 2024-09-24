package com.project.application.file.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;

public record FileUploadRequest(
	@Schema(description = "첨부 파일 목록")
	List<MultipartFile> files
) {

}
