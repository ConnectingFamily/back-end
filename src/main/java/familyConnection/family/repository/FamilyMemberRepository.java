package familyConnection.family.repository;

import familyConnection.family.FamilyMember;
import familyConnection.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
