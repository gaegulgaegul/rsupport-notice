package com.project.acceptance.senario.notice;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import com.project.acceptance.utils.AcceptanceTest;
import com.project.application.file.domain.AttachFileEntity;
import com.project.application.file.domain.AttachFileRepository;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("공지사항 인수 테스트")
class NoticeAcceptanceTest extends AcceptanceTest {
	private static final String EMAIL = "user@gmail.com";
	private static final String PASSWORD = "1234";
	private static final String NAME = "테스트 사용자";
	private static final Long NO_EXIST_NOTICE_ID = Long.MAX_VALUE;
	private static final Long NO_EXIST_FILE_ID = Long.MAX_VALUE;

	@Autowired private NoticeAcceptanceDispatcher dispatcher;
	@Autowired private AttachFileRepository attachFileRepository;

	@BeforeEach
	void setUp() {
		dispatcher.사용자_생성(EMAIL, PASSWORD, NAME);
	}

	@DisplayName("로그인을 하고")
	@Nested
	class Case1 {
		private String session;

		@BeforeEach
		void setUp() {
			session = dispatcher.로그인(EMAIL, PASSWORD);
		}

		@DisplayName("공지사항 목록을 조회하면 정보가 없다")
		@Test
		void condition1() {
			ExtractableResponse<Response> response = dispatcher.공지사항_목록_조회(session);

			assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
			assertThat(response.jsonPath().getBoolean("empty")).isTrue();
			assertThat(response.jsonPath().getList("content")).isEmpty();
		}

		@DisplayName("등록되지 않은 공지사항을 조회 할 수 없다")
		@Test
		void condition2() {
			ExtractableResponse<Response> response = dispatcher.공지사항_조회(session, NO_EXIST_NOTICE_ID);

			assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
			assertThat(response.jsonPath().getInt("status")).isEqualTo(HttpStatus.NO_CONTENT.value());
			assertThat(response.jsonPath().getString("code")).isEqualTo("N001");
		}

		@DisplayName("공지사항을 등록 할 수 있다")
		@Test
		void condition3() {
			ExtractableResponse<Response> response = dispatcher.공지사항_등록(session, "공지사항 등록", "공지사항 등록 내용");

			assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
			assertThat(response.header("Location")).isNotNull();
			assertThat(response.jsonPath().getLong("noticeId")).isNotNull();
		}

		@DisplayName("등록되지 않은 공지사항을 수정 할 수 없다")
		@Test
		void condition4() {
			ExtractableResponse<Response> response = dispatcher.공지사항_수정(session, NO_EXIST_NOTICE_ID);

			assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
			assertThat(response.jsonPath().getInt("status")).isEqualTo(HttpStatus.NO_CONTENT.value());
			assertThat(response.jsonPath().getString("code")).isEqualTo("N001");
		}

		@DisplayName("등록되지 않은 공지사항을 식제 할 수 없다")
		@Test
		void condition5() {
			ExtractableResponse<Response> response = dispatcher.공지사항_삭제(session, NO_EXIST_NOTICE_ID);

			assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
			assertThat(response.jsonPath().getInt("status")).isEqualTo(HttpStatus.NO_CONTENT.value());
			assertThat(response.jsonPath().getString("code")).isEqualTo("N001");
		}

		@DisplayName("공지사항을 등록하면")
		@Nested
		class Case1_1 {
			private Long noticeId;

			@BeforeEach
			void setUp() {
				ExtractableResponse<Response> response = dispatcher.공지사항_등록(session);
				noticeId = response.jsonPath().getLong("noticeId");
			}

			@DisplayName("공지사항을 조회 할 수 있다")
			@Test
			void condition1() {
				ExtractableResponse<Response> response = dispatcher.공지사항_조회(session, noticeId);

				String actualTitle = "공지사항 등록 제목";
				String actualContent = "등록 내용";

				assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
				assertThat(response.jsonPath().getString("title")).isEqualTo(actualTitle);
				assertThat(response.jsonPath().getString("content")).isEqualTo(actualContent);
			}

			@DisplayName("공지사항을 수정 할 수 있다")
			@Test
			void condition2() {
				String modifyTitle = "공지사항 수정";
				String modifyContent = "공지사항 내용 수정";

				ExtractableResponse<Response> modifyResponse = dispatcher.공지사항_수정(session, noticeId, modifyTitle, modifyContent);
				assertThat(modifyResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

				ExtractableResponse<Response> readResponse = dispatcher.공지사항_조회(session, noticeId);

				assertThat(readResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
				assertThat(readResponse.jsonPath().getString("title")).isEqualTo(modifyTitle);
				assertThat(readResponse.jsonPath().getString("content")).isEqualTo(modifyContent);
			}

			@DisplayName("공지사항을 삭제 할 수 있다")
			@Test
			void condition3() {
				ExtractableResponse<Response> deleteResponse = dispatcher.공지사항_삭제(session, noticeId);
				assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

				ExtractableResponse<Response> response = dispatcher.공지사항_조회(session, noticeId);
				assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
				assertThat(response.jsonPath().getInt("status")).isEqualTo(HttpStatus.NO_CONTENT.value());
				assertThat(response.jsonPath().getString("code")).isEqualTo("N001");
			}
		}

		@DisplayName("여러 파일을 업로드 후")
		@Nested
		class Case1_2 {
			private static final String NOTICE_TITLE = "파일과 공지사항 등록";
			private static final String NOTICE_CONTENT = "파일과 공지사항 등록 내용";

			private MockMultipartFile file1;
			private MockMultipartFile file2;
			private MockMultipartFile file3;
			private MockMultipartFile file4;
			private MockMultipartFile file5;
			private List<MockMultipartFile> files;
			private List<Long> fileIds;
			private List<Long> modifyFileIds;

			@BeforeEach
			void setUp() throws IOException {
				file1 = new MockMultipartFile("files", "sample1.txt", MediaType.TEXT_PLAIN_VALUE, "file upload except test1".getBytes());
				file2 = new MockMultipartFile("files", "sample2.txt", MediaType.TEXT_PLAIN_VALUE, "file upload except test2".getBytes());
				file3 = new MockMultipartFile("files", "sample3.txt", MediaType.TEXT_PLAIN_VALUE, "file upload except test3".getBytes());
				file4 = new MockMultipartFile("files", "sample4.txt", MediaType.TEXT_PLAIN_VALUE, "file upload except test4".getBytes());
				file5 = new MockMultipartFile("files", "sample5.txt", MediaType.TEXT_PLAIN_VALUE, "file upload except test5".getBytes());
				files = List.of(file1, file2, file3);
				ExtractableResponse<Response> firstResponse = dispatcher.파일_업로드(session, files);
				fileIds = firstResponse.jsonPath().getList("fileId", Long.class);

				ExtractableResponse<Response> secondResponse = dispatcher.파일_업로드(session, List.of(file4, file5));
				modifyFileIds = secondResponse.jsonPath().getList("fileId", Long.class);
			}

			@DisplayName("파일 정보와 공지사항을 등록 할 수 있다")
			@Test
			void condition1() {
				ExtractableResponse<Response> response = dispatcher.공지사항_등록(session, NOTICE_TITLE, NOTICE_CONTENT, fileIds);

				assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
				assertThat(response.header("Location")).isNotNull();
				assertThat(response.jsonPath().getLong("noticeId")).isNotNull();

				List<AttachFileEntity> activeAttachFiles = attachFileRepository.findAllByIdIn(fileIds);
				assertThat(activeAttachFiles).allMatch(AttachFileEntity::isActive);
			}

			@DisplayName("공지사항을 10건 등록하고")
			@Nested
			class Case1_2_1 {
				private static final String NOTICE_TITLE = "파일과 공지사항 등록";
				private static final String NOTICE_CONTENT = "파일과 공지사항 등록 내용";

				private Long noticeId;
				private List<Long> noticeIds;

				@BeforeEach
				void setUp() {
					noticeIds = new ArrayList<>();
					for (int i = 1; i <= 10; i++) {
						ExtractableResponse<Response> response = dispatcher.공지사항_등록(session, NOTICE_TITLE + i, NOTICE_CONTENT + i, fileIds);
						noticeIds.add(response.jsonPath().getLong("noticeId"));
					}
					noticeId = noticeIds.get(0);
				}

				@DisplayName("공지사항 목록을 조회해도 조회수가 증가하지 않는다")
				@Test
				void condition1() {
					ExtractableResponse<Response> response = dispatcher.공지사항_목록_조회(session);

					assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
					assertThat(response.jsonPath().getList("content.noticeId", Long.class).size()).isEqualTo(noticeIds.size());
					assertThat(response.jsonPath().getList("content.viewCount", Integer.class)).allMatch(item -> item == 0);
				}

				@DisplayName("첫 공지사항을 조회하면 파일 목록을 확인할 수 있다")
				@Test
				void condition2() {
					ExtractableResponse<Response> response = dispatcher.공지사항_조회(session, noticeId);

					assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
					assertThat(response.jsonPath().getList("files.fileId", Long.class)).allMatch(fileIds::contains);
				}

				@DisplayName("첫 공지사항을 조회하면 조회수가 증가한다")
				@Test
				void condition3() {
					ExtractableResponse<Response> response = dispatcher.공지사항_조회(session, noticeId);

					assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
					assertThat(response.jsonPath().getInt("viewCount")).isEqualTo(1);
				}

				@DisplayName("첫 공지사항을 두번 조회하면 사용자가 같은 경우 조회수가 1만 증가한다")
				@Test
				void condition4() {
					dispatcher.공지사항_조회(session, noticeId);
					ExtractableResponse<Response> response = dispatcher.공지사항_조회(session, noticeId);

					assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
					assertThat(response.jsonPath().getInt("viewCount")).isEqualTo(1);
				}

				@DisplayName("첫 공지사항의 파일 정보를 대체할 수 있다")
				@Test
				void condition5() {
					ExtractableResponse<Response> modifyResponse = dispatcher.공지사항_수정(session, noticeId, modifyFileIds);
					assertThat(modifyResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

					ExtractableResponse<Response> response = dispatcher.공지사항_조회(session, noticeId);

					assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
					assertThat(response.jsonPath().getList("files.fileId", Long.class)).allMatch(modifyFileIds::contains);
					assertThat(response.jsonPath().getList("files.fileId", Long.class)).noneMatch(fileIds::contains);

					List<AttachFileEntity> activeAttachFiles = attachFileRepository.findAllByIdIn(modifyFileIds);
					List<AttachFileEntity> deactivateAttachFiles = attachFileRepository.findAllByIdIn(fileIds);
					assertThat(activeAttachFiles).allMatch(AttachFileEntity::isActive);
					assertThat(deactivateAttachFiles).allMatch(AttachFileEntity::isDeactivate);
				}

				@DisplayName("첫 공지사항을 삭제하면 첨부파일은 비활성으로 변경된다")
				@Test
				void condition6() {
					ExtractableResponse<Response> deleteResponse = dispatcher.공지사항_삭제(session, noticeId);
					assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

					ExtractableResponse<Response> response = dispatcher.공지사항_조회(session, noticeId);
					assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
					assertThat(response.jsonPath().getInt("status")).isEqualTo(HttpStatus.NO_CONTENT.value());
					assertThat(response.jsonPath().getString("code")).isEqualTo("N001");

					List<AttachFileEntity> attachFiles = attachFileRepository.findAllByIdIn(fileIds);
					assertThat(attachFiles).allMatch(AttachFileEntity::isDeactivate);
				}

				@DisplayName("다른 사용자로 로그인 후에")
				@Nested
				class Case1_2_1_1 {
					private static final String ANOTHER_EMAIL = "another@gmail.com";
					private static final String ANOTHER_PASSWORD = "1234";
					private static final String ANOTHER_NAME = "다른 테스트 사용자";

					private String anotherSession;

					@BeforeEach
					void setUp() {
						dispatcher.사용자_생성(ANOTHER_EMAIL, ANOTHER_PASSWORD, ANOTHER_NAME);
						anotherSession = dispatcher.로그인(ANOTHER_EMAIL, ANOTHER_PASSWORD);
					}

					@DisplayName("첫 공지사항을 조회하면 조회수가 증가한다")
					@Test
					void condition1() {
						ExtractableResponse<Response> response = dispatcher.공지사항_조회(anotherSession, noticeId);

						assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
						assertThat(response.jsonPath().getInt("viewCount")).isEqualTo(1);
					}

					@DisplayName("서로 같은 공지사항을 조회하면 조회수가 두번 증가한다")
					@Test
					void condition2() {
						dispatcher.공지사항_조회(session, noticeId);
						ExtractableResponse<Response> response = dispatcher.공지사항_조회(anotherSession, noticeId);

						assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
						assertThat(response.jsonPath().getInt("viewCount")).isEqualTo(2);
					}

					@DisplayName("기존 사용자의 공지사항을 수정할 수 없다")
					@Test
					void condition3() {
						ExtractableResponse<Response> response = dispatcher.공지사항_수정(anotherSession, noticeId);

						assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
						assertThat(response.jsonPath().getInt("status")).isEqualTo(HttpStatus.UNAUTHORIZED.value());
						assertThat(response.jsonPath().getString("code")).isEqualTo("N003");
					}

					@DisplayName("기존 사용자의 공지사항을 삭제할 수 없다")
					@Test
					void condition4() {
						ExtractableResponse<Response> response = dispatcher.공지사항_삭제(anotherSession, noticeId);

						assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
						assertThat(response.jsonPath().getInt("status")).isEqualTo(HttpStatus.UNAUTHORIZED.value());
						assertThat(response.jsonPath().getString("code")).isEqualTo("N003");
					}
				}
			}
		}
	}

	@DisplayName("로그인을 하지 않고")
	@Nested
	class Case2 {

		private String noSession = null;

		@DisplayName("공지사항 목록을 조회하면 정보가 없다")
		@Test
		void condition1() {
			ExtractableResponse<Response> response = dispatcher.공지사항_목록_조회(noSession);

			assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
			assertThat(response.jsonPath().getBoolean("empty")).isTrue();
			assertThat(response.jsonPath().getList("content")).isEmpty();
		}

		@DisplayName("공지사항을 조회하면 인증 에러가 발생한다")
		@Test
		void condition2() {
			ExtractableResponse<Response> response = dispatcher.공지사항_조회(noSession, NO_EXIST_NOTICE_ID);

			assertThatNoAuthentication(response);
		}

		@DisplayName("공지사항을 등록하면 인증 에러가 발생한다")
		@Test
		void condition3() {
			ExtractableResponse<Response> response = dispatcher.공지사항_등록(noSession);

			assertThatNoAuthentication(response);
		}

		@DisplayName("공지사항을 수정하면 인증 에러가 발생한다")
		@Test
		void condition4() {
			ExtractableResponse<Response> response = dispatcher.공지사항_수정(noSession, NO_EXIST_NOTICE_ID);

			assertThatNoAuthentication(response);
		}

		@DisplayName("공지사항을 삭제하면 인증 에러가 발생한다")
		@Test
		void condition5() {
			ExtractableResponse<Response> response = dispatcher.공지사항_삭제(noSession, NO_EXIST_NOTICE_ID);

			assertThatNoAuthentication(response);
		}

		@DisplayName("파일을 업로드하면 인증 에러가 발생한다")
		@Test
		void condition6() throws IOException {
			ExtractableResponse<Response> response = dispatcher.파일_업로드(noSession);

			assertThatNoAuthentication(response);
		}

		@DisplayName("파일을 다운로드하면 인증 에러가 발생한다")
		@Test
		void condition7() {
			ExtractableResponse<Response> response = dispatcher.파일_다운로드(noSession, NO_EXIST_FILE_ID);

			assertThatNoAuthentication(response);
		}

		private void assertThatNoAuthentication(ExtractableResponse<Response> response) {
			assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
			assertThat(response.jsonPath().getInt("status")).isEqualTo(401);
			assertThat(response.jsonPath().getString("code")).isEqualTo("AZ001");
		}
	}
}
