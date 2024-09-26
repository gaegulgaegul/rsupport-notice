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
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
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

	@Nested
	class 로그인을_하고 {
		private String session;

		@BeforeEach
		void setUp() {
			session = dispatcher.로그인(EMAIL, PASSWORD);
		}

		@Test
		void 공지사항_목록을_조회하면_정보가_없다() {
			ExtractableResponse<Response> response = dispatcher.공지사항_목록_조회(session);

			assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
			assertThat(response.jsonPath().getBoolean("empty")).isTrue();
			assertThat(response.jsonPath().getList("content")).isEmpty();
		}

		@Test
		void 등록되지_않은_공지사항을_조회_할_수_없다() {
			ExtractableResponse<Response> response = dispatcher.공지사항_조회(session, NO_EXIST_NOTICE_ID);

			assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
			assertThat(response.jsonPath().getInt("status")).isEqualTo(HttpStatus.NO_CONTENT.value());
			assertThat(response.jsonPath().getString("code")).isEqualTo("N001");
		}

		@Test
		void 공지사항을_등록_할_수_있다() {
			ExtractableResponse<Response> response = dispatcher.공지사항_등록(session, "공지사항 등록", "공지사항 등록 내용");

			assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
			assertThat(response.header("Location")).isNotNull();
			assertThat(response.jsonPath().getLong("noticeId")).isNotNull();
		}

		@Test
		void 등록되지_않은_공지사항을_수정_할_수_없다() {
			ExtractableResponse<Response> response = dispatcher.공지사항_수정(session, NO_EXIST_NOTICE_ID);

			assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
			assertThat(response.jsonPath().getInt("status")).isEqualTo(HttpStatus.NO_CONTENT.value());
			assertThat(response.jsonPath().getString("code")).isEqualTo("N001");
		}

		@Test
		void 등록되지_않은_공지사항을_식제_할_수_없다() {
			ExtractableResponse<Response> response = dispatcher.공지사항_삭제(session, NO_EXIST_NOTICE_ID);

			assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
			assertThat(response.jsonPath().getInt("status")).isEqualTo(HttpStatus.NO_CONTENT.value());
			assertThat(response.jsonPath().getString("code")).isEqualTo("N001");
		}

		@Nested
		class 공지사항을_등록하면 {
			private Long noticeId;

			@BeforeEach
			void setUp() {
				ExtractableResponse<Response> response = dispatcher.공지사항_등록(session);
				noticeId = response.jsonPath().getLong("noticeId");
			}

			@Test
			void 공지사항을_조회_할_수_있다() {
				ExtractableResponse<Response> response = dispatcher.공지사항_조회(session, noticeId);

				String actualTitle = "공지사항 등록 제목";
				String actualContent = "등록 내용";

				assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
				assertThat(response.jsonPath().getString("title")).isEqualTo(actualTitle);
				assertThat(response.jsonPath().getString("content")).isEqualTo(actualContent);
			}

			@Test
			void 공지사항을_수정_할_수_있다() {
				String modifyTitle = "공지사항 수정";
				String modifyContent = "공지사항 내용 수정";

				ExtractableResponse<Response> modifyResponse = dispatcher.공지사항_수정(session, noticeId, modifyTitle, modifyContent);
				assertThat(modifyResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

				ExtractableResponse<Response> readResponse = dispatcher.공지사항_조회(session, noticeId);

				assertThat(readResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
				assertThat(readResponse.jsonPath().getString("title")).isEqualTo(modifyTitle);
				assertThat(readResponse.jsonPath().getString("content")).isEqualTo(modifyContent);
			}

			@Test
			void 공지사항을_삭제_할_수_있다() {
				ExtractableResponse<Response> deleteResponse = dispatcher.공지사항_삭제(session, noticeId);
				assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

				ExtractableResponse<Response> response = dispatcher.공지사항_조회(session, noticeId);
				assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
				assertThat(response.jsonPath().getInt("status")).isEqualTo(HttpStatus.NO_CONTENT.value());
				assertThat(response.jsonPath().getString("code")).isEqualTo("N001");
			}
		}

		@Nested
		class 여러_파일을_업로드_후 {
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

			@Test
			void 파일_정보와_공지사항을_등록_할_수_있다() {
				ExtractableResponse<Response> response = dispatcher.공지사항_등록(session, NOTICE_TITLE, NOTICE_CONTENT, fileIds);

				assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
				assertThat(response.header("Location")).isNotNull();
				assertThat(response.jsonPath().getLong("noticeId")).isNotNull();

				List<AttachFileEntity> activeAttachFiles = attachFileRepository.findAllByIdIn(fileIds);
				assertThat(activeAttachFiles).allMatch(AttachFileEntity::isActive);
			}

			@Nested
			class 공지사항을_10건_등록하고 {
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

				@Test
				void 공지사항_목록을_조회해도_조회수가_증가하지_않는다() {
					ExtractableResponse<Response> response = dispatcher.공지사항_목록_조회(session);

					assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
					assertThat(response.jsonPath().getList("content.noticeId", Long.class).size()).isEqualTo(noticeIds.size());
					assertThat(response.jsonPath().getList("content.viewCount", Integer.class)).allMatch(item -> item == 0);
				}

				@Test
				void 첫_공지사항을_조회하면_파일_목록을_확인할_수_있다() {
					ExtractableResponse<Response> response = dispatcher.공지사항_조회(session, noticeId);

					assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
					assertThat(response.jsonPath().getList("files.fileId", Long.class)).allMatch(fileIds::contains);
				}

				@Test
				void 첫_공지사항을_조회하면_조회수가_증가한다() {
					ExtractableResponse<Response> response = dispatcher.공지사항_조회(session, noticeId);

					assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
					assertThat(response.jsonPath().getInt("viewCount")).isEqualTo(1);
				}

				@Test
				void 첫_공지사항을_두번_조회하면_사용자가_같은_경우_조회수가_1만_증가한다() {
					dispatcher.공지사항_조회(session, noticeId);
					ExtractableResponse<Response> response = dispatcher.공지사항_조회(session, noticeId);

					assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
					assertThat(response.jsonPath().getInt("viewCount")).isEqualTo(1);
				}

				@Test
				void 첫_공지사항의_파일_정보를_대체할_수_있다() {
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

				@Test
				void 첫_공지사항을_삭제하면_첨부파일은_비활성으로_변경된다() {
					ExtractableResponse<Response> deleteResponse = dispatcher.공지사항_삭제(session, noticeId);
					assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

					ExtractableResponse<Response> response = dispatcher.공지사항_조회(session, noticeId);
					assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
					assertThat(response.jsonPath().getInt("status")).isEqualTo(HttpStatus.NO_CONTENT.value());
					assertThat(response.jsonPath().getString("code")).isEqualTo("N001");

					List<AttachFileEntity> attachFiles = attachFileRepository.findAllByIdIn(fileIds);
					assertThat(attachFiles).allMatch(AttachFileEntity::isDeactivate);
				}

				@Nested
				class 다른_사용자로_로그인_후에 {
					private static final String ANOTHER_EMAIL = "another@gmail.com";
					private static final String ANOTHER_PASSWORD = "1234";
					private static final String ANOTHER_NAME = "다른 테스트 사용자";

					private String anotherSession;

					@BeforeEach
					void setUp() {
						dispatcher.사용자_생성(ANOTHER_EMAIL, ANOTHER_PASSWORD, ANOTHER_NAME);
						anotherSession = dispatcher.로그인(ANOTHER_EMAIL, ANOTHER_PASSWORD);
					}

					@Test
					void 기존_사용자의_첫_공지사항을_조회하면_조회수가_증가한다() {
						ExtractableResponse<Response> response = dispatcher.공지사항_조회(anotherSession, noticeId);

						assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
						assertThat(response.jsonPath().getInt("viewCount")).isEqualTo(1);
					}

					@Test
					void 기존_사용자와_같은_공지사항을_조회하면_조회수가_두번_증가한다() {
						dispatcher.공지사항_조회(session, noticeId);
						ExtractableResponse<Response> response = dispatcher.공지사항_조회(anotherSession, noticeId);

						assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
						assertThat(response.jsonPath().getInt("viewCount")).isEqualTo(2);
					}

					@Test
					void 기존_사용자의_공지사항을_수정할_수_없다() {
						ExtractableResponse<Response> response = dispatcher.공지사항_수정(anotherSession, noticeId);

						assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
						assertThat(response.jsonPath().getInt("status")).isEqualTo(HttpStatus.UNAUTHORIZED.value());
						assertThat(response.jsonPath().getString("code")).isEqualTo("N003");
					}

					@Test
					void 기존_사용자의_공지사항을_삭제할_수_없다() {
						ExtractableResponse<Response> response = dispatcher.공지사항_삭제(anotherSession, noticeId);

						assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
						assertThat(response.jsonPath().getInt("status")).isEqualTo(HttpStatus.UNAUTHORIZED.value());
						assertThat(response.jsonPath().getString("code")).isEqualTo("N003");
					}
				}
			}
		}
	}

	@Nested
	class 로그인을_하지_않고 {

		private String noSession = null;

		@Test
		void 공지사항_목록을_조회하면_정보가_없다() {
			ExtractableResponse<Response> response = dispatcher.공지사항_목록_조회(noSession);

			assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
			assertThat(response.jsonPath().getBoolean("empty")).isTrue();
			assertThat(response.jsonPath().getList("content")).isEmpty();
		}

		@Test
		void 공지사항을_조회하면_인증_에러가_발생한다() {
			ExtractableResponse<Response> response = dispatcher.공지사항_조회(noSession, NO_EXIST_NOTICE_ID);

			assertThatNoAuthentication(response);
		}

		@Test
		void 공지사항을_등록하면_인증_에러가_발생한다() {
			ExtractableResponse<Response> response = dispatcher.공지사항_등록(noSession);

			assertThatNoAuthentication(response);
		}

		@Test
		void 공지사항을_수정하면_인증_에러가_발생한다() {
			ExtractableResponse<Response> response = dispatcher.공지사항_수정(noSession, NO_EXIST_NOTICE_ID);

			assertThatNoAuthentication(response);
		}

		@Test
		void 공지사항을_삭제하면_인증_에러가_발생한다() {
			ExtractableResponse<Response> response = dispatcher.공지사항_삭제(noSession, NO_EXIST_NOTICE_ID);

			assertThatNoAuthentication(response);
		}

		@Test
		void 파일을_업로드하면_인증_에러가_발생한다() throws IOException {
			ExtractableResponse<Response> response = dispatcher.파일_업로드(noSession);

			assertThatNoAuthentication(response);
		}

		@Test
		void 파일을_다운로드하면_인증_에러가_발생한다() {
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
