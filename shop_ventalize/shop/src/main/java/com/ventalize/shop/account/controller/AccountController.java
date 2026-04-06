package com.ventalize.shop.account.controller;

import com.ventalize.shop.account.dto.AccountCheckResponse;
import com.ventalize.shop.account.dto.AccountJoinRequest;
import com.ventalize.shop.account.dto.AccountLoginRequest;
import com.ventalize.shop.common.util.SecurityUtil;
import com.ventalize.shop.member.dto.MemberRead;
import com.ventalize.shop.member.dto.MemberUpdateRequest;
import com.ventalize.shop.member.entity.Member;
import com.ventalize.shop.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil;

    /** 회원가입 */
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody AccountJoinRequest req) {
        if (!StringUtils.hasLength(req.getName()) || !StringUtils.hasLength(req.getLoginId())
                || !StringUtils.hasLength(req.getLoginPw())) {
            return ResponseEntity.badRequest().body("필수 항목을 모두 입력하세요.");
        }
        if (memberRepository.existsByLoginId(req.getLoginId())) {
            return ResponseEntity.status(409).body("이미 사용 중인 이메일입니다.");
        }
        Member member = Member.builder()
                .name(req.getName())
                .loginId(req.getLoginId())
                .loginPw(passwordEncoder.encode(req.getLoginPw()))
                .role("ROLE_USER")
                .grade("BRONZE")
                .status("ACTIVE")
                .build();
        memberRepository.save(member);
        return ResponseEntity.ok().build();
    }

    /** 로그인 */
    @PostMapping("/login")
    public ResponseEntity<?> login(HttpServletRequest request,
                                   @RequestBody AccountLoginRequest req) {
        if (!StringUtils.hasLength(req.getLoginId()) || !StringUtils.hasLength(req.getLoginPw())) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getLoginId(), req.getLoginPw()));
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

            Member member = memberRepository.findByLoginId(req.getLoginId()).orElseThrow();
            return ResponseEntity.ok(new AccountCheckResponse(true, member.getLoginId(), member.getName(), member.getRole()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
    }

    /** 로그인 상태 확인 */
    @GetMapping("/check")
    public ResponseEntity<?> check() {
        Member member = securityUtil.getCurrentMember();
        if (member == null) {
            return ResponseEntity.ok(new AccountCheckResponse(false, null, null, null));
        }
        return ResponseEntity.ok(new AccountCheckResponse(true, member.getLoginId(), member.getName(), member.getRole()));
    }

    /** 로그아웃 */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }

    /** 내 정보 조회 */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        Member member = securityUtil.getCurrentMember();
        if (member == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(MemberRead.from(member));
    }

    /** 내 정보 수정 */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody MemberUpdateRequest req) {
        Member member = securityUtil.getCurrentMember();
        if (member == null) return ResponseEntity.status(401).build();

        if (StringUtils.hasLength(req.getName())) member.setName(req.getName());
        if (StringUtils.hasLength(req.getPhone())) member.setPhone(req.getPhone());
        if (StringUtils.hasLength(req.getAddress())) member.setAddress(req.getAddress());

        if (StringUtils.hasLength(req.getNewPw())) {
            if (!passwordEncoder.matches(req.getCurrentPw(), member.getLoginPw())) {
                return ResponseEntity.badRequest().body("현재 비밀번호가 올바르지 않습니다.");
            }
            member.setLoginPw(passwordEncoder.encode(req.getNewPw()));
        }
        memberRepository.save(member);
        return ResponseEntity.ok(MemberRead.from(member));
    }

    /** 회원 탈퇴 */
    @DeleteMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestParam String password, HttpServletRequest request) {
        Member member = securityUtil.getCurrentMember();
        if (member == null) return ResponseEntity.status(401).build();
        if (!passwordEncoder.matches(password, member.getLoginPw())) {
            return ResponseEntity.badRequest().body("비밀번호가 올바르지 않습니다.");
        }
        member.setStatus("WITHDRAWN");
        memberRepository.save(member);
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
}
