package com.project.core.support.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.project.core.exception.ApplicationException;
import com.project.core.support.annotation.Support;

@Support
public class FileManager {

	@Value("${app.upload.file-path}")
	private String mediaFilePath;

	@Value("${app.upload.extensions}")
	private List<String> allowedExtensions;

	public List<Attachment> store(List<MultipartFile> files) {
		validate(files);

		List<Attachment> attachFiles = new ArrayList<>();
		String dirPath = "%s/%s".formatted(mediaFilePath, UUID.randomUUID().toString());
		try {
			Path path = Paths.get(dirPath);
			if (Files.notExists(path)) {
				Files.createDirectories(path);
			}
			for (MultipartFile file : files) {
				String extension = file.getOriginalFilename().split("\\.")[1];

				String physicalFilename = UUID.randomUUID().toString();
				Path filePath = Paths.get("%s/%s.%s".formatted(dirPath, physicalFilename, extension));
				Files.copy(file.getInputStream(), filePath);

				BasicFileAttributes attr = Files.readAttributes(filePath, BasicFileAttributes.class);
				attachFiles.add(toUploadFile(file, dirPath, physicalFilename, extension, attr));
			}
		} catch (IOException e) {
			throw new ApplicationException(FileErrorCode.NOT_UPLOADED_FILE);
		}
		return attachFiles;
	}

	private void validate(List<MultipartFile> files) {
		if (ObjectUtils.isEmpty(files)) {
			throw new ApplicationException(FileErrorCode.NOT_ATTACH);
		}
		if (files.stream().anyMatch(item -> !allowedExtensions.contains(item.getOriginalFilename().split("\\.")[1]))) {
			throw new ApplicationException(FileErrorCode.NOT_EXTENSION);
		}
	}

	private Attachment toUploadFile(MultipartFile file, String dirPath, String physicalFilename, String extension, BasicFileAttributes attr) {
		return Attachment.builder()
			.originalFilename(file.getOriginalFilename())
			.physicalFilename(physicalFilename)
			.contentType(file.getContentType())
			.extension(extension)
			.dirPath(dirPath)
			.fileSize(file.getSize())
			.createFileDateTime(LocalDateTime.ofInstant(attr.creationTime().toInstant(), ZoneId.of("Asia/Seoul")))
			.lastModifiedFileDateTime(LocalDateTime.ofInstant(attr.lastModifiedTime().toInstant(), ZoneId.of("Asia/Seoul")))
			.lastAccessFileDateTime(LocalDateTime.ofInstant(attr.lastAccessTime().toInstant(), ZoneId.of("Asia/Seoul")))
			.build();
	}

	public LoadFile load(String dirPath, String physicalFilename, String extension) {
		try {
			Path file = Paths.get(dirPath).resolve("%s.%s".formatted(physicalFilename, extension)).normalize();
			UrlResource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return new LoadFile(
					resource,
					Optional.ofNullable(Files.probeContentType(file)).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE)
				);
			} else {
				throw new ApplicationException(FileErrorCode.NOT_ATTACH);
			}
		} catch (IOException e) {
			throw new ApplicationException(FileErrorCode.NOT_ATTACH);
		}
	}
}
