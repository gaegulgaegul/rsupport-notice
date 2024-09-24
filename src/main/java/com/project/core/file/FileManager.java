package com.project.core.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import com.project.core.exception.ApplicationException;
import com.project.core.support.annotation.Support;

@Support
public class FileManager {

	@Value("${app.upload.file-path}")
	private String mediaFilePath;

	public List<Attachment> store(List<MultipartFile> files) {
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
			throw new ApplicationException(FileErrorCode.NOT_UPLOAD_FILE);
		}
		return attachFiles;
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
}
