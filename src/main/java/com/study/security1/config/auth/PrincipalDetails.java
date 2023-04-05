package com.study.security1.config.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.study.security1.model.User;

import lombok.Data;

@Data //이 어노테이션은 컨트롤러의 OAuth2 로그인을을 테스트 하기 위한 test/login에 User 값을 가져오기 위해 쓴 어노테이션임
public class PrincipalDetails implements UserDetails, OAuth2User{
	
	//public class PrincipalDetails implements UserDetails, OAuth2User 이렇게 하면
	//일반로그인시에는 spring security session의 authentication가 UserDetails와 OAuth2User룰 둘다 사용 가능해짐
	//그러므로 우리는 PrincipalDetails만 찾으면 된다
	
	private static final long serialVersionUID = 1L;
	
	private User user;//콤포지션 (뜻은 구성)
	
	// 이부분은 OAuth로그인 정보를 담을 그룻이 됩니다
	private Map<String, Object> attributes;
	
	
	//일반 로그인용 생성자
	public PrincipalDetails(User user) {
			//super();
		this.user = user;
		
	}
	
	//OAuth 로그인용 생성자
	public PrincipalDetails(User user, Map<String, Object> attributes) {
				//super();
			this.user = user; //OAuth 로그인을 했을때는 Map<String, Object> attributes 이 정보로 user 정보를 만들어서 넣어줄 것임
			this.attributes = attributes;
	}
    
	//해당 user 의 권한을 리턴하는 곳
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collect =new ArrayList<GrantedAuthority>();
		collect.add(new GrantedAuthority() {
			@Override
			public String getAuthority() {
				// String 타입으로 리턴이 가능해짐
				return user.getRole();
			}
		});
		
		
		return collect;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		// 계정이 만료 안되었니?
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// 계정이 안 잠겼니?
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// 니 계정이 1년이 지나지 않았니?
		return true;
	}

	@Override
	public boolean isEnabled() {
		// 여기서는 이런것을 설정할수 있다
		// 우리사이트! 1년동안 회원이 로그인을 안하면!! 휴면계정으로 하기로 함
		// User.getLoginDate 로 로긴시간을 가져온뒤
		// 현재시간 - 로긴시간 => 1년을 초과하면 return false;
		
		// 계정이 활성화 되어었니?(사용가능하니?)
		// 네

		return true;
	}
    
	///////////////////////////////////////////////////////////////////////
	// OAuth2User를 implement 하면서 override 해야하는 함수 두가지임
	@Override
	public Map<String, Object> getAttributes() {
		// TODO Auto-generated method stub
		return attributes;
	}

	@Override
	public String getName() {
		//return attributes.get("sub"); 안중요함 안쓸 것임
		return null;
		
	}
	///////////////////////////////////////////////////////////////////////

}
