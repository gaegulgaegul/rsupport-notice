package com.project.core.support.file;

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

import com.project.core.exception.ApplicationException;

@DisplayName("파일 서버 관리 기능 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest
class FileManagerTest {

	@Autowired private FileManager sut;

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
	void null_또는_빈_배열을_전달하면_예외발생() {
		assertThatThrownBy(() -> sut.store(null))
			.isInstanceOf(ApplicationException.class);
		assertThatThrownBy(() -> sut.store(List.of()))
			.isInstanceOf(ApplicationException.class);
	}

	@Test
	void 허용된_확장자에_맞는_파일을_업로드_할_수_있다() {
		List<MultipartFile> files = List.of(
			new MockMultipartFile(
				"files",
				"sample.txt",
				MediaType.TEXT_PLAIN_VALUE,
				"file upload except test".getBytes()
			)
		);
		List<Attachment> attachments = sut.store(files);
		assertThat(attachments).hasSize(1);
	}

	@Test
	void 허용되지_않은_확장자에_맞는_파일은_예외발생() {
		List<MultipartFile> files = List.of(
			new MockMultipartFile(
				"files",
				"sample.java",
				MediaType.TEXT_PLAIN_VALUE,
				"file upload except test".getBytes()
			)
		);
		assertThatThrownBy(() -> sut.store(files))
			.isInstanceOf(ApplicationException.class);
	}

	@Test
	void 서버에_존재하는_파일을_다운로드_할_수_있다() {
		List<MultipartFile> files = List.of(
			new MockMultipartFile(
				"files",
				"sample.txt",
				MediaType.TEXT_PLAIN_VALUE,
				"file upload except test".getBytes()
			)
		);
		Attachment attachment = sut.store(files).get(0);

		LoadFile loadFile = sut.load(attachment.getDirPath(), attachment.getPhysicalFilename(),
			attachment.getExtension());

		assertThat(loadFile.getContentType()).isEqualTo(MediaType.TEXT_PLAIN_VALUE);
		assertThat(loadFile.getResource()).isNotNull();
	}

	@Test
	void 서버에_존재하는_파일과_디렉토리을_삭제_할_수_있다() {
		List<MultipartFile> files = List.of(
			new MockMultipartFile(
				"files",
				"sample.txt",
				MediaType.TEXT_PLAIN_VALUE,
				"file upload except test".getBytes()
			)
		);
		Attachment attachment = sut.store(files).get(0);

		sut.remove(attachment.getDirPath(), attachment.getPhysicalFilename(), attachment.getExtension());

		Path filePath = Paths.get(attachment.getDirPath())
			.resolve("%s.%s".formatted(attachment.getPhysicalFilename(), attachment.getExtension())).normalize();
		Path directoryPath = Paths.get(attachment.getDirPath());
		assertThat(Files.notExists(filePath)).isTrue();
		assertThat(Files.notExists(directoryPath)).isTrue();
	}

	@Test
	void 서버에_존재하는_파일을_삭제_할_수_있다() {
		List<MultipartFile> files = List.of(
			new MockMultipartFile(
				"files",
				"sample1.txt",
				MediaType.TEXT_PLAIN_VALUE,
				"file upload except test1".getBytes()
			),
			new MockMultipartFile(
				"files",
				"sample2.txt",
				MediaType.TEXT_PLAIN_VALUE,
				"file upload except test2".getBytes()
			)
		);
		Attachment attachment = sut.store(files).get(0);

		sut.remove(attachment.getDirPath(), attachment.getPhysicalFilename(), attachment.getExtension());

		Path directoryPath = Paths.get(attachment.getDirPath());
		Path filePath = directoryPath.resolve("%s.%s".formatted(attachment.getPhysicalFilename(), attachment.getExtension())).normalize();
		assertThat(Files.notExists(filePath)).isTrue();
		assertThat(Files.notExists(directoryPath)).isFalse();
	}
}