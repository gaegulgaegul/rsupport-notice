package com.project.application.notice.service;

import java.util.List;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.project.application.account.vo.Account;
import com.project.application.notice.domain.NoticeEntity;
import com.project.application.notice.domain.NoticeFileEntity;
import com.project.application.notice.domain.repository.NoticeRepository;
import com.project.application.notice.dto.response.NoticeFileResponse;
import com.project.application.notice.dto.response.NoticeReadResponse;
import com.project.application.notice.error.NoticeErrorCode;
import com.project.core.exception.ApplicationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeReader {
	private final NoticeRepository noticeRepository;

	@Transactional
	public NoticeReadResponse read(Long noticeId, Account account) {
		NoticeEntity notice = noticeRepository.findById(noticeId)
			.orElseThrow(() -> new ApplicationException(NoticeErrorCode.NO_CONTENT));

		if (notice.isNotViewed(account.getId())) {
			increaseViewCount(notice, account);
		}

		return toResponse(notice);
	}

	@CachePut(value = "notices", key = "#notice.id", cacheManager = "redisCacheManager")
	public NoticeReadResponse increaseViewCount(NoticeEntity notice, Account account) {
		notice.view(account.getId());
		noticeRepository.save(notice);
		return toResponse(notice);
	}

	@Cacheable(value = "notices", key = "#notice.id", cacheManager = "redisCacheManager")
	public NoticeReadResponse toResponse(NoticeEntity notice) {
		return NoticeReadResponse.builder()
			.noticeId(notice.getId())
			.title(notice.getTitle())
			.content(notice.getContent())
			.viewCount(notice.getViewCount())
			.createdAt(notice.getCreatedAt())
			.createdBy(notice.getCreatedBy())
			.lastModifiedAt(notice.getLastModifiedAt())
			.lastModifiedBy(notice.getLastModifiedBy())
			.files(toNoticeFileResponse(notice.getFiles()))
			.build();
	}

	private List<NoticeFileResponse> toNoticeFileResponse(List<NoticeFileEntity> files) {
		if (ObjectUtils.isEmpty(files)) {
			return List.of();
		}
		return files.stream()
			.map(item -> NoticeFileResponse.builder()
				.fileId(item.getFileId())
				.filename(item.getFilename())
				.build())
			.toList();
	}
}
