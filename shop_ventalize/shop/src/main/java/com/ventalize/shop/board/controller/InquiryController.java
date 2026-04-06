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
}
