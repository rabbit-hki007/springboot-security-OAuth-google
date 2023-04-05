package com.study.security1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.study.security1.config.auth.PrincipalDetails;
import com.study.security1.model.User;
import com.study.security1.repository.UserRepository;


@Controller
public class IndexController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping({ "", "/" })
	public String index() {
		return "index";
	}
	
	// 일반적인 로그인을 해도 PrincipalDetails
	// OAuth 로그인을 해도 PrincipalDetails
	// @AuthenticationPrincipal 이 어노테이션 만으로 접근 가능해짐
	// 해당 어노테이션의 활성화 되는 시점은 
	// 너무 편함 따로 분기할 필요가 없어 졌음
	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("PrincipalDetails : " + principalDetails.getUser());
		return "user";
	
	}
	
	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "admin";
	
	}
	
	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	
	}
	
	@GetMapping("/loginForm")
	public String loginForm() {
		return "loginForm";
	
	}
	
	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	
	}
	
	@PostMapping("/joinProc")
	public String joinProc(User user) {
        System.out.println("회원가입 진행 : " + user);
        String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		user.setRole("ROLE_USER");
		userRepository.save(user);
		return "redirect:/loginForm";	
	}
	
	// ROLE_ADMIN 권한만 접속 가능한 링크 - 한개의 권한만 접속 가능하도록 함
	@Secured("ROLE_ADMIN") //SecurityConfig에서 @EnableGlobalMethodSecurity(securedEnabled = true) 로 설정했을때 가능 
	@GetMapping("/info")
	public @ResponseBody  String info() {
		return "개인정보";
	}
	
	// ROLE_MANAGER  ROLE_ADMIN 여러개의 권한이 접속 가능하도록 함
	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')") //SecurityConfig에서@EnableGlobalMethodSecurity(prePostEnabled = true) 로 설정했을때 가능 
	@GetMapping("/data")
	public @ResponseBody  String data() {
		return "데이타 정보";
	}
	
	//////////////OAuth2 Login 테스트를 위한 부분입니다///////////////
	@GetMapping("/test/login")
	public @ResponseBody  String test(Authentication authentication,
			                           @AuthenticationPrincipal UserDetails userDetails,
			                           @AuthenticationPrincipal PrincipalDetails principalDetails1) {  //DI 의존성 주입
		System.out.println("=========일번 로그인 테스트 로그인 ===============");
		//System.out.println("authentication : " + authentication.getPrincipal()); //리턴 타입이 오브젝트임 그래서 아래처럼 다운케스팅 해주었음 그리고 PrincipalDetails 클래스에 @Data로 셋터를 적용해야 함 
		PrincipalDetails principalDetails =(PrincipalDetails)authentication.getPrincipal(); //authentication이 반환타입이 object 타입이라서 PrincipalDetails로 다운케스팅 하였음 그리고 PrincipalDetails에 @Data를 하여 getter와 setter를 생성하였음
		System.out.println("authentication : " + principalDetails.getUser());
		
		//@AuthenticationPrincipal 어노테이션으로 세션정보 접근 가능함 PrincipalDetails는 UserDetails의 impliments 이기 때문에
		System.out.println("principalDetails.getUser() : " + principalDetails1.getUser());
		
		//@AuthenticationPrincipal 어노테이션으로 세션정보 접근 가능함
		System.out.println("userDetails.getUsername() : " + userDetails.getUsername());
		
		
		return "일반 로그인 세션정보 확인하기";
	}
	
	@GetMapping("/test/oauth/login")
	public @ResponseBody  String oauthLogintest(Authentication authentication,
			                                    @AuthenticationPrincipal OAuth2User oauth2User) {  //DI 의존성 주입
		System.out.println("=========OAuth 로그인 테스트 로그인 ===============");
		//System.out.println("authentication : " + authentication.getPrincipal()); //리턴 타입이 오브젝트임 그래서 아래처럼 다운케스팅 해주었음 그리고 PrincipalDetails 클래스에 @Data로 셋터를 적용해야 함 
		
		//구글로 로그인 할 경우 PrincipalDetails로 케스팅이 안됨 이때는 OAuth2U로 케스팅 해야함
		//PrincipalDetails principalDetails =(PrincipalDetails)authentication.getPrincipal(); // 이건 구글로 로그인시 에러 발생 왜냐하면 캐스팅을 할수가 없음
		OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
		System.out.println("authentication : " + oAuth2User.getAttributes()); //getAttributes는 MAP<String, Object> 타입임
		
		//어노테이션으로 접근
		System.out.println("oauth2User : " + oauth2User.getAttributes());

		return "OAuth 로그인 세션정보 확인하기";
	}
	// 결국 일반 로그인과 OAuth로그인 둘다를 위해 
	// public class PrincipalDetails implements UserDetails, OAuth2User{ // 이렇게 일반 로그인과 oauth로그인을 둘다 풀고 있어야 한다
	// }
	//////////////OAuth2 Login 테스트를 위한 끝 부분입니다///////////////
}
