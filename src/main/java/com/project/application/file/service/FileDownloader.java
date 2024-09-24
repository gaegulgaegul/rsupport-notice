package com.project.application.file.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.project.application.file.domain.AttachFileEntity;
import com.project.application.file.domain.AttachFileRepository;
import com.project.application.file.dto.response.FileDownloadResponse;
import com.project.application.file.error.AttachFileErrorCode;
import com.project.core.exception.ApplicationException;
import com.project.core.file.FileManager;
import com.project.core.file.LoadFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileDownloader {
	private final AttachFileRepository attachFileRepository;
	private final FileManager fileManager;

	public FileDownloadResponse download(Long fileId) {
		AttachFileEntity attachFile = attachFileRepository.findById(fileId)
			.orElseThrow(() -> new ApplicationException(AttachFileErrorCode.NO_CONTENT));

		LoadFile loadFile = fileManager.load(attachFile.getDirPath(), attachFile.getPhysicalFilename(), attachFile.getExtension());

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"%s\"".formatted(attachFile.getOriginalFilename()));
		headers.add(HttpHeaders.TRANSFER_ENCODING, "binary");

		return FileDownloadResponse.builder()
			.contentType(MediaType.parseMediaType(loadFile.getContentType()))
			.headers(headers)
			.resource(loadFile.getResource())
			.build();
	}
}
