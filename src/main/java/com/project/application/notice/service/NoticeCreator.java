package com.project.application.notice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.project.application.notice.domain.NoticeEntity;
import com.project.application.notice.domain.NoticeFileEntity;
import com.project.application.notice.domain.repository.NoticeRepository;
import com.project.application.notice.dto.NoticeFileDTO;
import com.project.application.notice.dto.request.NoticeCreateRequest;
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
		NoticeEntity.NoticeEntityBuilder builder = NoticeEntity.builder()
			.title(request.title())
			.content(request.content())
			.from(request.from())
			.to(request.to());
		if (!ObjectUtils.isEmpty(request.files())) {
			builder.files(toNoticeFiles(request.files()));
		}
		return builder.build();
	}

	private List<NoticeFileEntity> toNoticeFiles(List<NoticeFileDTO> dtoList) {
		return dtoList.stream()
			.map(item -> NoticeFileEntity.builder()
				.fileId(item.fileId())
				.fileName(item.fileName())
				.build())
			.toList();
	}

}
