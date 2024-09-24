package com.project.application.notice.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.project.application.notice.domain.NoticeEntity;
import com.project.application.notice.domain.NoticeFileEntity;
import com.project.application.notice.domain.repository.NoticeRepository;
import com.project.application.notice.dto.response.NoticeFileResponse;
import com.project.application.notice.dto.response.NoticeReadResponse;
import com.project.application.notice.error.NoticeErrorCode;
import com.project.core.exception.ApplicationException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeReader {
	private final NoticeRepository noticeRepository;

	public Object read(Long noticeId) {
		NoticeEntity notice = noticeRepository.findById(noticeId)
			.orElseThrow(() -> new ApplicationException(NoticeErrorCode.NO_CONTENT));
		return toResponse(notice);
	}

	private NoticeReadResponse toResponse(NoticeEntity notice) {
		return NoticeReadResponse.builder()
			.noticeId(notice.getId())
			.title(notice.getTitle())
			.content(notice.getContent())
			.viewCount(notice.getViewUsers().size())
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
				.fileName(item.getFileName())
				.build())
			.toList();
	}
}
