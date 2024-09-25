package com.project.application.account.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Account implements Serializable {
	public static final long serialVersionUID = 1L;
	public static final Account DEFAULT = Account.builder()
		.id(0L)
		.email("account@gmail.com")
		.name("기본 사용자")
		.build();

	private Long id;
	private String email;
	private String name;

	public void copy(Account that) {
		this.id = that.id;
		this.email = that.email;
		this.name = that.name;
	}
}
