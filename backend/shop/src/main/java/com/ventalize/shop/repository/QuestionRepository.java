package com.ventalize.shop.repository;

import com.ventalize.shop.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

    List<Question> findAllByItemIdOrderByCreatedAtDesc(Integer itemId);

    List<Question> findAllByMemberIdOrderByCreatedAtDesc(Integer memberId);

    Optional<Question> findByIdAndMemberId(Integer id, Integer memberId);

    List<Question> findAllByOrderByCreatedAtDesc();
}
