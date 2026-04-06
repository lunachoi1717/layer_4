package com.ventalize.shop.member.service;

import com.ventalize.shop.member.entity.Member;
import com.ventalize.shop.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BaseMemberService implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public void save(String name, String loginId, String loginPw) {
        memberRepository.save(Member.builder()
                .name(name)
                .loginId(loginId)
                .loginPw(loginPw)
                .build());
    }

    @Override
    public Member find(String loginId, String loginPw) {
        Optional<Member> memberOptional = memberRepository.findByLoginIdAndLoginPw(loginId, loginPw);
        return memberOptional.orElse(null);
    }
}
