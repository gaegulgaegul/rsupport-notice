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

import com.project.application.notice.domain.repository.NoticeRepository;
import com.project.application.notice.dto.NoticeFileDTO;
import com.project.application.notice.dto.request.NoticeCreateRequest;
import com.project.application.notice.dto.response.NoticeCreateResponse;

@DisplayName("공지사항 등록 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DataJpaTest
class NoticeDeleterTest {

	@Autowired
	private NoticeRepository noticeRepository;

	private NoticeCreator noticeCreator;
	private NoticeDeleter sut;

	@BeforeEach
	void setUp() {
		this.noticeCreator = new NoticeCreator(noticeRepository);
		this.sut = new NoticeDeleter(noticeRepository);
	}

	@Test
	void 공지사항은_ID에_해당하는_정보를_삭제할_수_있다() {
		NoticeCreateRequest request = new NoticeCreateRequest(
			"공지사항등록테스트",
			"공지사항등록테스트",
			LocalDateTime.of(2024, 9, 1, 0, 0, 0),
			LocalDateTime.of(2024, 9, 30, 0, 0, 0),
			List.of(
				new NoticeFileDTO(1L, "첫번째 파일.jpg"),
				new NoticeFileDTO(2L, "두번째 파일.jpg"),
				new NoticeFileDTO(3L, "세번째 파일.jpg")
			)
		);
		NoticeCreateResponse response = noticeCreator.create(request);
		Long noticeId = response.noticeId();

		sut.delete(noticeId);

		assertThat(noticeRepository.existsById(noticeId)).isFalse();
	}

	@Test
	void 존재하지_않는_ID에_해당하는_정보를_삭제하면_예외발생() {
		assertThatThrownBy(() -> sut.delete(99L));
	}

	@Test
	void null을_전달하면_예외발생() {
		assertThatThrownBy(() -> sut.delete(null));
	}

}