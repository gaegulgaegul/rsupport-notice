package com.project.core.support.crypto;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;

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
			throw new ApplicationException(CryptoErrorCode.NOT_DECRYPT);
		}
	}

	/*
	 * 공개키와 개인키 한 쌍 생성
	 */
	public static HashMap<String, String> createKeypairAsString() {
		HashMap<String, String> stringKeypair = new HashMap<>();

		try {
			SecureRandom secureRandom = new SecureRandom();
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048, secureRandom);
			KeyPair keyPair = keyPairGenerator.genKeyPair();

			PublicKey publicKey = keyPair.getPublic();
			PrivateKey privateKey = keyPair.getPrivate();

			String stringPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
			String stringPrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());

			stringKeypair.put("publicKey", stringPublicKey);
			stringKeypair.put("privateKey", stringPrivateKey);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return stringKeypair;
	}
}
