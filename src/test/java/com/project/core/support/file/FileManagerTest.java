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
import com.project.core.support.file.Attachment;
import com.project.core.support.file.FileManager;
import com.project.core.support.file.LoadFile;

@DisplayName("파일 업/다운로드 기능 테스트")
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
}