package com.project.acceptance;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.project.application.account.domain.AccountEntity;
import com.project.application.account.domain.AccountRepository;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@Component
class NoticeAcceptanceDispatcher {
	@Autowired private AccountRepository accountRepository;
	@Autowired private PasswordEncoder passwordEncoder;

	void 사용자_생성(String email, String password, String name) {
		AccountEntity account = AccountEntity.builder()
			.email(email)
			.password(passwordEncoder.encode(password))
			.name(name)
			.build();
		accountRepository.save(account);
	}

	ExtractableResponse<Response> 로그인(String email, String password) {
		return RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(Map.of(
				"email", email,
				"password", password
			))
			.when().post("/api/sign/in")
			.then().log().all().extract();
	}

	ExtractableResponse<Response> 공지사항_목록_조회() {
		return 공지사항_목록_조회(0, 10, "createdAt,DESC");
	}

	ExtractableResponse<Response> 공지사항_목록_조회(int page, int size, String sort) {
		return RestAssured
			.given().log().all()
			.queryParams(Map.of(
				"page", page,
				"size", size,
				"sort", sort
			))
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/api/notices")
			.then().log().all().extract();
	}

	ExtractableResponse<Response> 공지사항_조회(Long noticeId) {
		return RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/api/notices/" + noticeId)
			.then().log().all().extract();
	}

	ExtractableResponse<Response> 공지사항_등록() {
		return 공지사항_등록("공지사항 등록 제목", "등록 내용");
	}

	ExtractableResponse<Response> 공지사항_등록(String title, String content) {
		return RestAssured
			.given().log().all()
			.body(Map.of(
				"title", title,
				"content", content,
				"from", "2024-07-01T00:00:00.000Z",
				"to", "2024-12-31T00:00:00.000Z",
				"fileIds", List.of()
			))
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post("/api/notices")
			.then().log().all().extract();
	}

	ExtractableResponse<Response> 공지사항_수정(Long noticeId) {
		return 공지사항_수정(noticeId, "공지사항 수정 제목", "수정 내용");
	}

	ExtractableResponse<Response> 공지사항_수정(Long noticeId, String title, String content) {
		return RestAssured
			.given().log().all()
			.body(Map.of(
				"title", title,
				"content", content,
				"from", "2024-07-01T00:00:00.000Z",
				"to", "2024-12-31T00:00:00.000Z",
				"fileIds", List.of()
			))
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().put("/api/notices/" + noticeId)
			.then().log().all().extract();
	}

	ExtractableResponse<Response> 공지사항_삭제(Long noticeId) {
		return RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().delete("/api/notices/" + noticeId)
			.then().log().all().extract();
	}
}
