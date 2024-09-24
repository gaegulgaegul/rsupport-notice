package com.project.core.authorization;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.project.application.account.vo.Account;
import com.project.core.exception.ApplicationException;

import jakarta.servlet.http.HttpSession;

@Aspect
@Component
public class AuthorizationAspect {

	@Pointcut("@annotation(com.project.core.authorization.Authorization)")
	public void pointCut() {}

	@Before("pointCut()")
	public void around(JoinPoint joinPoint) {
		HttpSession session = getSession();
		if (session == null) {
			throw new ApplicationException(AuthorizationErrorCode.NO_SIGN_IN);
		}

		Account account = (Account) session.getAttribute("account");

		for (Object arg : joinPoint.getArgs()) {
			if (arg instanceof Account) {
				((Account)arg).copy(account);
			}
		}
	}

	private HttpSession getSession() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return (attributes != null) ? attributes.getRequest().getSession(false) : null;
	}
}
