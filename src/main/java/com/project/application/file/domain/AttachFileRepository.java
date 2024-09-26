package com.project.application.file.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachFileRepository extends JpaRepository<AttachFileEntity, Long> {

	List<AttachFileEntity> findAllByIdIn(List<Long> fileIds);

	List<AttachFileEntity> findAllByActiveAndRemoveAndCreatedAtBefore(Boolean isActive, Boolean isRemove, LocalDateTime createdAt);

	Optional<AttachFileEntity> findByIdAndRemove(Long id, Boolean isRemove);
}
