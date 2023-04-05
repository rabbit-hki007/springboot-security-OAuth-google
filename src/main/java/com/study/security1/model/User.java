package com.study.security1.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// ORM - Object Relation Mapping


@Entity
@Data
@NoArgsConstructor
public class User {
	
	@Id // primary key
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String username;
	private String password;
	private String email;
	private String role; //ROLE_USER, ROLE_ADMIN
	@CreationTimestamp
	private Timestamp createDate;
	
	// OAuth를 위해 구성한 추가 필드 2개 시작
	private String provider;
	private String providerId;
	// OAuth를 위해 구성한 추가 필드 2개 끝
	
	// OAuth 로그인 강제 가입을 위한 생성자 Builder 패턴
	@Builder
	public User( String username, String password, String email, String role, Timestamp createDate,
			String provider, String providerId) {
		//super();
		this.username = username;
		this.password = password;
		this.email = email;
		this.role = role;
		this.createDate = createDate;
		this.provider = provider;
		this.providerId = providerId;
	}
	
	
	
}
