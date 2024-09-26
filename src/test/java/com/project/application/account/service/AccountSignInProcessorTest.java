package com.project.application.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.application.account.domain.AccountEntity;
import com.project.application.account.domain.AccountRepository;
import com.project.application.account.dto.SignInRequest;
import com.project.application.account.vo.Account;
import com.project.core.exception.ApplicationException;
import com.project.core.support.session.SessionManager;

import jakarta.servlet.http.HttpSession;

@DisplayName("AES256 암복호화 기능 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest
class AccountSignInProcessorTest {

	@Autowired private AccountRepository accountRepository;
	@Autowired private PasswordEncoder passwordEncoder;
	@Autowired private SessionManager sessionManager;

	@Autowired private AccountSignInProcessor sut;

	private AccountEntity account;

	@BeforeEach
	void setUp() {
		accountRepository.deleteAll();

		String password = passwordEncoder.encode("1234");
		account = new AccountEntity(null, "signin@gmail.com", password, "로그인테스트");
		accountRepository.save(account);
	}

	@Test
	void 존재하지_않는_이메일_사용자는_로그인_시_예외발생() {
		SignInRequest request = new SignInRequest("empty@gmail.com", "1234");
		assertThatThrownBy(() -> sut.signIn(request))
			.isInstanceOf(ApplicationException.class);
	}

	@Test
	void 비밀번호가_일치하지_않는_사용자는_로그인_시_예외발생() {
		SignInRequest request = new SignInRequest("signin@gmail.com", "5678");
		assertThatThrownBy(() -> sut.signIn(request))
			.isInstanceOf(ApplicationException.class);
	}

	@Test
	void 이메일과_비밀번호가_일치하는_사용자면_로그인된다() {
		SignInRequest request = new SignInRequest("signin@gmail.com", "1234");

		sut.signIn(request);

		HttpSession session = sessionManager.session().get();
		Account account = (Account)session.getAttribute("account");
		assertThat(account).isNotNull();
		assertThat(account.getEmail()).isEqualTo("signin@gmail.com");
		assertThat(account.getName()).isEqualTo("로그인테스트");
	}

	@Test
	void 이미_로그인한_사용자면_반환된다() {
		SignInRequest request = new SignInRequest("signin@gmail.com", "1234");

		sut.signIn(request);
		HttpSession firstSession = sessionManager.session().get();

		sut.signIn(request);
		HttpSession secondSession = sessionManager.session().get();

		assertThat(firstSession).isEqualTo(secondSession);
	}
}