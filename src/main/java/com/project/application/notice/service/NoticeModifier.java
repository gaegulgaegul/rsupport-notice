package com.project.application.notice.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.project.application.account.vo.Account;
import com.project.application.file.usecase.ActiveAttachFiles;
import com.project.application.file.usecase.CheckFileOwner;
import com.project.application.file.usecase.DeactivateAttachFiles;
import com.project.application.file.usecase.GetAttachFiles;
import com.project.application.file.vo.AttachFileInfo;
import com.project.application.notice.domain.NoticeEntity;
import com.project.application.notice.domain.NoticeFileEntity;
import com.project.application.notice.domain.repository.NoticeRepository;
import com.project.application.notice.dto.request.NoticeModifyRequest;
import com.project.application.notice.dto.response.NoticeFileResponse;
import com.project.application.notice.dto.response.NoticeReadResponse;
import com.project.application.notice.error.NoticeErrorCode;
import com.project.core.exception.ApplicationException;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeModifier {
	private final NoticeRepository noticeRepository;
	private final GetAttachFiles getAttachFiles;
	private final ActiveAttachFiles activeAttachFiles;
	private final DeactivateAttachFiles deactivateAttachFiles;
	private final CheckFileOwner checkFileOwner;

	@Transactional
	@CachePut(value = "notices", key = "#noticeId", cacheManager = "redisCacheManager")
	public NoticeReadResponse modify(@NotNull Long noticeId, NoticeModifyRequest request, Account account) {
		NoticeEntity notice = noticeRepository.findById(noticeId)
			.orElseThrow(() -> new ApplicationException(NoticeErrorCode.NO_CONTENT));

		if (notice.isNotAuthor(account.getId())) {
			throw new ApplicationException(NoticeErrorCode.ANOTHER_AUTHOR);
		}

		checkFileOwner.check(account.getId(), request.fileIds());

		List<AttachFileInfo> files = getAttachFiles.read(request.fileIds());
		if (hasInvalidFiles(files, request.fileIds())) {
			throw new ApplicationException(NoticeErrorCode.INVALID_FILE);
		}

		List<Long> removeFileIds = notice.getRemoveFileIds(request.fileIds());

		notice.modify(toNoticeBuilder(request, files));

		if (notice.isInvalidDuration()) {
			throw new ApplicationException(NoticeErrorCode.DURATION);
		}

		noticeRepository.save(notice);
		activeAttachFiles.active(toActiveFileIds(request.fileIds(), removeFileIds));
		deactivateAttachFiles.deactivate(removeFileIds);
		return toResponse(notice);
	}

	private boolean hasInvalidFiles(List<AttachFileInfo> files, List<Long> fileIds) {
		List<Long> dbFileIds = files.stream()
			.map(AttachFileInfo::fileId)
			.toList();
		ArrayList<Long> newFileIds = new ArrayList<>(fileIds);
		newFileIds.removeAll(dbFileIds);
		return !newFileIds.isEmpty();
	}

	private List<Long> toActiveFileIds(List<Long> fileIds, List<Long> removeFileIds) {
		List<Long> newFileIds = new ArrayList<>(fileIds);
		newFileIds.removeAll(removeFileIds);
		return newFileIds;
	}

	private NoticeEntity.NoticeEntityBuilder toNoticeBuilder(NoticeModifyRequest request, List<AttachFileInfo> files) {
		return NoticeEntity.builder()
			.title(request.title())
			.content(request.content())
			.from(request.from())
			.to(request.to())
			.files(toNoticeFiles(files));
	}

	private List<NoticeFileEntity> toNoticeFiles(List<AttachFileInfo> requests) {
		if (ObjectUtils.isEmpty(requests)) {
			return List.of();
		}
		return requests.stream()
			.distinct()
			.map(item -> NoticeFileEntity.builder()
				.fileId(item.fileId())
				.filename(item.filename())
				.build())
			.distinct()
			.toList();
	}

	private NoticeReadResponse toResponse(NoticeEntity notice) {
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
