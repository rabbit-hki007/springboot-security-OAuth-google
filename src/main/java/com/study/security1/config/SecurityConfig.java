package com.study.security1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.web.server.AuthenticatedPrincipalServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.SecurityFilterChain;

import com.study.security1.config.oauth.PrincipalOauth2UserService;

//@SuppressWarnings("deprecation")
@Configuration
//아래는 특정 주소 접근시 권한 및 인증을 위한 어노테이션 활성화 Controller에서 @Secured와 @PreAuthorize 를 쓸수 있게 해준다
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) 
public class SecurityConfig {
	
	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService; // DI 한 이유는 후처리를 한 놈들 .userService(principalOauth2UserService)에설 리턴한다
	
	@Bean
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.csrf().disable();
		http.authorizeRequests()
			.antMatchers("/user/**").authenticated() //인증만 되면 들어갈수 있는 주소
			.antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')") // 권한이 있어야 들어간다 권한없으면 403에러 발생
			.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')") // 권한이 있어야 들어간다 권한없으면 403에러 발생
			.anyRequest().permitAll()
		.and()
			.formLogin()
			//.usernameParameter("id") 
			.loginPage("/loginForm")
			.loginProcessingUrl("/loginProc")
			.defaultSuccessUrl("/")
		.and()
	        .logout().permitAll()
	        .logoutSuccessUrl("/")
		.and()
			.oauth2Login()
			.loginPage("/loginForm") //여기까지만 하면 구글 로그인 기능은 완료되나 구글 로그인이 완료된뒤 권한에 대한 후처리가 필요함
			// 구글 로그인이 완료된 뒤에 후처리가 필요함 1. 코드받기(인증) 2.엑세스 토큰(권한) 3.사용자 프로필 정보를 가져오고 
			// 4-1 그 정보를 토대로 회원가입을 자동으로 진행시키기도 함
			// 4-2 (이메일,전화번호,이름,아이디)쇼핑몰 -> (집주소), 백화점 -> VIP등급
			// 구글 로그인이 완료된 뒤의 후처리로 코드를 받는 것이 아니라 (액세스토큰+사용자프로필을 한방에 받게 된다)
			// 이것이 OAuth의 개편함이다
			.userInfoEndpoint()
			.userService(principalOauth2UserService); 
		
		return http.build();	
 
	}
	
//  옛날 방식임	
//	@Configuration // IoC 빈(bean)을 등록
//	@EnableWebSecurity // 필터 체인 관리 시작 어노테이션
//  아래는 특정 주소 접근시 권한 및 인증을 위한 어노테이션 활성화 Controller에서 @Secured와 @PreAuthorize 를 쓸수 있게 해준다
//	@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) // 특정 주소 접근시 권한 및 인증을 위한 어노테이션 활성화
//	public class SecurityConfig extends WebSecurityConfigurerAdapter{
//		
//		@Bean
//		public BCryptPasswordEncoder encodePwd() {
//			return new BCryptPasswordEncoder();
//		}
//		
//		@Override
//		protected void configure(HttpSecurity http) throws Exception {
//			
//			http.csrf().disable();
//			http.authorizeRequests()
//				.antMatchers("/user/**").authenticated()
//				//.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
//				//.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN') and hasRole('ROLE_USER')")
//				.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
//				.anyRequest().permitAll()
//			.and()
//				.formLogin()
//				.loginPage("/login")
//				.loginProcessingUrl("/loginProc")
//				.defaultSuccessUrl("/")
//	        .and()
//              .logout().permitAll()
//              .logoutSuccessUrl("/");
//		}
	
	
	
}





