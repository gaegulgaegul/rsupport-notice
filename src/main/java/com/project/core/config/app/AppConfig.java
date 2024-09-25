package com.project.core.config.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.core.config.filter.CacheResponseFilter;
import com.project.core.support.crypto.Aes256Crypto;
import com.project.core.support.crypto.RsaCrypto;

@Configuration
public class AppConfig {

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		return objectMapper;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public Aes256Crypto aes256Crypto(@Value("${app.crypto.aes256.key}") String key, @Value("${app.crypto.aes256.iv}") String iv) {
		return new Aes256Crypto(key, iv);
	}

	@Bean
	public RsaCrypto rsaCrypto(@Value("${app.crypto.rsa.public-key}") String publicKey, @Value("${app.crypto.rsa.private-key}") String privateKey) {
		return new RsaCrypto(publicKey, privateKey);
	}

	@Bean
	public FilterRegistrationBean<CacheResponseFilter> cacheResponseFilter() {
		FilterRegistrationBean<CacheResponseFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new CacheResponseFilter());
		registrationBean.addUrlPatterns("/*");
		return registrationBean;
	}
}
