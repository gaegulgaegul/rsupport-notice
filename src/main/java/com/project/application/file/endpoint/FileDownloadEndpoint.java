package com.project.application.file.endpoint;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.application.file.dto.response.FileDownloadResponse;
import com.project.application.file.service.FileDownloader;
import com.project.core.authorization.Authorization;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "파일 API")
@RestController
@RequiredArgsConstructor
class FileDownloadEndpoint {
	private final FileDownloader fileDownloader;

	@Authorization
	@Operation(summary = "파일 다운로드")
	@PostMapping("/api/files/{fileId}")
	ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
		FileDownloadResponse response = fileDownloader.download(fileId);
		return ResponseEntity.ok()
			.contentType(response.contentType())
			.headers(response.headers())
			.body(response.resource());
	}
}
