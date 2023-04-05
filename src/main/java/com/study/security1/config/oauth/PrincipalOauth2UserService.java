package com.study.security1.config.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.study.security1.config.auth.PrincipalDetails;
import com.study.security1.model.User;
import com.study.security1.repository.UserRepository;

import antlr.TokenWithIndex;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

	// 구글 로그인 패스워드 강제 인코딩 비밀번호를 강제로 박아줌
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	// 이미 가입된 구글 사용자인지 확인을 위한 검색 처리를 위해 @Autowired
	@Autowired
	private UserRepository userRepository;
	
	// 구글로 부터 받은 userRequest 데이터에 대한 후처리 함수
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		//System.out.println("PrincipalOauth2UserService의 loadUser()의 userRequest : " + userRequest);
		System.out.println("PrincipalOauth2UserService의 loadUser()의 userRequest의 getClientRegistration : " + userRequest.getClientRegistration()); // getClientRegistration 정보의 registrationId로 어떤 OAuth로 로그인 했는지 확인가능함 구글인지 페이스북인지 등
		//결과값 : 보안상 직접 확인하자
		
		//AccessToken은 필요가 없다 왜냐하면 이미 oauth 라이브러리가 AccessToken으로 사용자 정보를 가져온다
		System.out.println("PrincipalOauth2UserService의 loadUser()의 userRequest의 getAccessToken : " + userRequest.getAccessToken());
		
		
		OAuth2User oauth2User = super.loadUser(userRequest);
		///구글로그인 버튼 클릭 -> 구글로그인창 -> 로그인 완료 -> code를 return(OAuth-Client 라이브러리가 해줌-개편함)->code를 통해 Access Token 요청
		// -> userRequest 정보 -> 회원 프로필 받아야함 이게 바로 loadUser함수임 호출-> 호출뒤 구글로 부터 회원프로필을 받음
		//엑세스 토큰과 사용자 정보를 모두 가지고 있는 것이 아래 것임
		System.out.println("PrincipalOauth2UserService의 super.loadUser(userRequest)의 getAttributes: " + super.loadUser(userRequest).getAttributes()); // 해당 정보의 getAttributes로 강제 회원가입할 것임
		System.out.println("PrincipalOauth2UserService의 oAuth2User의 getAttributes: " + oauth2User.getAttributes());
		//위 아래 결과값 동일: 값은 보안상 직접 확인하자
		
	    // super.loadUser(usgerRequest)로 강제 회원가입을 진
		String provider = userRequest.getClientRegistration().getRegistrationId(); //google
		String providerId = oauth2User.getAttribute("sub");
		String username = provider+"_"+providerId; //google_114878831288728429347
		String password = bCryptPasswordEncoder.encode("bacboy5604");
		String email = oauth2User.getAttribute("email");
		String role = "ROLE_USER";
		
		User userEntity = userRepository.findByUsername(username);
		
		if (userEntity == null) {
			System.out.println("구글 로그인 최초인 사용자로 강제 회원가입을 진행합니다");
			userEntity =User.builder()
					.username(username)
					.password(password)
					.email(email)
					.role(role)
					.provider(provider)
					.providerId(providerId)
					.build();
			//user 강제 가입
			userRepository.save(userEntity);
		} else {
			System.out.println("구글 로그인을 이미 한적이 있는 사용자로 강제(자동) 회원가입을 하지 않습니다.");
			//null 이 아니면 이미 구글로 가입된 사용자 임
		}
		
	    // 원래는 아래가 리턴 되던 것이엇지만
		// return super.loadUser(userRequest);
		// 지금은 아래것으로 리턴함 왜냐하면 OAuth2User를 PrincipalDetails에 implement 했기때문에 리턴이 가능해짐
		// 결국은 PrincipalDetails(userEntity, oauth2User.getAttributes() 이것이 PrincipalDetails의 생성자에 의해
		// 만들어진뒤 Athentication 세션 객체안에 쏙 들어갈 것이고 
		// 우리는 Controller에서 이것에 접근해서 user 정보를 가져오면 된다
		return new PrincipalDetails(userEntity, oauth2User.getAttributes());
	}
	
// google 로그인시 받은 값
//	super.loadUser(userRequest).getAttributes : 
//	{
//		sub=보안, 
//		name=보안, 
//		given_name=보안, 
//		family_name=보안, 
//		picture=보안, 
//		email=보안, 
//		email_verified=true, 
//	            locale=ko
//	}

}
