package com.project.application.notice.service;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.project.application.account.vo.Account;
import com.project.application.notice.domain.NoticeEntity;
import com.project.application.notice.domain.repository.NoticeRepository;
import com.project.application.notice.error.NoticeErrorCode;
import com.project.core.exception.ApplicationException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeDeleter {
	private final NoticeRepository noticeRepository;

	public void delete(Long noticeId, Account account) {
		if (ObjectUtils.isEmpty(noticeId)) {
			throw new ApplicationException(NoticeErrorCode.EMPTY_ID);
		}
		NoticeEntity notice = noticeRepository.findById(noticeId)
			.orElseThrow(() -> new ApplicationException(NoticeErrorCode.NO_CONTENT));
		if (notice.isNotAuthor(account.getId())) {
			throw new ApplicationException(NoticeErrorCode.ANOTHER_AUTHOR);
		}
		noticeRepository.deleteById(noticeId);
	}
}
