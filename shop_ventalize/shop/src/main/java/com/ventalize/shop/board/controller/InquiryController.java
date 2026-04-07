package com.ventalize.shop.board.controller;

import com.ventalize.shop.board.dto.InquiryRead;
import com.ventalize.shop.board.dto.InquiryRequest;
import com.ventalize.shop.board.entity.Inquiry;
import com.ventalize.shop.board.repository.InquiryRepository;
import com.ventalize.shop.common.util.SecurityUtil;
import com.ventalize.shop.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/board/inquiry")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryRepository inquiryRepository;
    private final MemberRepository memberRepository;
    private final SecurityUtil securityUtil;

    /** 전체 문의 목록 (공개) — 제목·카테고리·상태만 노출, 내용 숨김 */
    @GetMapping
    public ResponseEntity<?> list() {
        Integer currentMemberId = securityUtil.getCurrentMemberId();
        boolean isAdmin = isCurrentAdmin();
        return ResponseEntity.ok(
                inquiryRepository.findAllByOrderByCreatedAtDesc()
                        .stream()
                        .map(i -> {
                            boolean isOwner = currentMemberId != null && currentMemberId.equals(i.getMemberId());
                            return toReadPublic(i, isOwner || isAdmin);
                        })
                        .toList()
        );
    }

    /** 문의 상세 (작성자, 관리자, 또는 비밀번호 일치 시 열람 가능) */
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Integer id,
                                    @RequestParam(required = false) String pw) {
        Integer currentMemberId = securityUtil.getCurrentMemberId();
        boolean isAdmin = isCurrentAdmin();
        return inquiryRepository.findById(id)
                .map(i -> {
                    boolean isOwner = currentMemberId != null && currentMemberId.equals(i.getMemberId());
                    boolean pwMatch = pw != null && !pw.isBlank()
                            && i.getInquiryPw() != null && pw.equals(i.getInquiryPw());
                    if (!isOwner && !isAdmin && !pwMatch) {
                        return ResponseEntity.status(403).<Object>build();
                    }
                    return ResponseEntity.ok((Object) toRead(i, isAdmin));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /** 내 1:1 문의 목록 */
    @GetMapping("/my")
    public ResponseEntity<?> my() {
        Integer memberId = securityUtil.getCurrentMemberId();
        if (memberId == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(inquiryRepository.findByMemberIdOrderByCreatedAtDesc(memberId)
                .stream().map(i -> toRead(i, false)).toList());
    }

    /** 1:1 문의 등록 */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody InquiryRequest req) {
        Integer memberId = securityUtil.getCurrentMemberId();
        if (memberId == null) return ResponseEntity.status(401).build();
        Inquiry inquiry = Inquiry.builder()
                .memberId(memberId)
                .category(req.getCategory() != null ? req.getCategory() : "기타")
                .title(req.getTitle())
                .content(req.getContent())
                .inquiryPw(req.getInquiryPw())
                .build();
        return ResponseEntity.ok(toRead(inquiryRepository.save(inquiry), false));
    }

    /** 1:1 문의 삭제 (본인만) */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        Integer memberId = securityUtil.getCurrentMemberId();
        return inquiryRepository.findById(id)
                .filter(i -> i.getMemberId().equals(memberId) && !i.getIsAnswered())
                .map(i -> {
                    inquiryRepository.delete(i);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /** 권한 있는 사용자용 전체 내용 DTO */
    private InquiryRead toRead(Inquiry i, boolean isAdmin) {
        String memberName = memberRepository.findById(i.getMemberId())
                .map(m -> m.getName()).orElse("(탈퇴회원)");
        return InquiryRead.builder()
                .id(i.getId())
                .memberId(i.getMemberId())
                .memberName(memberName)
                .category(i.getCategory())
                .title(i.getTitle())
                .content(i.getContent())
                .answerContent(i.getAnswerContent())
                .isAnswered(i.getIsAnswered())
                .answeredAt(i.getAnsweredAt())
                .createdAt(i.getCreatedAt())
                .build();
    }

    /** 공개용 DTO — 내용 열람 권한이 없으면 content·answerContent 마스킹 */
    private InquiryRead toReadPublic(Inquiry i, boolean canViewContent) {
        String memberName = memberRepository.findById(i.getMemberId())
                .map(m -> m.getName()).orElse("(탈퇴회원)");
        return InquiryRead.builder()
                .id(i.getId())
                .memberId(i.getMemberId())
                .memberName(memberName)
                .category(i.getCategory())
                .title(i.getTitle())
                .content(canViewContent ? i.getContent() : null)
                .answerContent(canViewContent ? i.getAnswerContent() : null)
                .isAnswered(i.getIsAnswered())
                .answeredAt(i.getAnsweredAt())
                .createdAt(i.getCreatedAt())
                .build();
    }

    private boolean isCurrentAdmin() {
        try {
            org.springframework.security.core.Authentication auth =
                    org.springframework.security.core.context.SecurityContextHolder
                            .getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) return false;
            return auth.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        } catch (Exception e) {
            return false;
        }
    }
}
