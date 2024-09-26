package com.project.core.config.authorization;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.project.application.account.vo.Account;
import com.project.core.exception.ApplicationException;
import com.project.core.support.session.SessionManager;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthorizationAspect {
	private final SessionManager sessionManager;

	@Pointcut("@annotation(com.project.core.support.annotation.Authorization)")
	public void pointCut() {}

	@Before("pointCut()")
	public void around(JoinPoint joinPoint) {
		HttpSession session = sessionManager.session()
			.orElseThrow(() -> new ApplicationException(AuthorizationErrorCode.NO_SIGN_IN));

		Account account = (Account) session.getAttribute("account");

		for (Object arg : joinPoint.getArgs()) {
			if (arg instanceof Account) {
				((Account)arg).copy(account);
			}
		}
	}
}
