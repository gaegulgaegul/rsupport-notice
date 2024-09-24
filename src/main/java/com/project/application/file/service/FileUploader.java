package com.project.application.file.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.application.file.domain.AttachFileEntity;
import com.project.application.file.domain.AttachFileRepository;
import com.project.application.file.dto.request.FileUploadRequest;
import com.project.application.file.dto.response.FileUploadResponse;
import com.project.core.file.Attachment;
import com.project.core.file.FileManager;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileUploader {
	private final AttachFileRepository attachFileRepository;
	private final FileManager fileManager;

	public List<FileUploadResponse> upload(FileUploadRequest request) {
		fileManager.validate(request.files());

		List<AttachFileEntity> attachFiles = toAttachFiles(fileManager.store(request.files()));
		attachFileRepository.saveAll(attachFiles);
		return toResponse(attachFiles);
	}

	private List<FileUploadResponse> toResponse(List<AttachFileEntity> attachFiles) {
		return attachFiles.stream()
			.map(item -> new FileUploadResponse(item.getId(), item.getOriginalFilename()))
			.toList();
	}

	private List<AttachFileEntity> toAttachFiles(List<Attachment> attachments) {
		return attachments.stream()
			.map(item -> AttachFileEntity.builder()
				.originalFilename(item.getOriginalFilename())
				.physicalFilename(item.getPhysicalFilename())
				.contentType(item.getContentType())
				.extension(item.getExtension())
				.dirPath(item.getDirPath())
				.fileSize(item.getFileSize())
				.createFileDateTime(item.getCreateFileDateTime())
				.lastModifiedFileDateTime(item.getLastModifiedFileDateTime())
				.lastAccessFileDateTime(item.getLastAccessFileDateTime())
				.build())
			.toList();
	}
}
