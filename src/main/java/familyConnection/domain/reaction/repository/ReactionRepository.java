package familyConnection.domain.reaction.repository;

import familyConnection.domain.reaction.entity.Reaction;
import familyConnection.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    // 특정 대상에 달린 반응 전부
    List<Reaction> findByTargetTypeAndTargetId(String targetType, Long targetId);

    // 내가 이미 눌렀는지 확인
    Optional<Reaction> findByTargetTypeAndTargetIdAndUserAndEmojiType(
            String targetType,
            Long targetId,
            User user,
            String emojiType
    );
}
