package com.project.application.notice.service;

import org.springframework.stereotype.Service;

import com.project.application.notice.domain.repository.NoticeRepository;
import com.project.application.notice.error.NoticeErrorCode;
import com.project.core.exception.ApplicationException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeDeleter {
	private final NoticeRepository noticeRepository;

	public void delete(Long noticeId) {
		if (!noticeRepository.existsById(noticeId)) {
			throw new ApplicationException(NoticeErrorCode.NO_CONTENT);
		}
		noticeRepository.deleteById(noticeId);
	}
}
