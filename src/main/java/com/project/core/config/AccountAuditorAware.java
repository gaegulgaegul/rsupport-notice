package com.project.core.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import com.project.application.account.vo.Account;
import com.project.core.support.session.SessionManager;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountAuditorAware implements AuditorAware<Long> {
	private final SessionManager sessionManager;

	@Override
	public Optional<Long> getCurrentAuditor() {
		Account account = sessionManager.session()
			.map(session -> (Account)session.getAttribute("account"))
			.orElse(Account.DEFAULT);
		return Optional.of(account.getId());
	}
}
