package com.project.application.notice.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.project.application.account.vo.Account;
import com.project.application.notice.domain.NoticeEntity;
import com.project.application.notice.domain.repository.NoticeRepository;
import com.project.application.notice.dto.response.NoticeReadResponse;

@DisplayName("공지사항 수정 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest
@Transactional
class NoticeReaderTest {

	@Autowired private NoticeRepository noticeRepository;

	@Autowired private NoticeReader sut;

	@Test
	void 공지사항을_조회하면_조회수가_증가한다() {
		Long noticeId = createNotice();

		NoticeReadResponse read = sut.read(noticeId, Account.DEFAULT);

		assertThat(read.viewCount()).isEqualTo(1);
	}

	@Test
	void 이미_조회한_사용자는_다시_조회하면_조회수가_증가하지_않는다() {
		Long noticeId = createNotice();

		sut.read(noticeId, Account.DEFAULT);
		NoticeReadResponse read = sut.read(noticeId, Account.DEFAULT);

		assertThat(read.viewCount()).isEqualTo(1);
	}

	private Long createNotice() {
		NoticeEntity notice = NoticeEntity.builder()
			.title("공지사항조회테스트")
			.content("공지사항조회테스트")
			.from(LocalDateTime.of(2024, 9, 1, 0, 0, 0))
			.to(LocalDateTime.of(2024, 9, 30, 0, 0, 0))
			.build();
		noticeRepository.save(notice);
		return notice.getId();
	}
}