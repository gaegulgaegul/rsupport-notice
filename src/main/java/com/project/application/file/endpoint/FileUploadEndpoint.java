package com.project.application.file.endpoint;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.application.file.dto.request.FileUploadRequest;
import com.project.application.file.dto.response.FileUploadResponse;
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
	@PostMapping(value = "/api/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	ResponseEntity<List<FileUploadResponse>> uploadFiles(@ModelAttribute FileUploadRequest request) {
		return ResponseEntity.ok(fileUploader.upload(request));
	}
}
