package com.ventalize.shop.member.service;

import com.ventalize.shop.member.entity.Member;
import com.ventalize.shop.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다: " + loginId));

        if ("SUSPENDED".equals(member.getStatus())) {
            throw new UsernameNotFoundException("정지된 계정입니다.");
        }

        return new User(
                member.getLoginId(),
                member.getLoginPw(),
                List.of(new SimpleGrantedAuthority(member.getRole()))
        );
    }
}
