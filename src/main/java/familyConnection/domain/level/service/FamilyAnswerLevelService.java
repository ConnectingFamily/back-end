package familyConnection.domain.level.service;

import familyConnection.domain.family.entity.Family;
import familyConnection.domain.level.entity.FamilyLevel;
import familyConnection.domain.level.policy.AnswerCountLevelPolicy;
import familyConnection.domain.level.repository.FamilyLevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FamilyAnswerLevelService {

    private final FamilyLevelRepository familyLevelRepository;
    private final AnswerCountLevelPolicy levelPolicy;

    /**
     * 가족이 답변을 1개 남겼을 때 호출
     */
    public void increaseAnswerCount(Family family) {
        // 1. 가족 레벨 엔티티 조회 (없으면 생성)
        FamilyLevel familyLevel = familyLevelRepository.findByFamily(family)
                .orElseGet(() -> createInitLevel(family));

        // 2. 누적 답변 수 +1 (currentPoints를 '누적 답변 수'로 사용)
        int newAnswerCount = familyLevel.getCurrentPoints() + 1;
        familyLevel.setCurrentPoints(newAnswerCount);

        // 3. 개수에 맞춰 레벨 다시 계산
        int resolvedLevel = levelPolicy.resolveLevelByAnswerCount(newAnswerCount);

        // 4. 변경이 있을 때만 반영
        if (resolvedLevel != familyLevel.getCurrentLevel()) {
            familyLevel.setCurrentLevel(resolvedLevel);
            familyLevel.setLevelName(levelPolicy.resolveLevelName(resolvedLevel));
        }
        // updatedAt은 @UpdateTimestamp가 알아서 찍어줄 거야
    }

    private FamilyLevel createInitLevel(Family family) {
        return familyLevelRepository.save(
                FamilyLevel.builder()
                        .family(family)
                        .currentLevel(1)
                        .currentPoints(0) // 누적 답변 수 0
                        .levelName("낯선 마음")
                        .build()
        );
    }

    @Transactional
    public void decreaseAnswerCount(Family family) {
        FamilyLevel familyLevel = familyLevelRepository
                .findByFamily(family)
                .orElseThrow(() -> new IllegalStateException("FamilyLevel not found"));

        // 1. 포인트(답변 수) -1 (엔티티 단에서 0 미만 방지)
        familyLevel.decreaseAnswerCount();

        // 2. 감소된 포인트 기준으로 레벨 재계산
        int newAnswerCount = familyLevel.getCurrentPoints();
        int resolvedLevel = levelPolicy.resolveLevelByAnswerCount(newAnswerCount);

        // 3. 레벨 변경이 필요할 때만 반영
        if (resolvedLevel != familyLevel.getCurrentLevel()) {
            familyLevel.setCurrentLevel(resolvedLevel);
            familyLevel.setLevelName(levelPolicy.resolveLevelName(resolvedLevel));
        }
    }
}
