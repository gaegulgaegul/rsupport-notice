package com.project.core.support.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayName("AES256 암복호화 기능 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class Aes256CryptoTest {

	private Aes256Crypto sut = new Aes256Crypto("fUFlthxpeceJVBqcYp+XAM/izCw17H3q7UIy8wRT4fQ=", "kyULcMvM4oYyS6xncKWA5w==");

	@Test
	void 평문을_암호화_할_수_있다() {
		String plainText = "테스트";
		String cipherText = sut.encrypt(plainText);
		assertThat(cipherText).isEqualTo("7FjAX2fC43n/CL6i5iffzw==");
	}

	@Test
	void 암호화문을_복호화_할_수_있다() {
		String cipherText = "7FjAX2fC43n/CL6i5iffzw==";
		String plainText = sut.decrypt(cipherText);
		assertThat(plainText).isEqualTo("테스트");
	}
}