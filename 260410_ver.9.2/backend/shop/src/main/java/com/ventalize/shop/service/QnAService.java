package com.ventalize.shop.service;

import com.ventalize.shop.repository.ItemRepository;
import com.ventalize.shop.entity.Member;
import com.ventalize.shop.repository.MemberRepository;
import com.ventalize.shop.dto.qna.AnswerRequest;
import com.ventalize.shop.dto.qna.QuestionCreateRequest;
import com.ventalize.shop.dto.qna.QuestionRead;
import com.ventalize.shop.entity.Question;
import com.ventalize.shop.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QnAService {

    private final QuestionRepository questionRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    private QuestionRead toRead(Question q, Integer currentMemberId, boolean isAdmin) {
        String memberName = memberRepository.findById(q.getMemberId())
                .map(Member::getName).orElse("(탈퇴회원)");
        String itemName = q.getItemId() != null
                ? itemRepository.findById(q.getItemId()).map(i -> i.getName()).orElse("") : "";

        boolean canSeeContent = !q.getIsSecret() || isAdmin
                || (currentMemberId != null && currentMemberId.equals(q.getMemberId()));

        return QuestionRead.builder()
                .id(q.getId())
                .memberId(q.getMemberId())
                .memberName(memberName)
                .itemId(q.getItemId())
                .itemName(itemName)
                .title(q.getTitle())
                .content(canSeeContent ? q.getContent() : "비밀 문의입니다.")
                .isSecret(q.getIsSecret())
                .isAnswered(q.getIsAnswered())
                .answerContent(canSeeContent ? q.getAnswerContent() : null)
                .answeredAt(q.getAnsweredAt())
                .createdAt(q.getCreatedAt())
                .build();
    }

    public List<QuestionRead> findByItem(Integer itemId, Integer memberId, boolean isAdmin) {
        return questionRepository.findAllByItemIdOrderByCreatedAtDesc(itemId)
                .stream().map(q -> toRead(q, memberId, isAdmin)).toList();
    }

    public List<QuestionRead> findByMember(Integer memberId) {
        return questionRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId)
                .stream().map(q -> toRead(q, memberId, false)).toList();
    }

    public QuestionRead save(QuestionCreateRequest req, Integer memberId) {
        Question q = Question.builder()
                .memberId(memberId)
                .itemId(req.getItemId())
                .title(req.getTitle())
                .content(req.getContent())
                .isSecret(req.getIsSecret() != null && req.getIsSecret())
                .build();
        return toRead(questionRepository.save(q), memberId, false);
    }

    public QuestionRead update(Integer id, QuestionCreateRequest req, Integer memberId) {
        return questionRepository.findByIdAndMemberId(id, memberId).map(q -> {
            if (req.getTitle() != null) q.setTitle(req.getTitle());
            if (req.getContent() != null) q.setContent(req.getContent());
            if (req.getIsSecret() != null) q.setIsSecret(req.getIsSecret());
            return toRead(questionRepository.save(q), memberId, false);
        }).orElse(null);
    }

    public boolean delete(Integer id, Integer memberId) {
        return questionRepository.findByIdAndMemberId(id, memberId).map(q -> {
            questionRepository.delete(q);
            return true;
        }).orElse(false);
    }

    public List<QuestionRead> findAll() {
        return questionRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(q -> toRead(q, null, true)).toList();
    }

    public QuestionRead answer(Integer id, AnswerRequest req, Integer adminId) {
        return questionRepository.findById(id).map(q -> {
            q.setAnswerContent(req.getAnswerContent());
            q.setAnswerMemberId(adminId);
            q.setIsAnswered(true);
            q.setAnsweredAt(LocalDateTime.now());
            return toRead(questionRepository.save(q), null, true);
        }).orElse(null);
    }

    public boolean adminDelete(Integer id) {
        return questionRepository.findById(id).map(q -> {
            questionRepository.delete(q);
            return true;
        }).orElse(false);
    }
}
