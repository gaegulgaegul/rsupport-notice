package com.project.application.file.endpoint;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "파일 API")
@RestController
@RequiredArgsConstructor
class FileDownloadEndpoint {
	private final FileDownloader fileDownloader;

	@Operation(summary = "파일 다운로드")
	@PostMapping("/api/files/{fileId}")
	ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
		return ResponseEntity.ok(fileDownloader.download(fileId));
	}
}
