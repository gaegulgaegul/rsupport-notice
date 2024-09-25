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
import org.springframework.transaction.annotation.Transactional;

import com.project.application.account.vo.Account;
import com.project.application.notice.domain.NoticeEntity;
import com.project.application.notice.domain.NoticeFileEntity;
import com.project.application.notice.domain.repository.NoticeRepository;
import com.project.application.notice.dto.request.NoticeFileRequest;
import com.project.application.notice.dto.request.NoticeModifyRequest;
import com.project.core.exception.ApplicationException;

@DisplayName("공지사항 수정 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest
@Transactional
class NoticeModifierTest {

	@Autowired private NoticeRepository noticeRepository;

	@Autowired private NoticeModifier sut;

	private final Account account = Account.DEFAULT;
	private final Account anotherAccount = Account.builder().id(1L).build();

	@BeforeEach
	void setUp() {
		noticeRepository.deleteAll();
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
			NoticeFileEntity.builder().fileId(1L).fileName("첫번째 파일.jpg").build(),
			NoticeFileEntity.builder().fileId(2L).fileName("두번째 파일.jpg").build()
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
			NoticeFileEntity.builder().fileId(1L).fileName("첫번째 파일.jpg").build(),
			NoticeFileEntity.builder().fileId(2L).fileName("두번째 파일.jpg").build()
		));

		NoticeModifyRequest request = new NoticeModifyRequest(
			"공지사항수정테스트",
			"공지사항수정테스트",
			LocalDateTime.of(2024, 10, 1, 0, 0, 0),
			LocalDateTime.of(2024, 10, 30, 0, 0, 0),
			List.of(
				new NoticeFileRequest(1L, "첫번째 파일.jpg"),
				new NoticeFileRequest(2L, "두번째 파일.jpg"),
				new NoticeFileRequest(3L, "세번째 파일.jpg")
			)
		);
		sut.modify(noticeId, request, account);
		NoticeEntity notice = noticeRepository.findById(noticeId).get();

		assertModifiedNotice(notice, 3);
	}

	@Test
	void 공지기간_중_시작일이_종료일보다_후일이면_예외발생() {
		Long noticeId = createNotice(List.of(
			NoticeFileEntity.builder().fileId(1L).fileName("첫번째 파일.jpg").build(),
			NoticeFileEntity.builder().fileId(2L).fileName("두번째 파일.jpg").build()
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
			.files(files)
			.build();
		noticeRepository.save(notice);
		return notice.getId();
	}

}