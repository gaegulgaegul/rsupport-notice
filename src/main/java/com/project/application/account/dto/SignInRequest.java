package com.project.application.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record SignInRequest(
	@NotEmpty
	@Schema(description = "이메일", example = "user1@gmail.com")
	String email,
	@NotEmpty
	@Schema(description = "비밀번호", example = "1234")
	String password
) {

}
