package com.ventalize.shop.qna.controller;

import com.ventalize.shop.common.util.SecurityUtil;
import com.ventalize.shop.qna.dto.QuestionCreateRequest;
import com.ventalize.shop.qna.dto.QuestionRead;
import com.ventalize.shop.qna.service.QnAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/qna")
@RequiredArgsConstructor
public class QnAController {

    private final QnAService qnAService;
    private final SecurityUtil securityUtil;

    /** 상품별 Q&A 목록 */
    @GetMapping("/item/{itemId}")
    public ResponseEntity<?> byItem(@PathVariable Integer itemId) {
        Integer memberId = securityUtil.getCurrentMemberId();
        boolean isAdmin = securityUtil.isAdmin();
        return ResponseEntity.ok(qnAService.findByItem(itemId, memberId, isAdmin));
    }

    /** 내 Q&A 목록 */
    @GetMapping("/my")
    public ResponseEntity<?> my() {
        Integer memberId = securityUtil.getCurrentMemberId();
        if (memberId == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(qnAService.findByMember(memberId));
    }

    /** Q&A 작성 */
    @PostMapping
    public ResponseEntity<?> add(@RequestBody QuestionCreateRequest req) {
        Integer memberId = securityUtil.getCurrentMemberId();
        if (memberId == null) return ResponseEntity.status(401).build();
        QuestionRead question = qnAService.save(req, memberId);
        return ResponseEntity.ok(question);
    }

    /** Q&A 수정 */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody QuestionCreateRequest req) {
        Integer memberId = securityUtil.getCurrentMemberId();
        if (memberId == null) return ResponseEntity.status(401).build();
        QuestionRead question = qnAService.update(id, req, memberId);
        return question != null ? ResponseEntity.ok(question) : ResponseEntity.notFound().build();
    }

    /** Q&A 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        Integer memberId = securityUtil.getCurrentMemberId();
        if (memberId == null) return ResponseEntity.status(401).build();
        return qnAService.delete(id, memberId)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }
}
