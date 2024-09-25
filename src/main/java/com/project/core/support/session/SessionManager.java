package com.project.core.support.session;

import java.util.Optional;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.project.core.support.annotation.Support;

import jakarta.servlet.http.HttpSession;

@Support
public class SessionManager {

	public Optional<HttpSession> session() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attributes == null) {
			return Optional.empty();
		}
		HttpSession session = attributes.getRequest().getSession(false);
		if (session == null) {
			return Optional.empty();
		}
		return Optional.of(session);
	}

	public Optional<HttpSession> sessionOrDefault() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attributes == null) {
			return Optional.empty();
		}
		HttpSession session = attributes.getRequest().getSession();
		return Optional.ofNullable(session);
	}
}
