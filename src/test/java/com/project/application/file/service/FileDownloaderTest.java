package com.project.application.file.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.project.application.file.dto.request.FileUploadRequest;
import com.project.application.file.dto.response.FileDownloadResponse;
import com.project.application.file.dto.response.FileUploadResponse;
import com.project.application.file.usecase.ActiveAttachFiles;
import com.project.application.file.usecase.DeactivateAttachFiles;
import com.project.core.exception.ApplicationException;

@DisplayName("로그인 기능 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest
class FileDownloaderTest {
	@Autowired private FileUploader fileUploader;
	@Autowired private ActiveAttachFiles activeAttachFiles;
	@Autowired private DeactivateAttachFiles deactivateAttachFiles;

	@Autowired private FileDownloader sut;

	@AfterAll
	static void afterAll(@Value("${app.upload.file-path}") String uploadFilePath) throws IOException {
		if (uploadFilePath == null) return;

		Path path = Paths.get(uploadFilePath);
		if (Files.notExists(path)) return;

		Files.walk(path)
			.sorted(Comparator.reverseOrder())
			.map(Path::toFile)
			.forEach(File::delete);
	}

	@Test
	void 활성되진_않았지만_삭제되지_않은_파일은_다운로드_할_수_있다() {
		Long fileId = upload();

		FileDownloadResponse response = sut.download(fileId);
		assertThat(response.headers()).containsKey("Content-Disposition");
		assertThat(response.resource()).isNotNull();
	}

	@Test
	void 활성된_파일은_다운로드_할_수_있다() {
		Long fileId = upload();

		activeAttachFiles.active(List.of(fileId));

		FileDownloadResponse response = sut.download(fileId);
		assertThat(response.headers()).containsKey("Content-Disposition");
		assertThat(response.resource()).isNotNull();
	}

	@Test
	void 삭제된_파일을_다운로드하면_예외발생() {
		Long fileId = upload();

		activeAttachFiles.active(List.of(fileId));
		deactivateAttachFiles.deactivate(List.of(fileId));

		assertThatThrownBy(() -> sut.download(fileId))
			.isInstanceOf(ApplicationException.class);
	}

	private Long upload() {
		List<MultipartFile> files = List.of(
			new MockMultipartFile(
				"files",
				"sample.txt",
				MediaType.TEXT_PLAIN_VALUE,
				"file upload except test".getBytes()
			)
		);
		List<FileUploadResponse> response = fileUploader.upload(new FileUploadRequest(files));
		return response.get(0).fileId();
	}
}