package com.ventalize.shop.service;

import com.ventalize.shop.entity.Member;
import com.ventalize.shop.repository.MemberRepository;
import com.ventalize.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 회원 등급 자동 산정 배치
 * - 매일 새벽 2시 실행
 * - 최근 6개월 누적 구매 금액 기준으로 등급 결정
 *
 * 등급 기준 (6개월 누적):
 *   SAPPHIRE : 0원 이상 (신규 / 기본)
 *   RUBY     : 30만원 이상
 *   EMERALD  : 80만원 이상
 *   GOLD     : 200만원 이상
 *   DIAMOND  : 500만원 이상
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GradeScheduler {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;

    @Scheduled(cron = "0 0 2 * * *")   // 매일 새벽 2:00
    @Transactional
    public void updateGrades() {
        LocalDateTime since = LocalDateTime.now().minusMonths(6);
        List<Member> members = memberRepository.findAll();

        int updated = 0;
        for (Member member : members) {
            if ("ROLE_ADMIN".equals(member.getRole())) continue;

            long totalAmount = orderRepository
                    .sumAmountByMemberIdAndStatusNotCancelledSince(member.getId(), since);

            String newGrade = resolveGrade(totalAmount);
            if (!newGrade.equals(member.getGrade())) {
                member.setGrade(newGrade);
                memberRepository.save(member);
                updated++;
            }
        }
        log.info("[GradeScheduler] 등급 갱신 완료 — {}명 변경", updated);
    }

    private String resolveGrade(long amount) {
        if (amount >= 5_000_000) return "DIAMOND";
        if (amount >= 2_000_000) return "GOLD";
        if (amount >= 800_000)  return "EMERALD";
        if (amount >= 300_000)  return "RUBY";
        return "SAPPHIRE";
    }
}
