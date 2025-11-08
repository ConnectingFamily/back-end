package familyConnection.domain.family.controller;

import familyConnection.domain.family.dto.CreateFamilyRequestDto;
import familyConnection.domain.family.dto.FamilyResponseDto;
import familyConnection.domain.family.dto.FamilySearchResponseDto;
import familyConnection.domain.family.service.FamilyService;
import familyConnection.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/families")
@RequiredArgsConstructor
public class FamilyController {

  private final FamilyService familyService;

  @PostMapping
  public ResponseEntity<ApiResponse<FamilyResponseDto>> createFamily(
      @Valid @RequestBody CreateFamilyRequestDto request) {

    // JWT에서 사용자 ID 추출
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Long userId = Long.parseLong(authentication.getName());

    // 가족 생성
    FamilyResponseDto familyResponse = familyService.createFamily(userId, request);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.onSuccess(familyResponse));
  }

  @GetMapping("/search")
  public ResponseEntity<ApiResponse<FamilySearchResponseDto>> searchFamilyByInviteCode(
      @RequestParam("invite-code") String inviteCode) {

    // 초대 코드로 가족 검색
    FamilySearchResponseDto familySearch = familyService.searchFamilyByInviteCode(inviteCode);

    return ResponseEntity.ok(ApiResponse.onSuccess(familySearch));
  }
}
