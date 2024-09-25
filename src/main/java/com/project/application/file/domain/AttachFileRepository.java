package com.project.application.file.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachFileRepository extends JpaRepository<AttachFileEntity, Long> {

	List<AttachFileEntity> findAllByIdIn(List<Long> fileIds);
}
