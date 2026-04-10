package com.ventalize.shop.controller;

import com.ventalize.shop.util.SecurityUtil;
import com.ventalize.shop.dto.qna.QuestionCreateRequest;
import com.ventalize.shop.dto.qna.QuestionRead;
import com.ventalize.shop.service.QnAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/qna")
@RequiredArgsConstructor
public class QnAController {

    private final QnAService qnAService;
    private final SecurityUtil securityUtil;

    @GetMapping("/item/{itemId}")
    public ResponseEntity<?> byItem(@PathVariable Integer itemId) {
        Integer memberId = securityUtil.getCurrentMemberId();
        boolean isAdmin = securityUtil.isAdmin();
        return ResponseEntity.ok(qnAService.findByItem(itemId, memberId, isAdmin));
    }

    @GetMapping("/my")
    public ResponseEntity<?> my() {
        Integer memberId = securityUtil.getCurrentMemberId();
        if (memberId == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(qnAService.findByMember(memberId));
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody QuestionCreateRequest req) {
        Integer memberId = securityUtil.getCurrentMemberId();
        if (memberId == null) return ResponseEntity.status(401).build();
        QuestionRead question = qnAService.save(req, memberId);
        return ResponseEntity.ok(question);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody QuestionCreateRequest req) {
        Integer memberId = securityUtil.getCurrentMemberId();
        if (memberId == null) return ResponseEntity.status(401).build();
        QuestionRead question = qnAService.update(id, req, memberId);
        return question != null ? ResponseEntity.ok(question) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        Integer memberId = securityUtil.getCurrentMemberId();
        if (memberId == null) return ResponseEntity.status(401).build();
        return qnAService.delete(id, memberId)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }
}
