package com.project.application.notice.domain.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.project.application.notice.domain.QNoticeEntity;
import com.project.application.notice.dto.response.NoticeSearchResponse;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class NoticeQueryRepositoryImpl implements NoticeQueryRepository {
	private final JPAQueryFactory jpaQueryFactory;

	private QNoticeEntity notice = QNoticeEntity.noticeEntity;

	@Override
	public Page<NoticeSearchResponse> searchAll(Pageable pageable) {
		JPAQuery<NoticeSearchResponse> query = searchNoticeQuery();

		for (Sort.Order order : pageable.getSort()) {
			PathBuilder pathBuilder = new PathBuilder(notice.getType(), notice.getMetadata());
			query.orderBy(new OrderSpecifier(order.isAscending() ? Order.ASC : Order.DESC, pathBuilder.get(order.getProperty())));
		}

		long count = query.stream().count();

		List<NoticeSearchResponse> notices = query
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		return new PageImpl<>(notices, pageable, count);
	}

	private JPAQuery<NoticeSearchResponse> searchNoticeQuery() {
		return jpaQueryFactory.select(Projections.constructor(NoticeSearchResponse.class,
				notice.id.as("noticeId"),
				notice.title,
				notice.content,
				notice.createdAt,
				notice.viewUsers.size().as("viewCount"),
				notice.createdBy
			))
			.from(notice);
	}
}
