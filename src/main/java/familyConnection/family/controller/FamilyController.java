package familyConnection.family.controller;

import familyConnection.family.dto.CreateFamilyRequestDto;
import familyConnection.family.dto.FamilyResponseDto;
import familyConnection.family.dto.FamilySearchResponseDto;
import familyConnection.family.service.FamilyService;
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

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/families")
@RequiredArgsConstructor
public class FamilyController {

  private final FamilyService familyService;

  @PostMapping
  public ResponseEntity<Map<String, Object>> createFamily(
      @Valid @RequestBody CreateFamilyRequestDto request) {

    // JWT에서 사용자 ID 추출
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Long userId = Long.parseLong(authentication.getName());

    // 가족 생성
    FamilyResponseDto familyResponse = familyService.createFamily(userId, request);

    // 응답 구성
    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("data", familyResponse);
    response.put("message", "가족이 생성되었습니다.");

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/search")
  public ResponseEntity<Map<String, Object>> searchFamilyByInviteCode(
      @RequestParam("invite-code") String inviteCode) {

    // 초대 코드로 가족 검색
    FamilySearchResponseDto familySearch = familyService.searchFamilyByInviteCode(inviteCode);

    // 응답 구성
    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("data", familySearch);

    return ResponseEntity.ok(response);
  }
}
