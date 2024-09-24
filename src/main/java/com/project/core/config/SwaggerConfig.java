package com.project.core.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
	info = @Info(
		title = "공지사항 관리",
		version = "v1",
		description = "공지사항 관리에 사용되는 API 정보입니다."
	)
)
@Configuration
public class SwaggerConfig {
}
