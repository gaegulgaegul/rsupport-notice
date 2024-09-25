package com.project;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.project.application.account.domain.AccountEntity;
import com.project.application.account.domain.AccountRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class initData implements ApplicationRunner {
	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		String decryptedPassword = passwordEncoder.encode("1234");
		for (int i = 1; i < 10; i++) {
			AccountEntity account = AccountEntity.builder()
				.email("user%d@gmail.com".formatted(i))
				.password(decryptedPassword)
				.name("사용자%d".formatted(i))
				.build();
			accountRepository.save(account);
		}
	}
}
