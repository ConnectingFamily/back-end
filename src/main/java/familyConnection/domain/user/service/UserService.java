package familyConnection.domain.user.service;

import familyConnection.domain.family.repository.FamilyMemberRepository;
import familyConnection.domain.user.dto.UpdateProfileRequestDto;
import familyConnection.domain.user.dto.UserProfileResponseDto;
import familyConnection.domain.user.entity.User;
import familyConnection.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final FamilyMemberRepository familyMemberRepository;

  @Transactional
  public UserProfileResponseDto updateProfile(Long userId, UpdateProfileRequestDto request) {
    // 사용자 조회
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

    // 프로필 업데이트
    user.setNickname(request.getNickname());

    // 프로필 사진은 선택사항이므로 null이 아닐 때만 업데이트
    if (request.getProfileImageUrl() != null && !request.getProfileImageUrl().trim().isEmpty()) {
      user.setProfileImageUrl(request.getProfileImageUrl());
    }

    User updatedUser = userRepository.save(user);

    // 해당 사용자의 활성화된 FamilyMember의 nicknameInFamily도 업데이트
    familyMemberRepository.findByUserAndIsActiveTrue(user)
        .ifPresent(member -> {
          member.setNicknameInFamily(request.getNickname());
          familyMemberRepository.save(member);
        });

    return UserProfileResponseDto.builder()
        .userId(updatedUser.getId())
        .nickname(updatedUser.getNickname())
        .profileImageUrl(updatedUser.getProfileImageUrl())
        .updatedAt(updatedUser.getUpdatedAt())
        .build();
  }
}
