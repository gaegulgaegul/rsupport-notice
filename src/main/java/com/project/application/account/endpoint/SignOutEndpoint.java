package com.project.application.account.endpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.application.account.service.AccountSignOutProcessor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "인증 API")
@RestController
@RequiredArgsConstructor
class SignOutEndpoint {

	private final AccountSignOutProcessor accountSignOutProcessor;

	@Operation(summary = "로그아웃")
	@PostMapping("/api/sign/out")
	ResponseEntity<Void> signOut() {
		accountSignOutProcessor.signOut();
		return ResponseEntity.ok().build();
	}
}
