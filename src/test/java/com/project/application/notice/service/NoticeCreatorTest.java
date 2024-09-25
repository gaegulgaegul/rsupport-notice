package com.project.application.notice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.project.application.account.vo.Account;
import com.project.application.file.domain.AttachFileEntity;
import com.project.application.file.domain.AttachFileRepository;
import com.project.application.notice.domain.NoticeEntity;
import com.project.application.notice.domain.repository.NoticeRepository;
import com.project.application.notice.dto.request.NoticeCreateRequest;
import com.project.application.notice.dto.response.NoticeCreateResponse;
import com.project.core.exception.ApplicationException;

@DisplayName("공지사항 등록 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest
@Transactional
class NoticeCreatorTest {

	@Autowired private NoticeRepository noticeRepository;
	@Autowired private AttachFileRepository attachFileRepository;

	@Autowired private NoticeCreator sut;

	private final Account account = Account.DEFAULT;

	private AttachFileEntity file1;
	private AttachFileEntity file2;
	private AttachFileEntity anotherOwnerFile;

	@BeforeEach
	void setUp() {
		noticeRepository.deleteAll();
		attachFileRepository.deleteAll();

		file1 = attachFile("첫번째 파일.jpg");
		file2 = attachFile("두번째 파일.jpg");
		anotherOwnerFile = attachFile("다른 사용자가 만든 파일");
		attachFileRepository.saveAll(List.of(file1, file2, anotherOwnerFile));
	}

	@Test
	void 공지사항은_파일첨부_없이_등록_할_수_있다() {
		NoticeCreateRequest request = new NoticeCreateRequest(
			"공지사항등록테스트",
			"공지사항등록테스트",
			LocalDateTime.of(2024, 9, 1, 0, 0, 0),
			LocalDateTime.of(2024, 9, 30, 0, 0, 0),
			List.of()
		);
		NoticeCreateResponse result = sut.create(request, account);
		NoticeEntity notice = noticeRepository.findById(result.noticeId()).get();

		assertThat(result.noticeId()).isNotZero();
		assertThat(notice.getFiles()).isEmpty();
	}

	@Test
	void 공지사항은_파일과_등록_할_수_있다() {
		NoticeCreateRequest request = new NoticeCreateRequest(
			"공지사항등록테스트",
			"공지사항등록테스트",
			LocalDateTime.of(2024, 9, 1, 0, 0, 0),
			LocalDateTime.of(2024, 9, 30, 0, 0, 0),
			List.of(file1.getId(), file2.getId())
		);

		NoticeCreateResponse result = sut.create(request, account);
		NoticeEntity notice = noticeRepository.findById(result.noticeId()).get();

		assertThat(result.noticeId()).isNotZero();
		assertThat(notice.getFiles()).hasSize(2);
	}

	@Test
	void 공지사항의_파일은_중복_등록_할_수_없다() {
		NoticeCreateRequest request = new NoticeCreateRequest(
			"공지사항등록테스트",
			"공지사항등록테스트",
			LocalDateTime.of(2024, 9, 1, 0, 0, 0),
			LocalDateTime.of(2024, 9, 30, 0, 0, 0),
			List.of(file1.getId(), file1.getId())
		);

		NoticeCreateResponse result = sut.create(request, account);
		NoticeEntity notice = noticeRepository.findById(result.noticeId()).get();

		assertThat(result.noticeId()).isNotZero();
		assertThat(notice.getFiles()).hasSize(1);
	}

	@Test
	void 존재하지_않는_파일은_공지사항에_등록하면_예외발생() {
		NoticeCreateRequest request = new NoticeCreateRequest(
			"공지사항등록테스트",
			"공지사항등록테스트",
			LocalDateTime.of(2024, 9, 1, 0, 0, 0),
			LocalDateTime.of(2024, 9, 30, 0, 0, 0),
			List.of(3L)
		);

		assertThatThrownBy(() -> sut.create(request, account))
			.isInstanceOf(ApplicationException.class);
	}

	@Test
	void 공지기간_중_시작일이_종료일보다_후일이면_예외발생() {
		NoticeCreateRequest request = new NoticeCreateRequest(
			"공지사항등록테스트",
			"공지사항등록테스트",
			LocalDateTime.of(2024, 9, 24, 0, 0, 0),
			LocalDateTime.of(2024, 9, 23, 0, 0, 0),
			List.of()
		);

		assertThatThrownBy(() -> sut.create(request, account))
			.isInstanceOf(ApplicationException.class);
	}

	@Test
	void 다른_소유자_파일을_첨부하면_예외발생() {
		ReflectionTestUtils.setField(anotherOwnerFile, "createdBy", 99L);

		NoticeCreateRequest request = new NoticeCreateRequest(
			"공지사항등록테스트",
			"공지사항등록테스트",
			LocalDateTime.of(2024, 9, 1, 0, 0, 0),
			LocalDateTime.of(2024, 9, 30, 0, 0, 0),
			List.of(anotherOwnerFile.getId())
		);

		assertThatThrownBy(() -> sut.create(request, account))
			.isInstanceOf(ApplicationException.class);
	}

	private AttachFileEntity attachFile(String filename) {
		return AttachFileEntity.builder()
			.originalFilename(filename)
			.physicalFilename("test")
			.contentType("test")
			.extension("jpg")
			.dirPath("test")
			.fileSize(100L)
			.createFileDateTime(LocalDateTime.now())
			.lastModifiedFileDateTime(LocalDateTime.now())
			.lastAccessFileDateTime(LocalDateTime.now())
			.build();
	}
}