package com.project.core.support.file;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class Attachment {
	private String originalFilename;
	private String physicalFilename;
	private String contentType;
	private String extension;
	private String dirPath;
	private Long fileSize;
	private LocalDateTime createFileDateTime;
	private LocalDateTime lastModifiedFileDateTime;
	private LocalDateTime lastAccessFileDateTime;
}
