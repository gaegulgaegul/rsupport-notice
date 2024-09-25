package com.project.application.account.service;

import org.springframework.stereotype.Service;

import com.project.core.support.session.SessionManager;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountSignOutProcessor {
	private final SessionManager sessionManager;

	public void signOut() {
		sessionManager.session()
			.ifPresent(HttpSession::invalidate);
	}
}
