package com.project.application.notice.service;

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
		// TODO 파일 ID 검증

		NoticeEntity notice = toNotice(request);
		if (notice.isInvalidDuration()) {
			throw new ApplicationException(NoticeErrorCode.DURATION);
		}
		noticeRepository.save(notice);
		return new NoticeCreateResponse(notice.getId());
	}

	private NoticeEntity toNotice(NoticeCreateRequest request) {
		NoticeEntity notice = NoticeEntity.builder()
			.title(request.title())
			.content(request.content())
			.from(request.from())
			.to(request.to())
			.build();
		notice.linkFiles(toNoticeFiles(request.files()));
		return notice;
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
