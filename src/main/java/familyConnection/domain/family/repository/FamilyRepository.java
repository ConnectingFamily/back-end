package familyConnection.domain.family.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import familyConnection.domain.family.entity.Family;

import java.util.Optional;

public interface FamilyRepository extends JpaRepository<Family, Long> {
  Optional<Family> findByInviteCode(String inviteCode);

  boolean existsByInviteCode(String inviteCode);
}
