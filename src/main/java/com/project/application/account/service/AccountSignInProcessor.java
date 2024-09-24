package com.project.application.account.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.project.application.account.domain.AccountEntity;
import com.project.application.account.domain.AccountRepository;
import com.project.application.account.dto.SignInRequest;
import com.project.application.account.error.AccountErrorCode;
import com.project.core.exception.ApplicationException;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountSignInProcessor {
	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;

	public void signIn(SignInRequest request) {

		// TODO 복호화
		String plainEmail = request.email();
		String plainPassword = request.password();

		AccountEntity account = accountRepository.findByEmail(plainEmail)
			.orElseThrow(() -> new ApplicationException(AccountErrorCode.NO_SIGN_IN));

		if (!passwordEncoder.matches(plainPassword, account.getPassword())) {
			throw new ApplicationException(AccountErrorCode.NO_SIGN_IN);
		}

		HttpSession session = getSession();
		if (session.getAttribute("account") != null) {
			return;
		}
		session.setAttribute("account", account);
		session.setMaxInactiveInterval(3600);
	}

	private HttpSession getSession() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return (attributes != null) ? attributes.getRequest().getSession(false) : null;
	}
}
