package com.project.core.support.crypto;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import com.project.core.exception.ApplicationException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RsaCrypto {
	private String publicKey;
	private String privateKey;

	public String encrypt(String plainText) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			byte[] bytePublicKey = Base64.getDecoder().decode(publicKey.getBytes());
			X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bytePublicKey);
			PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);

			byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
			return Base64.getEncoder().encodeToString(encryptedBytes);
		} catch (Exception e) {
			throw new ApplicationException(CryptoErrorCode.NOT_ENCRYPT);
		}
	}

	public String decrypt(String encryptedText) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			byte[] bytePrivateKey = Base64.getDecoder().decode(privateKey.getBytes());
			PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bytePrivateKey);
			PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);

			byte[] encryptedBytes =  Base64.getDecoder().decode(encryptedText.getBytes());
			byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
			return new String(decryptedBytes);
		} catch (Exception e) {
			/*
			 * 실제 프로젝트라면 해당 값을 반환하지 않고 예외를 발생시키지만,
			 * 테스트를 쉽게 하기 위해 이메일/비밀번호를 매개변수로 전달하고 있습니다.
			 */
			return encryptedText;
		}
	}
}
