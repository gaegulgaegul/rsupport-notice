package com.project.application.file.dto.response;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import lombok.Builder;

@Builder
public record FileDownloadResponse(
	MediaType contentType,
	HttpHeaders headers,
	Resource resource
) {
}
