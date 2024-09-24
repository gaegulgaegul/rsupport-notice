package com.project.application.account.endpoint;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.application.account.dto.SignInRequest;
import com.project.application.account.service.AccountSignInProcessor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "인증 API")
@RestController
@RequiredArgsConstructor
class SignInEndpoint {

	private final AccountSignInProcessor accountSignInProcessor;

	@Operation(summary = "로그인")
	@PostMapping(value = "/api/sign/in")
	ResponseEntity<Void> signIn(@RequestBody @Valid SignInRequest request) {
		accountSignInProcessor.signIn(request);
		return ResponseEntity.ok().build();
	}
}
