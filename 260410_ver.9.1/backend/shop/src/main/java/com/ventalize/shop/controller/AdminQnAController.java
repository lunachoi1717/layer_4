package com.ventalize.shop.controller;

import com.ventalize.shop.util.SecurityUtil;
import com.ventalize.shop.dto.qna.AnswerRequest;
import com.ventalize.shop.service.QnAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/admin/qna")
@RequiredArgsConstructor
public class AdminQnAController {

    private final QnAService qnAService;
    private final SecurityUtil securityUtil;

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(qnAService.findAll());
    }

    @PostMapping("/{id}/answer")
    public ResponseEntity<?> answer(@PathVariable Integer id, @RequestBody AnswerRequest req) {
        Integer adminId = securityUtil.getCurrentMemberId();
        if (adminId == null) return ResponseEntity.status(401).build();
        var result = qnAService.answer(id, req, adminId);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        return qnAService.adminDelete(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }
}
