package com.project.application.account.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.application.account.vo.Account;
import com.project.core.support.session.SessionManager;

import jakarta.servlet.http.HttpSession;

@DisplayName("AES256 암복호화 기능 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest
class AccountSignOutProcessorTest {
	@Autowired private SessionManager sessionManager;

	@Autowired private AccountSignOutProcessor sut;

	private Account account = new Account(1L, "signin@gmail.com", "로그인테스트");

	@Test
	void 로그인된_사용자는_정상적으로_로그아웃된다() {
		HttpSession session = sessionManager.sessionOrDefault().get();
		session.setAttribute("account", account);
		session.setMaxInactiveInterval(3600);

		sut.signOut();

		assertThat(sessionManager.session()).isEmpty();
	}

	@Test
	void 로그아웃을_중복해도_예외는_발생하지_않는다() {
		HttpSession session = sessionManager.sessionOrDefault().get();
		session.setAttribute("account", account);
		session.setMaxInactiveInterval(3600);

		sut.signOut();
		sut.signOut();

		assertThat(sessionManager.session()).isEmpty();
	}
}