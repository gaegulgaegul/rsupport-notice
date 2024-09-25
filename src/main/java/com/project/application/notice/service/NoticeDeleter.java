package com.project.application.notice.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.application.account.vo.Account;
import com.project.application.file.usecase.DeactivateAttachFiles;
import com.project.application.notice.domain.NoticeEntity;
import com.project.application.notice.domain.repository.NoticeRepository;
import com.project.application.notice.error.NoticeErrorCode;
import com.project.core.exception.ApplicationException;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeDeleter {
	private final NoticeRepository noticeRepository;
	private final DeactivateAttachFiles deactivateAttachFiles;

	@Transactional
	@CacheEvict(value = "notices", key = "#noticeId", cacheManager = "redisCacheManager")
	public void delete(@NotNull Long noticeId, Account account) {
		NoticeEntity notice = noticeRepository.findById(noticeId)
			.orElseThrow(() -> new ApplicationException(NoticeErrorCode.NO_CONTENT));

		if (notice.isNotAuthor(account.getId())) {
			throw new ApplicationException(NoticeErrorCode.ANOTHER_AUTHOR);
		}

		deactivateAttachFiles.deactivate(notice.getFileIds());
		noticeRepository.deleteById(noticeId);
	}
}
