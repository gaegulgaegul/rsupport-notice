package com.project.application.file.usecase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ObjectUtils;

import com.project.application.file.domain.AttachFileEntity;
import com.project.application.file.domain.AttachFileRepository;
import com.project.application.file.error.AttachFileErrorCode;
import com.project.application.file.vo.AttachFileInfo;
import com.project.core.exception.ApplicationException;
import com.project.core.support.annotation.Usecase;
import com.project.core.support.file.FileManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Usecase
@RequiredArgsConstructor
class FileUsecase implements GetAttachFiles, ActiveAttachFiles, DeactivateAttachFiles, CleanUpDeactivatedFiles, CheckFileOwner {
	private final AttachFileRepository attachFileRepository;
	private final FileManager fileManager;

	@Override
	public List<AttachFileInfo> read(List<Long> fileIds) {
		List<AttachFileEntity> files = attachFileRepository.findAllByIdIn(fileIds);
		if (ObjectUtils.isEmpty(files)) {
			return List.of();
		}
		return files.stream()
			.map(item -> new AttachFileInfo(item.getId(), item.getOriginalFilename()))
			.toList();
	}

	@Override
	public void active(List<Long> fileIds) {
		List<AttachFileEntity> files = attachFileRepository.findAllByIdIn(fileIds);
		if (ObjectUtils.isEmpty(files)) {
			return;
		}

		List<AttachFileEntity> saveFiles = files.stream()
			.filter(AttachFileEntity::isDeactivate)
			.peek(AttachFileEntity::active)
			.toList();
		attachFileRepository.saveAll(saveFiles);
	}

	@Override
	public void deactivate(List<Long> fileIds) {
		List<AttachFileEntity> files = attachFileRepository.findAllByIdIn(fileIds);
		if (ObjectUtils.isEmpty(files)) {
			return;
		}

		List<AttachFileEntity> saveFiles = files.stream()
			.filter(AttachFileEntity::isActive)
			.peek(AttachFileEntity::deactivate)
			.toList();
		attachFileRepository.saveAll(saveFiles);
	}

	@Override
	public void clean(LocalDate now) {
		List<AttachFileEntity> files = attachFileRepository.findAllByActiveAndRemoveAndCreatedAtBefore(false, true, now.atStartOfDay());

		List<AttachFileEntity> deleteFiles = new ArrayList<>();
		for (AttachFileEntity file : files) {
			try {
				fileManager.remove(file.getDirPath(), file.getPhysicalFilename(), file.getExtension());
				deleteFiles.add(file);
			} catch (Exception e) {
				log.error("[fileId: {}] {}", file.getId(), e.getMessage());
				deleteFiles.remove(file);
			}
		}
		attachFileRepository.deleteAll(deleteFiles);
	}

	@Override
	public void check(Long accountId, List<Long> fileIds) {
		List<AttachFileEntity> files = attachFileRepository.findAllByIdIn(fileIds);
		if (ObjectUtils.isEmpty(files)) {
			return;
		}

		for (AttachFileEntity file : files) {
			if (!file.getCreatedBy().equals(accountId)) {
				throw new ApplicationException(AttachFileErrorCode.NO_OWNER);
			}
		}
	}
}
