package com.project.acceptance.senario.notice;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.project.application.account.domain.AccountEntity;
import com.project.application.account.domain.AccountRepository;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

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

	String 로그인(String email, String password) {
		return RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(Map.of(
				"email", email,
				"password", password
			))
			.when().post("/api/sign/in")
			.then().log().all().statusCode(HttpStatus.OK.value())
			.extract().cookie("SESSION");
	}

	ExtractableResponse<Response> 공지사항_목록_조회(String sessionId) {
		return 공지사항_목록_조회(sessionId, 0, 10, "createdAt,DESC");
	}

	ExtractableResponse<Response> 공지사항_목록_조회(String sessionId, int page, int size, String sort) {
		return RestAssured
			.given().log().all().cookie("SESSION", sessionId)
			.queryParams(Map.of(
				"page", page,
				"size", size,
				"sort", sort
			))
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/api/notices")
			.then().log().all().extract();
	}

	ExtractableResponse<Response> 공지사항_조회(String sessionId, Long noticeId) {
		return RestAssured
			.given().log().all().cookie("SESSION", sessionId)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/api/notices/" + noticeId)
			.then().log().all().extract();
	}

	ExtractableResponse<Response> 공지사항_등록(String sessionId) {
		return 공지사항_등록(sessionId, "공지사항 등록 제목", "등록 내용");
	}

	ExtractableResponse<Response> 공지사항_등록(String sessionId, String title, String content) {
		return 공지사항_등록(sessionId, title, content, List.of());
	}

	ExtractableResponse<Response> 공지사항_등록(String sessionId, String title, String content, List<Long> fileIds) {
		return RestAssured
			.given().log().all().cookie("SESSION", sessionId)
			.body(Map.of(
				"title", title,
				"content", content,
				"from", "2024-07-01T00:00:00.000Z",
				"to", "2024-12-31T00:00:00.000Z",
				"fileIds", fileIds
			))
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post("/api/notices")
			.then().log().all().extract();
	}

	ExtractableResponse<Response> 공지사항_수정(String sessionId, Long noticeId) {
		return 공지사항_수정(sessionId, noticeId, "공지사항 수정 제목", "수정 내용", List.of());
	}

	ExtractableResponse<Response> 공지사항_수정(String sessionId, Long noticeId, List<Long> fileIds) {
		return 공지사항_수정(sessionId, noticeId, "공지사항 수정 제목", "수정 내용", fileIds);
	}

	ExtractableResponse<Response> 공지사항_수정(String sessionId, Long noticeId, String title, String content) {
		return 공지사항_수정(sessionId, noticeId, title, content, List.of());
	}

	ExtractableResponse<Response> 공지사항_수정(String sessionId, Long noticeId, String title, String content, List<Long> fileIds) {
		return RestAssured
			.given().log().all().cookie("SESSION", sessionId)
			.body(Map.of(
				"title", title,
				"content", content,
				"from", "2024-07-01T00:00:00.000Z",
				"to", "2024-12-31T00:00:00.000Z",
				"fileIds", fileIds
			))
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().put("/api/notices/" + noticeId)
			.then().log().all().extract();
	}

	ExtractableResponse<Response> 공지사항_삭제(String sessionId, Long noticeId) {
		return RestAssured
			.given().log().all().cookie("SESSION", sessionId)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().delete("/api/notices/" + noticeId)
			.then().log().all().extract();
	}

	ExtractableResponse<Response> 파일_업로드(String sessionId) throws IOException {
		MockMultipartFile file = new MockMultipartFile(
			"files",
			"sample.txt",
			MediaType.TEXT_PLAIN_VALUE,
			"file upload except test".getBytes()
		);
		return 파일_업로드(sessionId, List.of(file));
	}

	ExtractableResponse<Response> 파일_업로드(String sessionId, List<MockMultipartFile> files) throws IOException {
		RequestSpecification restAssured = RestAssured
			.given().log().all().cookie("SESSION", sessionId);
		for (MockMultipartFile file : files) {
			restAssured
				.multiPart("files", file.getOriginalFilename(), file.getInputStream(), file.getContentType());
		}
		return restAssured
			.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
			.when().post("/api/files")
			.then().log().all().extract();
	}

	ExtractableResponse<Response> 파일_다운로드(String sessionId, Long fileId) {
		return RestAssured
			.given().log().all().cookie("SESSION", sessionId)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/api/files/" + fileId)
			.then().log().all().extract();
	}
}
