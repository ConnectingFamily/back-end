package familyConnection.domain.comment.repository;

import familyConnection.domain.comment.entity.Comment;
import familyConnection.domain.question.entity.DailyQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  // 특정 DailyQuestion에 달린 삭제되지 않은 댓글들을 생성일시 내림차순(최신순)으로 조회
  List<Comment> findByDailyQuestionAndIsDeletedFalseOrderByCreatedAtDesc(DailyQuestion dailyQuestion);
}
