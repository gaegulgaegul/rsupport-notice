package com.project.application.notice.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.project.application.notice.domain.NoticeEntity;
import com.project.application.notice.domain.NoticeFileEntity;
import com.project.application.notice.domain.repository.NoticeRepository;
import com.project.application.notice.dto.NoticeFileDTO;
import com.project.application.notice.dto.request.NoticeCreateRequest;
import com.project.application.notice.dto.response.NoticeCreateResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeCreator {
	private final NoticeRepository noticeRepository;

	public NoticeCreateResponse create(NoticeCreateRequest request) {
		if (request.startDateTime().isBefore(request.endDateTime())) {

		}
		NoticeEntity notice = toNotice(request);
		// TODO 파일 ID 검증
		notice.link();
		noticeRepository.save(notice);
		return new NoticeCreateResponse(notice.getId());
	}

	private NoticeEntity toNotice(NoticeCreateRequest request) {
		NoticeEntity.NoticeEntityBuilder builder = NoticeEntity.builder()
			.title(request.title())
			.content(request.content())
			.startDateTime(request.startDateTime())
			.endDateTime(request.endDateTime());
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
