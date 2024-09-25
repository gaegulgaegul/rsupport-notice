package com.project.application.notice.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import com.project.application.account.vo.Account;

@DisplayName("공지사항 등록 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class NoticeEntityTest {
	public static final Long CREATED_BY = 1L;

	private final NoticeEntity createdNotice = new NoticeEntity() {
		@Override
		public Long getCreatedBy() {
			return CREATED_BY;
		}
	};


	@Test
	void 공지_시작일은_종료일보다_후일일_수_없다() {
		NoticeEntity notice = NoticeEntity.builder()
			.from(LocalDateTime.of(2024, 9, 25, 0, 0, 0))
			.to(LocalDateTime.of(2024, 9, 24, 0, 0, 0))
			.build();

		assertThat(notice.isInvalidDuration()).isTrue();
	}

	@Test
	void 공지_시작일은_종료일보다_전일만_입력할_수_있다() {
		NoticeEntity notice = NoticeEntity.builder()
			.from(LocalDateTime.of(2024, 9, 24, 0, 0, 0))
			.to(LocalDateTime.of(2024, 9, 25, 0, 0, 0))
			.build();

		assertThat(notice.isInvalidDuration()).isFalse();
	}

	@Test
	void 공지사항은_등록자만_권한이_있다() {
		assertThat(createdNotice.isNotAuthor(CREATED_BY)).isFalse();
	}

	@Test
	void 공지사항은_다른_사용자는_권한이_없다() {
		assertThat(createdNotice.isNotAuthor(Account.DEFAULT.getId())).isTrue();
	}
}