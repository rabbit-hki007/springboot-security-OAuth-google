package com.study.security1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.security1.model.User;

// JpaRepository 를 상속하면 자동 컴포넌트 스캔됨.
public interface UserRepository extends JpaRepository<User, Integer>{
	
	// Jpa Naming 전략 (JPA Query Method로 구글 검색해 볼 것)
	// findBy(select)가지는 규칙이고 그다음 Username을 붙이면
	// SELECT * FROM user WHERE username = 1? 이렇게 생긴 쿼리로 조회를 함
	User findByUsername(String username);
	// SELECT * FROM user WHERE username = 1? AND password = 2?
	// User findByUsernameAndPassword(String username, String password);
	
	// @Query(value = "select * from user", nativeQuery = true)
	// User find마음대로();
}
