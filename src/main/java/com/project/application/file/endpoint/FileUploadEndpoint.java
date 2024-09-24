package com.project.application.file.endpoint;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.application.file.service.FileUploader;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "파일 API")
@RestController
@RequiredArgsConstructor
class FileUploadEndpoint {
	private final FileUploader fileUploader;

	@Operation(summary = "파일 업로드")
	@PostMapping("/api/files")
	ResponseEntity<?> uploadFiles(@RequestParam List<MultipartFile> files) {
		return ResponseEntity.ok(fileUploader.upload(files));
	}
}
