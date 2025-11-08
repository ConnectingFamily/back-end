package familyConnection.domain.question.repository;

import familyConnection.domain.family.entity.Family;
import familyConnection.domain.question.entity.DailyQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyQuestionRepository extends JpaRepository<DailyQuestion, Long> {

    // 가족 + 날짜로 오늘 질문 조회
    Optional<DailyQuestion> findByFamilyAndAssignedDate(Family family, LocalDate assignedDate);
}