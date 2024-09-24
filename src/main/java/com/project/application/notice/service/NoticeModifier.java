package com.project.application.notice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.project.application.notice.domain.NoticeEntity;
import com.project.application.notice.domain.NoticeFileEntity;
import com.project.application.notice.domain.repository.NoticeRepository;
import com.project.application.notice.dto.request.NoticeFileRequest;
import com.project.application.notice.dto.request.NoticeModifyRequest;
import com.project.application.notice.error.NoticeErrorCode;
import com.project.core.exception.ApplicationException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeModifier {
	private final NoticeRepository noticeRepository;

	public void modify(Long noticeId, NoticeModifyRequest request) {
		validateNoticeDuration(request.from(), request.to());

		NoticeEntity notice = noticeRepository.findById(noticeId)
			.orElseThrow(() -> new ApplicationException(NoticeErrorCode.NO_CONTENT));
		notice.modify(toNoticeBuilder(request));
		notice.link();

		noticeRepository.save(notice);
	}

	private void validateNoticeDuration(LocalDateTime from, LocalDateTime to) {
		if (from.isAfter(to)) {
			throw new ApplicationException(NoticeErrorCode.DURATION);
		}
	}

	private NoticeEntity.NoticeEntityBuilder toNoticeBuilder(NoticeModifyRequest request) {
		return NoticeEntity.builder()
			.title(request.title())
			.content(request.content())
			.from(request.from())
			.to(request.to())
			.files(toNoticeFiles(request.files()));
	}

	private List<NoticeFileEntity> toNoticeFiles(List<NoticeFileRequest> requests) {
		if (ObjectUtils.isEmpty(requests)) {
			return List.of();
		}
		return requests.stream()
			.distinct()
			.map(item -> NoticeFileEntity.builder()
				.fileId(item.fileId())
				.fileName(item.fileName())
				.build())
			.distinct()
			.toList();
	}
}
