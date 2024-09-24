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
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.project.application.notice.domain.NoticeEntity;
import com.project.application.notice.domain.repository.NoticeRepository;
import com.project.application.notice.dto.request.NoticeFileRequest;
import com.project.application.notice.dto.request.NoticeCreateRequest;
import com.project.application.notice.dto.request.NoticeModifyRequest;
import com.project.application.notice.dto.response.NoticeCreateResponse;
import com.project.core.exception.ApplicationException;

@DisplayName("공지사항 수정 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DataJpaTest
class NoticeModifierTest {

	@Autowired
	private NoticeRepository noticeRepository;
	private NoticeCreator noticeCreator;

	private NoticeModifier sut;

	@BeforeEach
	void setUp() {
		noticeRepository.deleteAll();

		this.noticeCreator = new NoticeCreator(noticeRepository);
		this.sut = new NoticeModifier(noticeRepository);
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
		sut.modify(noticeId, request);
		NoticeEntity notice = noticeRepository.findById(noticeId).get();

		assertModifiedNotice(notice, 0);
	}

	@Test
	void 공지사항은_기존_파일있고_수정_파일없이_수정_할_수_있다() {
		Long noticeId = createNotice(List.of(
			new NoticeFileRequest(1L, "첫번째 파일.jpg"),
			new NoticeFileRequest(2L, "두번째 파일.jpg")
		));

		NoticeModifyRequest request = new NoticeModifyRequest(
			"공지사항수정테스트",
			"공지사항수정테스트",
			LocalDateTime.of(2024, 10, 1, 0, 0, 0),
			LocalDateTime.of(2024, 10, 30, 0, 0, 0),
			List.of()
		);
		sut.modify(noticeId, request);
		NoticeEntity notice = noticeRepository.findById(noticeId).get();

		assertModifiedNotice(notice, 0);
	}

	@Test
	void 공지사항은_기존_파일에_다른_파일을_더_추가할_수_있다() {
		Long noticeId = createNotice(List.of(
			new NoticeFileRequest(1L, "첫번째 파일.jpg"),
			new NoticeFileRequest(2L, "두번째 파일.jpg")
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
		sut.modify(noticeId, request);
		NoticeEntity notice = noticeRepository.findById(noticeId).get();

		assertModifiedNotice(notice, 3);
	}

	@Test
	void 공지기간_중_시작일이_종료일보다_후일이면_예외발생() {
		List<NoticeFileRequest> files = List.of(
			new NoticeFileRequest(1L, "첫번째 파일.jpg"),
			new NoticeFileRequest(2L, "두번째 파일.jpg")
		);

		Long noticeId = createNotice(files);

		NoticeModifyRequest request = new NoticeModifyRequest(
			"공지사항수정테스트",
			"공지사항수정테스트",
			LocalDateTime.of(2024, 10, 30, 0, 0, 0),
			LocalDateTime.of(2024, 10, 1, 0, 0, 0),
			List.of()
		);

		assertThatThrownBy(() -> sut.modify(noticeId, request))
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

	private Long createNotice(List<NoticeFileRequest> files) {
		NoticeCreateRequest request = new NoticeCreateRequest(
			"공지사항등록테스트",
			"공지사항등록테스트",
			LocalDateTime.of(2024, 9, 1, 0, 0, 0),
			LocalDateTime.of(2024, 9, 30, 0, 0, 0),
			files
		);
		NoticeCreateResponse response = noticeCreator.create(request);
		return response.noticeId();
	}

}