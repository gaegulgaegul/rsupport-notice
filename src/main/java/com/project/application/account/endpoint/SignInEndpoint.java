package com.project.application.account.endpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "인증 API")
@RestController
@RequiredArgsConstructor
class SignInEndpoint {

	private final AccountSignInProcessor accountSignInProcessor;

	@Operation(summary = "로그인")
	@PostMapping("/api/sign/in")
	ResponseEntity<?> signIn() {
		accountSignInProcessor.signIn();
		return ResponseEntity.ok().build();
	}
}
