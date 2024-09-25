package com.project.application.file.usecase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import com.project.application.file.domain.AttachFileEntity;
import com.project.application.file.domain.AttachFileRepository;
import com.project.application.file.vo.AttachFileInfo;
import com.project.core.support.annotation.Usecase;
import com.project.core.support.file.FileManager;

import lombok.RequiredArgsConstructor;

@Usecase
@RequiredArgsConstructor
class FileUsecase implements GetAttachFiles, ActiveAttachFiles, DeactivateAttachFiles, CleanUpDeactivatedFiles {
	private static final Logger log = LoggerFactory.getLogger(FileUsecase.class);
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
		List<AttachFileEntity> files = attachFileRepository.findAllByActiveAndCreatedAtBefore(false, now.atStartOfDay());

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
}
