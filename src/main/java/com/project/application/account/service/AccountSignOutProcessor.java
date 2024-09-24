package com.project.application.account.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

@Service
public class AccountSignOutProcessor {

	public void signOut() {
		HttpSession session = getSession();
		if (session == null) {
			return;
		}

		session.invalidate();
	}

	private HttpSession getSession() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return (attributes != null) ? attributes.getRequest().getSession(false) : null;
	}
}
