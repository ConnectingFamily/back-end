package familyConnection.domain.user.controller;

import org.springframework.data.jpa.repository.JpaRepository;

import familyConnection.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByKakaoId(String kakaoId);
}
