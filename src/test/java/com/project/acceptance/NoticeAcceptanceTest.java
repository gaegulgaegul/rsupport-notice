package com.project.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.project.application.account.domain.AccountEntity;
import com.project.application.account.domain.AccountRepository;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("공지사항 인수 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class NoticeAcceptanceTest extends AcceptanceTest {
	private static final String EMAIL = "user@gmail.com";
	private static final String PASSWORD = "1234";
	private static final String NAME = "테스트 사용자";

	@Autowired private NoticeAcceptanceDispatcher dispatcher;

	@BeforeEach
	void setUp() {
		dispatcher.사용자_생성(EMAIL, PASSWORD, NAME);
	}

	@Nested
	class 로그인을_하고 {

		@BeforeEach
		void setUp() {
			dispatcher.로그인(EMAIL, PASSWORD);
		}

		@Test
		void 공지사항_목록을_조회하면_정보가_없다() {
			ExtractableResponse<Response> response = dispatcher.공지사항_목록_조회();

			assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
			assertThat(response.jsonPath().getBoolean("empty")).isTrue();
			assertThat(response.jsonPath().getList("content")).isEmpty();
		}

		@Test
		void 공지사항을_조회_할_수_있다() {}

		@Test
		void 공지사항을_등록_할_수_있다() {}

		@Test
		void 공지사항을_수정_할_수_있다() {}

		@Test
		void 공지사항을_삭제_할_수_있다() {}

		@Nested
		class 여러_파일을_업로드하고 {

			@Test
			void 공지사항을_등록_할_수_있다() {}

			@Nested
			class 공지사항을_등록하면 {

				@Test
				void 공지사항을_조회_할_수_있다() {}

				@Test
				void 공지사항을_수정_할_수_있다() {}

				@Test
				void 공지사항을_삭제_할_수_있다() {}
			}

			@Nested
			class 공지사항을_등록_및_수정하면 {

				@Test
				void 공지사항을_조회_할_수_있다() {}

				@Test
				void 공지사항을_수정_할_수_있다() {}

				@Test
				void 공지사항을_삭제_할_수_있다() {}
			}
		}
	}

	@Nested
	class 로그인을_하지_않고 {

		@Test
		void 공지사항_목록을_조회하면_정보가_없다() {
			ExtractableResponse<Response> response = dispatcher.공지사항_목록_조회();

			assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
			assertThat(response.jsonPath().getBoolean("empty")).isTrue();
			assertThat(response.jsonPath().getList("content")).isEmpty();
		}

		@Test
		void 공지사항을_조회하면_인증_에러가_발생한다() {
			ExtractableResponse<Response> response = dispatcher.공지사항_조회(1L);

			assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
			assertThat(response.jsonPath().getInt("status")).isEqualTo(401);
			assertThat(response.jsonPath().getString("code")).isEqualTo("AZ001");
		}

		@Test
		void 공지사항을_등록하면_인증_에러가_발생한다() {
			ExtractableResponse<Response> response = dispatcher.공지사항_등록();

			assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
			assertThat(response.jsonPath().getInt("status")).isEqualTo(401);
			assertThat(response.jsonPath().getString("code")).isEqualTo("AZ001");
		}

		@Test
		void 공지사항을_수정하면_인증_에러가_발생한다() {
			ExtractableResponse<Response> response = dispatcher.공지사항_수정(1L);

			assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
			assertThat(response.jsonPath().getInt("status")).isEqualTo(401);
			assertThat(response.jsonPath().getString("code")).isEqualTo("AZ001");
		}

		@Test
		void 공지사항을_삭제하면_인증_에러가_발생한다() {
			ExtractableResponse<Response> response = dispatcher.공지사항_삭제(1L);

			assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
			assertThat(response.jsonPath().getInt("status")).isEqualTo(401);
			assertThat(response.jsonPath().getString("code")).isEqualTo("AZ001");
		}

		@Test
		void 파일을_업로드하면_인증_에러가_발생한다() {

		}
	}
}
