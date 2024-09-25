package com.project.core.support.crypto;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.project.core.exception.ApplicationException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Aes256Crypto {
	private String key;
	private String iv;

	public String encrypt(String plainText) {
		try {
			byte[] decodedKey = Base64.getDecoder().decode(key);
			byte[] decodedIV = Base64.getDecoder().decode(iv);

			SecretKeySpec secretKeySpec = new SecretKeySpec(decodedKey, "AES");
			IvParameterSpec ivParameterSpec = new IvParameterSpec(decodedIV);

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

			byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());

			return Base64.getEncoder().encodeToString(encryptedBytes);
		} catch (Exception e) {
			throw new ApplicationException(CryptoErrorCode.NOT_ENCRYPT);
		}
	}

	public String decrypt(String encryptedText) {
		try {
			byte[] decodedKey = Base64.getDecoder().decode(key);
			byte[] decodedIV = Base64.getDecoder().decode(iv);
			byte[] decodedEncryptedText = Base64.getDecoder().decode(encryptedText);

			SecretKeySpec secretKeySpec = new SecretKeySpec(decodedKey, "AES");
			IvParameterSpec ivParameterSpec = new IvParameterSpec(decodedIV);

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

			return new String(cipher.doFinal(decodedEncryptedText));
		} catch (Exception e) {
			throw new ApplicationException(CryptoErrorCode.NOT_DECRYPT);
		}
	}

	protected static String generateEncryptionKey() {
		byte[] key = new byte[32];
		new SecureRandom().nextBytes(key);
		return Base64.getEncoder().encodeToString(key);
	}

	protected static String generateIVKey() {
		byte[] iv = new byte[16];
		new SecureRandom().nextBytes(iv);
		return Base64.getEncoder().encodeToString(iv);
	}
}
