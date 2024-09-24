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
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.project.application.notice.domain.NoticeEntity;
import com.project.application.notice.domain.repository.NoticeRepository;
import com.project.application.notice.dto.request.NoticeFileRequest;
import com.project.application.notice.dto.request.NoticeCreateRequest;
import com.project.application.notice.dto.response.NoticeCreateResponse;
import com.project.core.exception.ApplicationException;

@DisplayName("공지사항 등록 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest
@Transactional
class NoticeCreatorTest {

	@Autowired private NoticeRepository noticeRepository;

	@Autowired private NoticeCreator sut;

	@BeforeEach
	void setUp() {
		noticeRepository.deleteAll();
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
		NoticeCreateResponse result = sut.create(request);
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
			List.of(
				new NoticeFileRequest(1L, "첫번째 파일.jpg"),
				new NoticeFileRequest(2L, "두번째 파일.jpg")
			)
		);

		NoticeCreateResponse result = sut.create(request);
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
			List.of(
				new NoticeFileRequest(1L, "첫번째 파일.jpg"),
				new NoticeFileRequest(1L, "첫번째 파일.jpg")
			)
		);

		NoticeCreateResponse result = sut.create(request);
		NoticeEntity notice = noticeRepository.findById(result.noticeId()).get();

		assertThat(result.noticeId()).isNotZero();
		assertThat(notice.getFiles()).hasSize(1);
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

		assertThatThrownBy(() -> sut.create(request))
			.isInstanceOf(ApplicationException.class);
	}
}