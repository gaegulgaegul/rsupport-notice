package com.project.application.account.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.application.account.domain.AccountEntity;
import com.project.application.account.domain.AccountRepository;
import com.project.application.account.dto.SignInRequest;
import com.project.application.account.error.SignErrorCode;
import com.project.application.account.vo.Account;
import com.project.core.exception.ApplicationException;
import com.project.core.support.crypto.RsaCrypto;
import com.project.core.support.session.SessionManager;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountSignInProcessor {
	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;
	private final SessionManager sessionManager;
	private final RsaCrypto rsaCrypto;

	public void signIn(SignInRequest request) {
		String plainEmail = rsaCrypto.decrypt(request.email());
		String plainPassword = rsaCrypto.decrypt(request.password());

		AccountEntity account = accountRepository.findByEmail(plainEmail)
			.orElseThrow(() -> new ApplicationException(SignErrorCode.INVALID));

		if (!passwordEncoder.matches(plainPassword, account.getPassword())) {
			throw new ApplicationException(SignErrorCode.INVALID);
		}

		HttpSession session = sessionManager.sessionOrDefault()
			.orElseThrow(() -> new ApplicationException(SignErrorCode.NO_SESSION));
		if (session != null && session.getAttribute("account") != null) {
			return;
		}

		Account build = Account.builder()
			.id(account.getId())
			.email(account.getEmail())
			.name(account.getName())
			.build();
		session.setAttribute("account", build);
		session.setMaxInactiveInterval(3600);
	}
}
