package com.project.core.support.jpa;

import com.project.core.support.annotation.Support;
import com.project.core.support.crypto.Aes256Crypto;

import jakarta.persistence.AttributeConverter;
import lombok.RequiredArgsConstructor;

@Support
@RequiredArgsConstructor
public class CryptoConverter implements AttributeConverter<String, String> {
	private final Aes256Crypto aes256Crypto;

	@Override
	public String convertToDatabaseColumn(String attribute) {
		if (attribute == null) {
			return null;
		}
		return aes256Crypto.encrypt(attribute);
	}

	@Override
	public String convertToEntityAttribute(String dbData) {
		if (dbData == null) {
			return null;
		}
		return aes256Crypto.decrypt(dbData);
	}
}
