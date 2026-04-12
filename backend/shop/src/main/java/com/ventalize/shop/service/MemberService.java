package com.ventalize.shop.service;

import com.ventalize.shop.entity.Member;
import com.ventalize.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public void save(String name, String loginId, String loginPw) {
        memberRepository.save(Member.builder()
                .name(name)
                .loginId(loginId)
                .loginPw(loginPw)
                .build());
    }

    public Member find(String loginId, String loginPw) {
        Optional<Member> memberOptional = memberRepository.findByLoginIdAndLoginPw(loginId, loginPw);
        return memberOptional.orElse(null);
    }
}
