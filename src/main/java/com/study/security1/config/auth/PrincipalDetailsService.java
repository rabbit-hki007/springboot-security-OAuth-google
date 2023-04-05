package com.study.security1.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.study.security1.model.User;
import com.study.security1.repository.UserRepository;

// 시큐리티 설정에서 .loginProcessingUrl("/loginProc")로
// 로그인 요청이 오면 자동으로 UserDetailsService 타입으로 IoC 되어 있는 loadUserByUsername 함수가 실행된다

@Service
public class PrincipalDetailsService implements UserDetailsService{

	//UserRepository는 findByUsername을 기본적으로 가지고 있지는 않아서 생성해 주어야 함
	@Autowired
	private UserRepository userRepository;
	
	// 로그인 버튼을 누르면 (요청이 오면) 자동으로 UserDetailsService 타입으로 IoC 되어 있는 loadUserByUsername 함수가 실행된다
	// 시큐리티 session(내부 Authentication(내부 UserDetails))
	// 함수 종료시 @AuthenticationPrincipal 어노테이션을 사용할수 있도록 만들어진다
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		
		System.out.println("로그인 시도시 넘어온 username : " + username );
		
		User userEntity = userRepository.findByUsername(username);  //findByUsername은 userRepository가 안들고 있음 기본적인 CRUD만 가지고 있어서 추가로 만들어 줘야 함

		//아래처럼 리턴해도 된다     
//	   if(userEntity == null) {
//			return null;
//		}
//		return new PrincipalDetails(userEntity);
		
		if(userEntity != null ) {
			return new PrincipalDetails(userEntity);
			// 리턴될때 어디로 리턴되냐하면 (어떻게 되냐하면)
			// 시큐리리 Session => Athentication => UserDetails 이런 구성에서
			// 아래처럼 UserDtails가 Athentication 객체 안으로 쏙 들어간다
			// 시큐리리 Session => Athentication(내부 UserDetails)
			// 최종적으로 아래 같은 구성이 된다
			// 시큐리티 session(내부 Athentication(내부 UserDetails))
		}
		return null;
	}

}
