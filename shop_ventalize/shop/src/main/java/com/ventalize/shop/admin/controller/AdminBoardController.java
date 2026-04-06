package com.ventalize.shop.admin.controller;

import com.ventalize.shop.board.dto.*;
import com.ventalize.shop.board.entity.Faq;
import com.ventalize.shop.board.entity.Inquiry;
import com.ventalize.shop.board.repository.FaqRepository;
import com.ventalize.shop.board.repository.InquiryRepository;
import com.ventalize.shop.common.util.SecurityUtil;
import com.ventalize.shop.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/api/admin/board")
@RequiredArgsConstructor
public class AdminBoardController {

    private final FaqRepository faqRepository;
    private final InquiryRepository inquiryRepository;
    private final MemberRepository memberRepository;
    private final com.ventalize.shop.common.util.SecurityUtil securityUtil;

    /* ============= FAQ 관리 ============= */

    @GetMapping("/faq")
    public ResponseEntity<?> faqList() {
        return ResponseEntity.ok(faqRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(f -> FaqRead.builder()
                        .id(f.getId()).category(f.getCategory())
                        .question(f.getQuestion()).answer(f.getAnswer())
                        .isPublished(f.getIsPublished()).createdAt(f.getCreatedAt())
                        .build()).toList());
    }

    @PostMapping("/faq")
    public ResponseEntity<?> createFaq(@RequestBody FaqRequest req) {
        Faq faq = Faq.builder()
                .category(req.getCategory() != null ? req.getCategory() : "기타")
                .question(req.getQuestion())
                .answer(req.getAnswer())
                .isPublished(req.getIsPublished() != null ? req.getIsPublished() : true)
                .build();
        return ResponseEntity.ok(faqRepository.save(faq));
    }

    @PutMapping("/faq/{id}")
    public ResponseEntity<?> updateFaq(@PathVariable Integer id, @RequestBody FaqRequest req) {
        return faqRepository.findById(id).map(f -> {
            if (req.getCategory() != null) f.setCategory(req.getCategory());
            if (req.getQuestion() != null) f.setQuestion(req.getQuestion());
            if (req.getAnswer() != null) f.setAnswer(req.getAnswer());
            if (req.getIsPublished() != null) f.setIsPublished(req.getIsPublished());
            return ResponseEntity.ok(faqRepository.save(f));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/faq/{id}")
    public ResponseEntity<?> deleteFaq(@PathVariable Integer id) {
        if (!faqRepository.existsById(id)) return ResponseEntity.notFound().build();
        faqRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /* ============= 1:1 문의 관리 ============= */

    @GetMapping("/inquiry")
    public ResponseEntity<?> inquiryList(@RequestParam(required = false) Boolean unanswered) {
        List<Inquiry> list = inquiryRepository.findAllByOrderByCreatedAtDesc();
        if (Boolean.TRUE.equals(unanswered)) {
            list = list.stream().filter(i -> !i.getIsAnswered()).toList();
        }
        return ResponseEntity.ok(list.stream().map(this::toInquiryRead).toList());
    }

    @PostMapping("/inquiry/{id}/answer")
    public ResponseEntity<?> answerInquiry(@PathVariable Integer id,
                                            @RequestBody AnswerRequest req) {
        Integer adminId = securityUtil.getCurrentMemberId();
        return inquiryRepository.findById(id).map(i -> {
            i.setAnswerContent(req.getAnswerContent());
            i.setAnswerMemberId(adminId);
            i.setAnsweredAt(LocalDateTime.now());
            i.setIsAnswered(true);
            return ResponseEntity.ok(toInquiryRead(inquiryRepository.save(i)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/inquiry/{id}")
    public ResponseEntity<?> deleteInquiry(@PathVariable Integer id) {
        if (!inquiryRepository.existsById(id)) return ResponseEntity.notFound().build();
        inquiryRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private InquiryRead toInquiryRead(Inquiry i) {
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
