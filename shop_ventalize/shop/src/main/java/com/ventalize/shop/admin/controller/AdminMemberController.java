package com.ventalize.shop.admin.controller;

import com.ventalize.shop.member.dto.MemberRead;
import com.ventalize.shop.member.entity.Member;
import com.ventalize.shop.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {

    private final MemberRepository memberRepository;

    /** 전체 회원 목록 */
    @GetMapping
    public ResponseEntity<?> list(@RequestParam(required = false) String keyword) {
        List<Member> members;
        if (StringUtils.hasLength(keyword)) {
            members = memberRepository.findByNameContainingOrLoginIdContaining(keyword, keyword);
        } else {
            members = memberRepository.findAllByOrderByCreatedAtDesc();
        }
        return ResponseEntity.ok(members.stream().map(MemberRead::from).toList());
    }

    /** 회원 상세 */
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Integer id) {
        return memberRepository.findById(id)
                .map(m -> ResponseEntity.ok(MemberRead.from(m)))
                .orElse(ResponseEntity.notFound().build());
    }

    /** 회원 강퇴(정지) */
    @PatchMapping("/{id}/suspend")
    public ResponseEntity<?> suspend(@PathVariable Integer id) {
        return memberRepository.findById(id).map(m -> {
            if ("ROLE_ADMIN".equals(m.getRole())) {
                return ResponseEntity.badRequest().<Object>body("관리자는 강퇴할 수 없습니다.");
            }
            m.setStatus("SUSPENDED");
            memberRepository.save(m);
            return ResponseEntity.ok().<Object>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    /** 회원 활성화 */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable Integer id) {
        return memberRepository.findById(id).map(m -> {
            m.setStatus("ACTIVE");
            memberRepository.save(m);
            return ResponseEntity.ok().<Object>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    /** 회원 등급 변경 */
    @PatchMapping("/{id}/grade")
    public ResponseEntity<?> changeGrade(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        String grade = body.get("grade");
        if (!List.of("SAPPHIRE", "RUBY", "EMERALD", "GOLD", "DIAMOND").contains(grade)) {
            return ResponseEntity.badRequest().body("유효하지 않은 등급입니다.");
        }
        return memberRepository.findById(id).map(m -> {
            m.setGrade(grade);
            memberRepository.save(m);
            return ResponseEntity.ok().<Object>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    /** 회원 역할 변경 (관리자 권한 부여/해제) */
    @PatchMapping("/{id}/role")
    public ResponseEntity<?> changeRole(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        String role = body.get("role");
        if (!List.of("ROLE_USER", "ROLE_ADMIN").contains(role)) {
            return ResponseEntity.badRequest().body("유효하지 않은 역할입니다.");
        }
        return memberRepository.findById(id).map(m -> {
            m.setRole(role);
            memberRepository.save(m);
            return ResponseEntity.ok().<Object>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
