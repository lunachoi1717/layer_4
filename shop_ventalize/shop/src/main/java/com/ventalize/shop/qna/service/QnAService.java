package com.ventalize.shop.qna.service;

import com.ventalize.shop.qna.dto.AnswerRequest;
import com.ventalize.shop.qna.dto.QuestionCreateRequest;
import com.ventalize.shop.qna.dto.QuestionRead;

import java.util.List;

public interface QnAService {
    List<QuestionRead> findByItem(Integer itemId, Integer memberId, boolean isAdmin);
    List<QuestionRead> findByMember(Integer memberId);
    QuestionRead save(QuestionCreateRequest req, Integer memberId);
    QuestionRead update(Integer id, QuestionCreateRequest req, Integer memberId);
    boolean delete(Integer id, Integer memberId);
    List<QuestionRead> findAll();
    QuestionRead answer(Integer id, AnswerRequest req, Integer adminId);
    boolean adminDelete(Integer id);
}
