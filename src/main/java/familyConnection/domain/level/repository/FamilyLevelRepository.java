package familyConnection.domain.level.repository;

import familyConnection.domain.family.entity.Family;
import familyConnection.domain.level.entity.FamilyLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FamilyLevelRepository extends JpaRepository<FamilyLevel, Long> {

    // Family 엔티티를 기준으로 FamilyLevel 찾기
    Optional<FamilyLevel> findByFamily(Family family);

    // 혹시 family_id로 직접 찾고 싶을 때
    Optional<FamilyLevel> findByFamily_FamilyId(Long familyId);
}
