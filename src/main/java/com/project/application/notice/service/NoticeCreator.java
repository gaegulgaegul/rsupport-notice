package com.project.application.notice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.project.application.notice.domain.NoticeEntity;
import com.project.application.notice.domain.NoticeFileEntity;
import com.project.application.notice.domain.repository.NoticeRepository;
import com.project.application.notice.dto.request.NoticeCreateRequest;
import com.project.application.notice.dto.request.NoticeFileRequest;
import com.project.application.notice.dto.response.NoticeCreateResponse;
import com.project.application.notice.error.NoticeErrorCode;
import com.project.core.exception.ApplicationException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeCreator {
	private final NoticeRepository noticeRepository;

	public NoticeCreateResponse create(NoticeCreateRequest request) {
		validateNoticeDuration(request.from(), request.to());
		// TODO 파일 ID 검증

		NoticeEntity notice = toNotice(request);
		notice.link();
		noticeRepository.save(notice);
		return new NoticeCreateResponse(notice.getId());
	}

	private void validateNoticeDuration(LocalDateTime from, LocalDateTime to) {
		if (from.isAfter(to)) {
			throw new ApplicationException(NoticeErrorCode.DURATION);
		}
	}

	private NoticeEntity toNotice(NoticeCreateRequest request) {
		return NoticeEntity.builder()
			.title(request.title())
			.content(request.content())
			.from(request.from())
			.to(request.to())
			.files(toNoticeFiles(request.files()))
			.build();
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
