package com.project.application.notice.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.project.application.notice.dto.response.NoticeSearchResponse;

interface NoticeQueryRepository {

	Page<NoticeSearchResponse> searchAll(Pageable pageable);
}
