package familyConnection.domain.record.service;

import familyConnection.domain.family.entity.Family;
import familyConnection.domain.family.repository.FamilyMemberRepository;
import familyConnection.domain.family.repository.FamilyRepository;
import familyConnection.domain.question.entity.Answer;
import familyConnection.domain.question.entity.DailyQuestion;
import familyConnection.domain.question.repository.AnswerRepository;
import familyConnection.domain.question.repository.DailyQuestionRepository;
import familyConnection.domain.record.dto.*;
import familyConnection.domain.user.entity.User;
import familyConnection.domain.user.repository.UserRepository;
import familyConnection.global.apiPayload.code.status.ErrorStatus;
import familyConnection.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final AnswerRepository answerRepository;
    private final DailyQuestionRepository dailyQuestionRepository;

    /**
     * 8.1 달력용: 특정 달에 내가 답한 날짜들 + 개수 + 연속일수
     */
    @Transactional(readOnly = true)
    public MonthlyRecordResponseDto getMonthlyRecords(Long userId, Long familyId, int year, int month) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus._USER_NOT_FOUND));

        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new CustomException(ErrorStatus._FAMILY_NOT_FOUND));

        // 이 가족에 실제로 속해 있는지 체크
        familyMemberRepository.findByUserAndFamilyAndIsActiveTrue(user, family)
                .orElseThrow(() -> new CustomException(ErrorStatus._FORBIDDEN));

        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);

        List<Answer> answers = answerRepository.findByUserAndFamilyAndSubmittedAtBetween(
                user, family, start, end);

        // 달력에 찍을 날짜들
        List<LocalDate> dates = answers.stream()
                .map(a -> a.getSubmittedAt().toLocalDate())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // 연속 일수 계산 (단순히 이 달 안에서만)
        int maxStreak = calcMaxStreak(dates);

        return MonthlyRecordResponseDto.builder()
                .familyId(familyId)
                .year(year)
                .month(month)
                .answeredDates(dates)
                .totalAnswered(dates.size())
                .continuousDays(maxStreak)
                .build();
    }

    /**
     * 8.2 답변 기록 목록 (최근 순)
     */
    @Transactional(readOnly = true)
    public RecordListResponseDto getRecordList(Long userId, Long familyId, int page, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus._USER_NOT_FOUND));

        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new CustomException(ErrorStatus._FAMILY_NOT_FOUND));

        familyMemberRepository.findByUserAndFamilyAndIsActiveTrue(user, family)
                .orElseThrow(() -> new CustomException(ErrorStatus._FORBIDDEN));

        PageRequest pr = PageRequest.of(page - 1, limit);
        Page<Answer> answerPage = answerRepository.findByUserAndFamilyOrderBySubmittedAtDesc(user, family, pr);

        List<RecordListItemDto> records = answerPage.getContent().stream()
                .map(a -> {
                    DailyQuestion dq = a.getDailyQuestion();
                    return RecordListItemDto.builder()
                            .dailyQuestionId(dq.getDailyQuestionId())
                            .questionNumber(dq.getQuestion().getQuestionNumber())
                            .questionText(dq.getQuestion().getQuestionText())
                            .assignedDate(dq.getAssignedDate())
                            .myAnswer(a.getFinalAnswer())
                            .submittedAt(a.getSubmittedAt())
                            .build();
                })
                .collect(Collectors.toList());

        long totalCount = answerRepository.countByUserAndFamily(user, family);

        return RecordListResponseDto.builder()
                .records(records)
                .totalCount(totalCount)
                .page(page)
                .limit(limit)
                .build();
    }

    /**
     * 8.3 특정 질문에 대한 내 답변 상세
     */
    @Transactional(readOnly = true)
    public RecordDetailResponseDto getRecordDetail(Long userId, Long familyId, Long dailyQuestionId) {
        // 1. 유저, 가족 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus._USER_NOT_FOUND));

        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new CustomException(ErrorStatus._FAMILY_NOT_FOUND));

        // 이 가족에 속한 사람인지
        familyMemberRepository.findByUserAndFamilyAndIsActiveTrue(user, family)
                .orElseThrow(() -> new CustomException(ErrorStatus._FORBIDDEN));

        // 2. 일일 질문 가져오기
        DailyQuestion dailyQuestion = dailyQuestionRepository.findById(dailyQuestionId)
                .orElseThrow(() -> new CustomException(ErrorStatus._NOT_FOUND));

        // 3. 이 질문에 달린 답변 전부 (가족 전체)
        List<Answer> answers = answerRepository.findByDailyQuestion(dailyQuestion);

        // 4. DTO 변환
        List<FamilyAnswerDto> answerDtos = answers.stream()
                .map(a -> {
                    User answerUser = a.getUser();
                    return FamilyAnswerDto.builder()
                            .userId(answerUser.getId())
                            .nickname(
                                    answerUser.getNickname() != null
                                            ? answerUser.getNickname()
                                            : "이름 없음"
                            )
                            .profileImageUrl(answerUser.getProfileImageUrl())
                            .answer(a.getFinalAnswer())
                            .submittedAt(a.getSubmittedAt())
                            .build();
                })
                .toList();

        return RecordDetailResponseDto.builder()
                .dailyQuestionId(dailyQuestion.getDailyQuestionId())
                .questionNumber(dailyQuestion.getQuestion().getQuestionNumber())
                .questionText(dailyQuestion.getQuestion().getQuestionText())
                .counselingTechnique(dailyQuestion.getQuestion().getCounselingTechnique())
                .description(dailyQuestion.getQuestion().getDescription())
                .exampleAnswer(dailyQuestion.getQuestion().getExampleAnswer())
                .assignedDate(dailyQuestion.getAssignedDate())
                .answers(answerDtos)
                .build();
    }

    @Transactional(readOnly = true)
    public MyAnsweredQuestionListDto getMyAnsweredQuestions(Long userId, Long familyId, int page, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus._USER_NOT_FOUND));

        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new CustomException(ErrorStatus._FAMILY_NOT_FOUND));

        PageRequest pr = PageRequest.of(page - 1, limit);
        Page<Answer> answerPage =
                answerRepository.findByUserAndFamilyOrderBySubmittedAtDesc(user, family, pr);

        List<MyAnsweredQuestionDto> items = answerPage.getContent().stream()
                .map(a -> MyAnsweredQuestionDto.builder()
                        .dailyQuestionId(a.getDailyQuestion().getDailyQuestionId())
                        .questionNumber(a.getDailyQuestion().getQuestion().getQuestionNumber())
                        .questionText(a.getDailyQuestion().getQuestion().getQuestionText())
                        .assignedDate(a.getDailyQuestion().getAssignedDate())
                        .answerPreview(cut(a.getFinalAnswer(), 50))
                        .build())
                .toList();

        long totalCount = answerRepository.countByUserAndFamily(user, family);

        return MyAnsweredQuestionListDto.builder()
                .records(items)
                .totalCount(totalCount)
                .page(page)
                .limit(limit)
                .build();
    }

    private String cut(String s, int len) {
        if (s == null) return null;
        return s.length() > len ? s.substring(0, len) + "..." : s;
    }

    /**
     * 같은 달 안에서 연속한 날짜 최대치 계산
     */
    private int calcMaxStreak(List<LocalDate> dates) {
        if (dates.isEmpty()) return 0;
        // dates는 이미 정렬되어 들어왔다고 가정
        int max = 1;
        int current = 1;
        for (int i = 1; i < dates.size(); i++) {
            LocalDate prev = dates.get(i - 1);
            LocalDate cur = dates.get(i);
            if (prev.plusDays(1).equals(cur)) {
                current++;
            } else {
                max = Math.max(max, current);
                current = 1;
            }
        }
        max = Math.max(max, current);
        return max;
    }
}
