package com.project.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("공지사항 인수 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class NoticeAcceptanceTest extends AcceptanceTest {
	private static final String EMAIL = "user@gmail.com";
	private static final String PASSWORD = "1234";
	private static final String NAME = "테스트 사용자";
	private static final Long NO_EXIST_NOTICE_ID = Long.MAX_VALUE;

	@Autowired private NoticeAcceptanceDispatcher dispatcher;

	@BeforeEach
	void setUp() {
		dispatcher.사용자_생성(EMAIL, PASSWORD, NAME);
	}

	@Nested
	class 로그인을_하고 {
		private String sessionId;

		@BeforeEach
		void setUp() {
			sessionId = dispatcher.로그인(EMAIL, PASSWORD);
		}

		@Test
		void 공지사항_목록을_조회하면_정보가_없다() {
			ExtractableResponse<Response> response = dispatcher.공지사항_목록_조회();

			assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
			assertThat(response.jsonPath().getBoolean("empty")).isTrue();
			assertThat(response.jsonPath().getList("content")).isEmpty();
		}

		@Test
		void 등록되지_않은_공지사항을_조회_할_수_없다() {
			ExtractableResponse<Response> response = dispatcher.공지사항_조회(sessionId, NO_EXIST_NOTICE_ID);

			assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
			assertThat(response.jsonPath().getInt("status")).isEqualTo(HttpStatus.NO_CONTENT.value());
			assertThat(response.jsonPath().getString("code")).isEqualTo("N001");
		}

		@Test
		void 공지사항을_등록_할_수_있다() {
			ExtractableResponse<Response> response = dispatcher.공지사항_등록(sessionId);

			assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
			assertThat(response.header("Location")).isNotNull();
			assertThat(response.jsonPath().getLong("noticeId")).isNotNull();
		}

		@Test
		void 등록되지_않은_공지사항을_수정_할_수_없다() {
			ExtractableResponse<Response> response = dispatcher.공지사항_수정(sessionId, NO_EXIST_NOTICE_ID);

			assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
			assertThat(response.jsonPath().getInt("status")).isEqualTo(HttpStatus.NO_CONTENT.value());
			assertThat(response.jsonPath().getString("code")).isEqualTo("N001");
		}

		@Test
		void 등록되지_않은_공지사항을_식제_할_수_없다() {
			ExtractableResponse<Response> response = dispatcher.공지사항_삭제(sessionId, NO_EXIST_NOTICE_ID);

			assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
			assertThat(response.jsonPath().getInt("status")).isEqualTo(HttpStatus.NO_CONTENT.value());
			assertThat(response.jsonPath().getString("code")).isEqualTo("N001");
		}

		@Nested
		class 공지사항을_등록하면 {
			public static final String NOTICE_TITLE = "공지사항 등록";
			public static final String NOTICE_CONTENT = "공지사항 내용";

			private Long noticeId;

			@BeforeEach
			void setUp() {
				ExtractableResponse<Response> response = dispatcher.공지사항_등록(sessionId, NOTICE_TITLE, NOTICE_CONTENT);
				noticeId = response.jsonPath().getLong("noticeId");
			}

			@Test
			void 공지사항을_조회_할_수_있다() {
				ExtractableResponse<Response> response = dispatcher.공지사항_조회(sessionId, noticeId);

				assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
				assertThat(response.jsonPath().getString("title")).isEqualTo(NOTICE_TITLE);
				assertThat(response.jsonPath().getString("content")).isEqualTo(NOTICE_CONTENT);
			}

			@Test
			void 공지사항을_수정_할_수_있다() {
				String 제목_수정 = "공지사항 수정";
				String 내용_수정 = "공지사항 내용 수정";

				ExtractableResponse<Response> modifyResponse = dispatcher.공지사항_수정(sessionId, noticeId, 제목_수정, 내용_수정);
				assertThat(modifyResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

				ExtractableResponse<Response> readResponse = dispatcher.공지사항_조회(sessionId, noticeId);

				assertThat(readResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
				assertThat(readResponse.jsonPath().getString("title")).isEqualTo(제목_수정);
				assertThat(readResponse.jsonPath().getString("content")).isEqualTo(내용_수정);
			}

			@Test
			void 공지사항을_삭제_할_수_있다() {
				ExtractableResponse<Response> deleteResponse = dispatcher.공지사항_삭제(sessionId, noticeId);
				assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
			}
		}

		@Nested
		class 여러_파일을_업로드_후 {

			@Test
			void 공지사항을_등록_할_수_있다() {

			}

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
			ExtractableResponse<Response> response = dispatcher.공지사항_조회(null, NO_EXIST_NOTICE_ID);

			assertThatNoAuthentication(response);
		}

		@Test
		void 공지사항을_등록하면_인증_에러가_발생한다() {
			ExtractableResponse<Response> response = dispatcher.공지사항_등록(null);

			assertThatNoAuthentication(response);
		}

		@Test
		void 공지사항을_수정하면_인증_에러가_발생한다() {
			ExtractableResponse<Response> response = dispatcher.공지사항_수정(null, NO_EXIST_NOTICE_ID);

			assertThatNoAuthentication(response);
		}

		@Test
		void 공지사항을_삭제하면_인증_에러가_발생한다() {
			ExtractableResponse<Response> response = dispatcher.공지사항_삭제(null, NO_EXIST_NOTICE_ID);

			assertThatNoAuthentication(response);
		}

		@Test
		void 파일을_업로드하면_인증_에러가_발생한다() throws IOException {
			ExtractableResponse<Response> response = dispatcher.파일_업로드();

			assertThatNoAuthentication(response);
		}

		private void assertThatNoAuthentication(ExtractableResponse<Response> response) {
			assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
			assertThat(response.jsonPath().getInt("status")).isEqualTo(401);
			assertThat(response.jsonPath().getString("code")).isEqualTo("AZ001");
		}
	}
}
