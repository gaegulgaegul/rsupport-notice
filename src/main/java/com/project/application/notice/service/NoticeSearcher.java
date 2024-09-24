package com.project.application.notice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.application.notice.domain.repository.NoticeRepository;
import com.project.application.notice.dto.response.NoticeSearchResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeSearcher {
	private final NoticeRepository noticeRepository;

	public Page<NoticeSearchResponse> read(Pageable pageable) {
		return noticeRepository.searchAll(pageable);
	}
}
