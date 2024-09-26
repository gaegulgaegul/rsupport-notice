package com.project.application.notice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.project.application.account.vo.Account;
import com.project.application.file.domain.AttachFileEntity;
import com.project.application.file.domain.AttachFileRepository;
import com.project.application.notice.domain.NoticeEntity;
import com.project.application.notice.domain.NoticeFileEntity;
import com.project.application.notice.domain.repository.NoticeRepository;
import com.project.application.notice.dto.request.NoticeModifyRequest;
import com.project.core.exception.ApplicationException;

@DisplayName("공지사항 수정 기능 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest
@Transactional
class NoticeModifierTest {

	@Autowired private NoticeRepository noticeRepository;
	@Autowired private AttachFileRepository attachFileRepository;

	@Autowired private NoticeModifier sut;

	private final Account account = Account.DEFAULT;
	private final Account anotherAccount = Account.builder().id(1L).build();

	private AttachFileEntity file1;
	private AttachFileEntity file2;
	private AttachFileEntity file3;
	private AttachFileEntity anotherOwnerFile;

	@BeforeEach
	void setUp() {
		noticeRepository.deleteAll();
		attachFileRepository.deleteAll();

		file1 = attachFile("첫번째 파일.jpg");
		file2 = attachFile("두번째 파일.jpg");
		file3 = attachFile("세번째 파일.jpg");
		anotherOwnerFile = attachFile("다른 사용자가 만든 파일");
		attachFileRepository.saveAll(List.of(file1, file2, file3, anotherOwnerFile));
	}

	@Test
	void 공지사항은_파일첨부_없이_수정_할_수_있다() {
		Long noticeId = createNotice(List.of());

		NoticeModifyRequest request = new NoticeModifyRequest(
			"공지사항수정테스트",
			"공지사항수정테스트",
			LocalDateTime.of(2024, 10, 1, 0, 0, 0),
			LocalDateTime.of(2024, 10, 30, 0, 0, 0),
			List.of()
		);
		sut.modify(noticeId, request, account);
		NoticeEntity notice = noticeRepository.findById(noticeId).get();

		assertModifiedNotice(notice, 0);
	}

	@Test
	void 공지사항은_기존_파일있고_수정_파일없이_수정_할_수_있다() {
		Long noticeId = createNotice(List.of(
			NoticeFileEntity.builder().fileId(file1.getId()).filename(file1.getOriginalFilename()).build(),
			NoticeFileEntity.builder().fileId(file2.getId()).filename(file2.getOriginalFilename()).build()
		));

		NoticeModifyRequest request = new NoticeModifyRequest(
			"공지사항수정테스트",
			"공지사항수정테스트",
			LocalDateTime.of(2024, 10, 1, 0, 0, 0),
			LocalDateTime.of(2024, 10, 30, 0, 0, 0),
			List.of()
		);
		sut.modify(noticeId, request, account);
		NoticeEntity notice = noticeRepository.findById(noticeId).get();

		assertModifiedNotice(notice, 0);
	}

	@Test
	void 공지사항은_기존_파일에_다른_파일을_더_추가할_수_있다() {
		Long noticeId = createNotice(List.of(
			NoticeFileEntity.builder().fileId(file1.getId()).filename(file1.getOriginalFilename()).build(),
			NoticeFileEntity.builder().fileId(file2.getId()).filename(file2.getOriginalFilename()).build()
		));

		NoticeModifyRequest request = new NoticeModifyRequest(
			"공지사항수정테스트",
			"공지사항수정테스트",
			LocalDateTime.of(2024, 10, 1, 0, 0, 0),
			LocalDateTime.of(2024, 10, 30, 0, 0, 0),
			List.of(file1.getId(), file2.getId(), file3.getId())
		);
		sut.modify(noticeId, request, account);
		NoticeEntity notice = noticeRepository.findById(noticeId).get();

		assertModifiedNotice(notice, 3);
	}

	@Test
	void 존재하지_않는_파일은_공지사항에_등록하면_예외발생() {
		Long noticeId = createNotice(List.of(
			NoticeFileEntity.builder().fileId(file1.getId()).filename(file1.getOriginalFilename()).build(),
			NoticeFileEntity.builder().fileId(file2.getId()).filename(file2.getOriginalFilename()).build()
		));

		NoticeModifyRequest request = new NoticeModifyRequest(
			"공지사항수정테스트",
			"공지사항수정테스트",
			LocalDateTime.of(2024, 10, 1, 0, 0, 0),
			LocalDateTime.of(2024, 10, 30, 0, 0, 0),
			List.of(99L)
		);

		assertThatThrownBy(() -> sut.modify(noticeId, request, account))
			.isInstanceOf(ApplicationException.class);
	}

	@Test
	void 공지기간_중_시작일이_종료일보다_후일이면_예외발생() {
		Long noticeId = createNotice(List.of(
			NoticeFileEntity.builder().fileId(file1.getId()).filename(file1.getOriginalFilename()).build(),
			NoticeFileEntity.builder().fileId(file2.getId()).filename(file2.getOriginalFilename()).build()
		));

		NoticeModifyRequest request = new NoticeModifyRequest(
			"공지사항수정테스트",
			"공지사항수정테스트",
			LocalDateTime.of(2024, 10, 30, 0, 0, 0),
			LocalDateTime.of(2024, 10, 1, 0, 0, 0),
			List.of()
		);

		assertThatThrownBy(() -> sut.modify(noticeId, request, account))
			.isInstanceOf(ApplicationException.class);
	}

	@Test
	void 등록자가_아닌_사용자가_수정하면_예외발생() {
		Long noticeId = createNotice(List.of());

		NoticeModifyRequest request = new NoticeModifyRequest(
			"공지사항수정테스트",
			"공지사항수정테스트",
			LocalDateTime.of(2024, 10, 1, 0, 0, 0),
			LocalDateTime.of(2024, 10, 30, 0, 0, 0),
			List.of()
		);

		assertThatThrownBy(() -> sut.modify(noticeId, request, anotherAccount))
			.isInstanceOf(ApplicationException.class);
	}

	@Test
	void ID에_null을_전달하면_예외발생() {
		NoticeModifyRequest request = new NoticeModifyRequest(
			"공지사항수정테스트",
			"공지사항수정테스트",
			LocalDateTime.of(2024, 10, 1, 0, 0, 0),
			LocalDateTime.of(2024, 10, 30, 0, 0, 0),
			List.of()
		);

		assertThatThrownBy(() -> sut.modify(null, request, account))
			.isInstanceOf(InvalidDataAccessApiUsageException.class);
	}

	@Test
	void 다른_소유자_파일을_첨부하면_예외발생() {
		ReflectionTestUtils.setField(anotherOwnerFile, "createdBy", 99L);
		Long noticeId = createNotice(List.of());

		NoticeModifyRequest request = new NoticeModifyRequest(
			"공지사항수정테스트",
			"공지사항수정테스트",
			LocalDateTime.of(2024, 10, 1, 0, 0, 0),
			LocalDateTime.of(2024, 10, 30, 0, 0, 0),
			List.of(anotherOwnerFile.getId())
		);

		assertThatThrownBy(() -> sut.modify(noticeId, request, anotherAccount))
			.isInstanceOf(ApplicationException.class);
	}

	private void assertModifiedNotice(NoticeEntity notice, int expected) {
		assertAll(() -> {
			assertThat(notice.getTitle()).isEqualTo("공지사항수정테스트");
			assertThat(notice.getContent()).isEqualTo("공지사항수정테스트");
			assertThat(notice.getFrom()).isEqualTo(LocalDateTime.of(2024, 10, 1, 0, 0, 0));
			assertThat(notice.getTo()).isEqualTo(LocalDateTime.of(2024, 10, 30, 0, 0, 0));
			assertThat(notice.getFiles()).hasSize(expected);
		});
	}

	private Long createNotice(List<NoticeFileEntity> files) {
		NoticeEntity notice = NoticeEntity.builder()
			.title("공지사항등록테스트")
			.content("공지사항등록테스트")
			.from(LocalDateTime.of(2024, 9, 1, 0, 0, 0))
			.to(LocalDateTime.of(2024, 9, 30, 0, 0, 0))
			.build();
		notice.linkFiles(files);
		noticeRepository.save(notice);
		return notice.getId();
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