package com.ventalize.shop.member.repository;

import com.ventalize.shop.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {

    Optional<Member> findByLoginId(String loginId);

    Optional<Member> findByLoginIdAndLoginPw(String loginId, String loginPw);

    boolean existsByLoginId(String loginId);

    List<Member> findByNameContainingOrLoginIdContaining(String name, String loginId);

    List<Member> findAllByOrderByCreatedAtDesc();
}
