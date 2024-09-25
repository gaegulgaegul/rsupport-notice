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
public class CacheResponseFilter implements Filter {

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
		HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

		ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(httpResponse);

		filterChain.doFilter(httpRequest, responseWrapper);

		byte[] content = responseWrapper.getContentAsByteArray();
		String responseBody = new String(content, responseWrapper.getCharacterEncoding());
		String modifiedResponseBody = responseBody.replaceAll("\"@class\":\"[^\"]*\",?", "");

		httpResponse.setContentLength(modifiedResponseBody.length());
		httpResponse.getOutputStream().write(modifiedResponseBody.getBytes(responseWrapper.getCharacterEncoding()));

		responseWrapper.copyBodyToResponse();
	}
}
