package com.ventalize.shop.controller;

import com.ventalize.shop.dto.board.FaqRead;
import com.ventalize.shop.entity.Faq;
import com.ventalize.shop.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/board/faq")
@RequiredArgsConstructor
public class FaqController {

    private final FaqRepository faqRepository;

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(required = false) String category) {
        List<Faq> faqs = (category != null && !category.isBlank())
                ? faqRepository.findByCategoryAndIsPublishedTrueOrderByCreatedAtDesc(category)
                : faqRepository.findByIsPublishedTrueOrderByCreatedAtDesc();
        return ResponseEntity.ok(faqs.stream().map(this::toRead).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Integer id) {
        return faqRepository.findById(id)
                .filter(Faq::getIsPublished)
                .map(f -> ResponseEntity.ok(toRead(f)))
                .orElse(ResponseEntity.notFound().build());
    }

    private FaqRead toRead(Faq f) {
        return FaqRead.builder()
                .id(f.getId())
                .category(f.getCategory())
                .question(f.getQuestion())
                .answer(f.getAnswer())
                .isPublished(f.getIsPublished())
                .createdAt(f.getCreatedAt())
                .build();
    }
}
