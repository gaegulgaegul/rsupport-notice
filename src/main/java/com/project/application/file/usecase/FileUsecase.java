package com.project.application.file.usecase;

import java.util.List;

import org.springframework.util.ObjectUtils;

import com.project.application.file.domain.AttachFileEntity;
import com.project.application.file.domain.AttachFileRepository;
import com.project.application.file.vo.AttachFileInfo;
import com.project.core.support.annotation.Usecase;

import lombok.RequiredArgsConstructor;

@Usecase
@RequiredArgsConstructor
class FileUsecase implements GetAttachFiles, ActiveAttachFiles {
	private final AttachFileRepository attachFileRepository;

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
			.filter(AttachFileEntity::isNotActive)
			.peek(AttachFileEntity::active)
			.toList();
		attachFileRepository.saveAll(saveFiles);
	}
}
