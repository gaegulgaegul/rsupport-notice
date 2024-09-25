package com.project.application.notice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.application.account.vo.Account;
import com.project.application.notice.domain.NoticeEntity;
import com.project.application.notice.domain.NoticeFileEntity;
import com.project.application.notice.domain.repository.NoticeRepository;
import com.project.core.exception.ApplicationException;

@DisplayName("공지사항 삭제 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest
class NoticeDeleterTest {

	@Autowired
	private NoticeRepository noticeRepository;

	@Autowired
	private NoticeDeleter sut;

	private final Account account = Account.DEFAULT;
	private final Account anotherAccount = Account.builder().id(1L).build();

	@Test
	void 공지사항은_ID에_해당하는_정보를_삭제할_수_있다() {
		Long noticeId = createNotice();

		sut.delete(noticeId, account);

		assertThat(noticeRepository.existsById(noticeId)).isFalse();
	}

	@Test
	void 공지사항은_등록자_외_사용자는_삭제하면_예외발생() {
		Long noticeId = createNotice();

		assertThatThrownBy(() -> sut.delete(noticeId, anotherAccount))
			.isInstanceOf(ApplicationException.class);
	}

	@Test
	void 존재하지_않는_ID에_해당하는_정보를_삭제하면_예외발생() {
		assertThatThrownBy(() -> sut.delete(99L, account));
	}

	@Test
	void null을_전달하면_예외발생() {
		assertThatThrownBy(() -> sut.delete(null, account));
	}

	private Long createNotice() {
		NoticeEntity notice = NoticeEntity.builder()
			.title("공지사항삭제테스트")
			.content("공지사항삭제테스트")
			.from(LocalDateTime.of(2024, 9, 1, 0, 0, 0))
			.to(LocalDateTime.of(2024, 9, 30, 0, 0, 0))
			.files(List.of(
				NoticeFileEntity.builder().fileId(1L).fileName("첫번째 파일.jpg").build(),
				NoticeFileEntity.builder().fileId(2L).fileName("두번째 파일.jpg").build(),
				NoticeFileEntity.builder().fileId(3L).fileName("세번째 파일.jpg").build()
			))
			.build();
		noticeRepository.save(notice);
		return notice.getId();
	}

}