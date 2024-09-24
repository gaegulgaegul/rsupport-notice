package com.project.application.notice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.application.notice.domain.NoticeEntity;

public interface NoticeRepository extends JpaRepository<NoticeEntity, Long>, NoticeQueryRepository {
}
