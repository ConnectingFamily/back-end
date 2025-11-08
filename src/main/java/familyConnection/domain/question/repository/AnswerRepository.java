package familyConnection.domain.question.repository;

import familyConnection.domain.question.entity.Answer;
import familyConnection.domain.question.entity.DailyQuestion;
import familyConnection.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    Optional<Answer> findByDailyQuestionAndUser(DailyQuestion dailyQuestion, User user);

    List<Answer> findByDailyQuestion(DailyQuestion dailyQuestion);
}
