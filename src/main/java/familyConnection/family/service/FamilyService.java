package familyConnection.family.service;

import familyConnection.family.Family;
import familyConnection.family.FamilyMember;
import familyConnection.family.dto.CreateFamilyRequestDto;
import familyConnection.family.dto.FamilyResponseDto;
import familyConnection.family.dto.FamilySearchResponseDto;
import familyConnection.family.dto.MemberDto;
import familyConnection.family.repository.FamilyMemberRepository;
import familyConnection.family.repository.FamilyRepository;
import familyConnection.user.User;
import familyConnection.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FamilyService {

  private final FamilyRepository familyRepository;
  private final FamilyMemberRepository familyMemberRepository;
  private final UserRepository userRepository;
  private static final String INVITE_CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final int INVITE_CODE_LENGTH = 10;
  private static final Random random = new SecureRandom();

  @Transactional
  public FamilyResponseDto createFamily(Long userId, CreateFamilyRequestDto request) {
    // 사용자 조회
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

    // 초대 코드 생성 (중복 체크)
    String inviteCode = generateUniqueInviteCode();

    // 가족 생성
    Family family = Family.builder()
        .familyName(request.getFamilyName())
        .inviteCode(inviteCode)
        .createdBy(user)
        .isActive(true)
        .build();

    Family savedFamily = familyRepository.save(family);

    // 생성자를 FamilyMember에 ADMIN으로 추가
    FamilyMember creatorMember = FamilyMember.builder()
        .family(savedFamily)
        .user(user)
        .role("ADMIN")
        .isActive(true)
        .build();
    familyMemberRepository.save(creatorMember);

    return FamilyResponseDto.builder()
        .familyId(savedFamily.getFamilyId())
        .familyName(savedFamily.getFamilyName())
        .inviteCode(savedFamily.getInviteCode())
        .createdBy(savedFamily.getCreatedBy().getId())
        .createdAt(savedFamily.getCreatedAt())
        .build();
  }

  private String generateUniqueInviteCode() {
    String inviteCode;
    int maxAttempts = 10;
    int attempts = 0;

    do {
      inviteCode = generateInviteCode();
      attempts++;
      if (attempts >= maxAttempts) {
        throw new RuntimeException("초대 코드 생성에 실패했습니다. 다시 시도해주세요.");
      }
    } while (familyRepository.existsByInviteCode(inviteCode));

    return inviteCode;
  }

  private String generateInviteCode() {
    StringBuilder code = new StringBuilder(INVITE_CODE_LENGTH);
    for (int i = 0; i < INVITE_CODE_LENGTH; i++) {
      code.append(INVITE_CODE_CHARS.charAt(random.nextInt(INVITE_CODE_CHARS.length())));
    }
    return code.toString();
  }

  @Transactional(readOnly = true)
  public FamilySearchResponseDto searchFamilyByInviteCode(String inviteCode) {
    // 초대 코드로 가족 조회
    Family family = familyRepository.findByInviteCode(inviteCode)
        .orElseThrow(() -> new RuntimeException("유효하지 않은 초대 코드입니다."));

    // 가족이 비활성화된 경우
    if (!family.getIsActive()) {
      throw new RuntimeException("유효하지 않은 초대 코드입니다.");
    }

    // 활성 멤버 목록 조회
    List<FamilyMember> activeMembers = familyMemberRepository.findByFamilyAndIsActiveTrueWithUser(family);

    // MemberDto 리스트 생성
    List<MemberDto> members = activeMembers.stream()
        .map(member -> {
          User user = member.getUser();
          // 가족 내 닉네임이 있으면 우선 사용, 없으면 User의 nickname 사용
          String nickname = member.getNicknameInFamily() != null && !member.getNicknameInFamily().isEmpty()
              ? member.getNicknameInFamily()
              : (user.getNickname() != null ? user.getNickname() : "이름 없음");

          return MemberDto.builder()
              .userId(user.getId())
              .nickname(nickname)
              .profileImageUrl(user.getProfileImageUrl())
              .build();
        })
        .collect(Collectors.toList());

    return FamilySearchResponseDto.builder()
        .familyId(family.getFamilyId())
        .familyName(family.getFamilyName())
        .inviteCode(family.getInviteCode())
        .memberCount(members.size())
        .members(members)
        .build();
  }
}
