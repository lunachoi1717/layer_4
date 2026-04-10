package com.ventalize.shop.controller;

import com.ventalize.shop.dto.board.InquiryRead;
import com.ventalize.shop.dto.board.InquiryRequest;
import com.ventalize.shop.entity.Inquiry;
import com.ventalize.shop.repository.InquiryRepository;
import com.ventalize.shop.util.SecurityUtil;
import com.ventalize.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/board/inquiry")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryRepository inquiryRepository;
    private final MemberRepository memberRepository;
    private final SecurityUtil securityUtil;

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

    @GetMapping("/my")
    public ResponseEntity<?> my() {
        Integer memberId = securityUtil.getCurrentMemberId();
        if (memberId == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(inquiryRepository.findByMemberIdOrderByCreatedAtDesc(memberId)
                .stream().map(i -> toRead(i, false)).toList());
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        Integer memberId = securityUtil.getCurrentMemberId();
        boolean isAdmin = isCurrentAdmin();
        return inquiryRepository.findById(id)
                .filter(i -> isAdmin || i.getMemberId().equals(memberId))
                .map(i -> {
                    inquiryRepository.delete(i);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.status(403).build());
    }

    private InquiryRead toRead(Inquiry i, boolean isAdmin) {
        var member = memberRepository.findById(i.getMemberId());
        String memberName    = member.map(m -> m.getName()).orElse("(탈퇴회원)");
        String memberLoginId = member.map(m -> m.getLoginId()).orElse(null);
        return InquiryRead.builder()
                .id(i.getId())
                .memberId(i.getMemberId())
                .memberLoginId(memberLoginId)
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

    private InquiryRead toReadPublic(Inquiry i, boolean canViewContent) {
        var member = memberRepository.findById(i.getMemberId());
        String memberName    = member.map(m -> m.getName()).orElse("(탈퇴회원)");
        String memberLoginId = member.map(m -> m.getLoginId()).orElse(null);
        return InquiryRead.builder()
                .id(i.getId())
                .memberId(i.getMemberId())
                .memberLoginId(memberLoginId)
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
