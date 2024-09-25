package com.project.application.account.domain;

import com.project.core.support.jpa.CryptoConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_account")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class AccountEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Convert(converter = CryptoConverter.class)
	@Column(name = "email", length = 100, nullable = false, unique = true)
	private String email;

	@Column(name = "password", length = 255, nullable = false)
	private String password;

	@Convert(converter = CryptoConverter.class)
	@Column(name = "name", length = 255, nullable = false)
	private String name;
}
