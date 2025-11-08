package familyConnection.domain.family.repository;

import familyConnection.domain.family.entity.Family;
import familyConnection.domain.family.entity.FamilyMember;
import familyConnection.domain.user.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {
  Optional<FamilyMember> findByUserAndIsActiveTrue(User user);

  boolean existsByUserAndIsActiveTrue(User user);

  // FamilyMember와 Family 모두 활성화된 경우만 체크
  @Query("SELECT COUNT(fm) > 0 FROM FamilyMember fm " +
      "WHERE fm.user = :user " +
      "AND fm.isActive = true " +
      "AND fm.family.isActive = true")
  boolean existsByUserAndMemberAndFamilyActive(@Param("user") User user);

  // 가족의 활성 멤버 목록 조회
  List<FamilyMember> findByFamilyAndIsActiveTrue(Family family);

  // 가족의 활성 멤버 목록 조회 (User 함께 조회 - LazyInitializationException 방지)
  @Query("SELECT fm FROM FamilyMember fm " +
      "JOIN FETCH fm.user " +
      "WHERE fm.family = :family " +
      "AND fm.isActive = true")
  List<FamilyMember> findByFamilyAndIsActiveTrueWithUser(@Param("family") Family family);
}
