package com.project.application.notice.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.project.application.file.usecase.ActiveAttachFiles;
import com.project.application.file.usecase.GetAttachFiles;
import com.project.application.file.vo.AttachFileInfo;
import com.project.application.notice.domain.NoticeEntity;
import com.project.application.notice.domain.NoticeFileEntity;
import com.project.application.notice.domain.repository.NoticeRepository;
import com.project.application.notice.dto.request.NoticeCreateRequest;
import com.project.application.notice.dto.response.NoticeCreateResponse;
import com.project.application.notice.error.NoticeErrorCode;
import com.project.core.exception.ApplicationException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeCreator {
	private final NoticeRepository noticeRepository;
	private final GetAttachFiles getAttachFiles;
	private final ActiveAttachFiles activeAttachFiles;

	@Transactional
	public NoticeCreateResponse create(NoticeCreateRequest request) {
		List<AttachFileInfo> files = getAttachFiles.read(request.fileIds());
		if (hasInvalidFiles(files, request.fileIds())) {
			throw new ApplicationException(NoticeErrorCode.INVALID_FILE);
		}

		NoticeEntity notice = toNotice(request, files);
		if (notice.isInvalidDuration()) {
			throw new ApplicationException(NoticeErrorCode.DURATION);
		}
		noticeRepository.save(notice);
		activeAttachFiles.active(request.fileIds());
		return new NoticeCreateResponse(notice.getId());
	}

	private boolean hasInvalidFiles(List<AttachFileInfo> files, List<Long> fileIds) {
		List<Long> dbFileIds = files.stream()
			.map(AttachFileInfo::fileId)
			.toList();
		ArrayList<Long> newFileIds = new ArrayList<>(fileIds);
		newFileIds.removeAll(dbFileIds);
		return !newFileIds.isEmpty();
	}

	private NoticeEntity toNotice(NoticeCreateRequest request, List<AttachFileInfo> files) {
		NoticeEntity notice = NoticeEntity.builder()
			.title(request.title())
			.content(request.content())
			.from(request.from())
			.to(request.to())
			.build();
		notice.linkFiles(toNoticeFiles(files));
		return notice;
	}

	private List<NoticeFileEntity> toNoticeFiles(List<AttachFileInfo> requests) {
		if (ObjectUtils.isEmpty(requests)) {
			return List.of();
		}
		return requests.stream()
			.distinct()
			.map(item -> NoticeFileEntity.builder()
				.fileId(item.fileId())
				.fileName(item.filename())
				.build())
			.distinct()
			.toList();
	}

}
