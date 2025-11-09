package familyConnection.domain.question.repository;

import familyConnection.domain.family.entity.Family;
import familyConnection.domain.question.entity.Answer;
import familyConnection.domain.question.entity.DailyQuestion;
import familyConnection.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    Optional<Answer> findByDailyQuestionAndUser(DailyQuestion dailyQuestion, User user);

    List<Answer> findByDailyQuestion(DailyQuestion dailyQuestion);

    List<Answer> findByUserAndFamilyAndSubmittedAtBetween(
            User user,
            Family family,
            LocalDateTime start,
            LocalDateTime end
    );

    Page<Answer> findByUserAndFamilyOrderBySubmittedAtDesc(
            User user,
            Family family,
            Pageable pageable
    );

    // 카운팅이에요 그냥 MAX 로 반환했습니다.
    long countByUserAndFamily(User user, Family family);

    Optional<Answer> findByUserAndFamilyAndDailyQuestion_DailyQuestionId(
            User user,
            Family family,
            Long dailyQuestionId
    );
}
