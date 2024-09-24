package com.project.application.notice.domain.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.project.application.notice.dto.response.NoticeSearchResponse;

public interface NoticeQueryRepository {

	Page<NoticeSearchResponse> findAll(Pageable pageable);
}
