package com.project.core.config.filter;

import java.io.IOException;

import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 레디스 캐시 설정 필드 제외 필터
 */
public class RedisCacheResponseFilter implements Filter {

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
		HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

		/*
		 * ContentCachingResponseWrapper
		 * 응답 본문을 캐싱하여, 필터나 핸들러에서 응답 데이터를 수정할 수 있도록 한다.
		 * 응답을 생성한 후에 본문을 변경하거나 추가적인 처리를 할 수 있다.
		 * 내부 메모리에 응답 본문을 저장하여 대량의 데이터 응답을 처리할 때는 메모리 사용량에 주의
		 */
		ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(httpResponse);

		filterChain.doFilter(httpRequest, responseWrapper);

		/* 응답 본문을 가져와 @class 필드를 제외한다. */
		byte[] content = responseWrapper.getContentAsByteArray();
		String responseBody = new String(content, responseWrapper.getCharacterEncoding());
		String modifiedResponseBody = responseBody.replaceAll("\"@class\":\"[^\"]*\",?", "");

		/* 수정한 응답으로 결과를 전달한다. */
		httpResponse.setContentLength(modifiedResponseBody.length());
		httpResponse.getOutputStream().write(modifiedResponseBody.getBytes(responseWrapper.getCharacterEncoding()));

		responseWrapper.copyBodyToResponse();
	}
}
