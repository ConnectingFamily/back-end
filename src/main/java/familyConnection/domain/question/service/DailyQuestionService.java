package familyConnection.domain.question.service;

import familyConnection.domain.family.entity.Family;
import familyConnection.domain.family.entity.FamilyMember;
import familyConnection.domain.family.repository.FamilyMemberRepository;
import familyConnection.domain.question.dto.AnswerDetailDto;
import familyConnection.domain.question.dto.AnswerRequestDto;
import familyConnection.domain.question.dto.DailyQuestionResponseDto;
import familyConnection.domain.question.dto.MemberAnswerStatusDto;
import familyConnection.domain.question.entity.Answer;
import familyConnection.domain.question.entity.DailyQuestion;
import familyConnection.domain.question.repository.AnswerRepository;
import familyConnection.domain.question.repository.DailyQuestionRepository;
import familyConnection.domain.user.entity.User;
import familyConnection.domain.user.repository.UserRepository;
import familyConnection.global.apiPayload.code.status.ErrorStatus;
import familyConnection.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyQuestionService {

    private final DailyQuestionRepository dailyQuestionRepository;
    private final AnswerRepository answerRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final UserRepository userRepository;

    /**
     * 1. 오늘자 가족 질문 가져오기
     */
    @Transactional(readOnly = true)
    public DailyQuestionResponseDto getTodayQuestion(Long userId) {
        // 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus._USER_NOT_FOUND));

        // 이 유저가 속한 가족 찾기
        FamilyMember myMember = familyMemberRepository.findByUserAndIsActiveTrue(user)
                .orElseThrow(() -> new CustomException(ErrorStatus._FAMILY_NOT_FOUND));
        Family family = myMember.getFamily();

        // 오늘 질문
        DailyQuestion dailyQuestion = dailyQuestionRepository
                .findByFamilyAndAssignedDate(family, LocalDate.now())
                .orElseThrow(() -> new CustomException(ErrorStatus._NOT_FOUND));

        // 오늘 질문에 대한 답변들
        List<Answer> answers = answerRepository.findByDailyQuestion(dailyQuestion);

        // 가족의 활성 멤버들
        List<FamilyMember> activeMembers =
                familyMemberRepository.findByFamilyAndIsActiveTrueWithUser(family);

        boolean isAllAnswered = activeMembers.size() == answers.size() && !answers.isEmpty();

        // 멤버별 상태 내려주기
        List<MemberAnswerStatusDto> memberDtos = activeMembers.stream()
                .map(member -> {
                    User mUser = member.getUser();

                    // 이 멤버가 쓴 답변 (있을 수도 있고 없을 수도 있음)
                    Answer memberAnswer = answers.stream()
                            .filter(a -> a.getUser().getId().equals(mUser.getId()))
                            .findFirst()
                            .orElse(null);

                    boolean answered = memberAnswer != null;

                    String nickname =
                            member.getNicknameInFamily() != null && !member.getNicknameInFamily().isEmpty()
                                    ? member.getNicknameInFamily()
                                    : (mUser.getNickname() != null ? mUser.getNickname() : "이름 없음");

                    return MemberAnswerStatusDto.builder()
                            .userId(mUser.getId())
                            .nickname(nickname)
                            .profileImageUrl(mUser.getProfileImageUrl())
                            .answered(answered)
                            // ✅ 공개 조건 만족하면 finalAnswer 내려줌
                            .answerContent(
                                    (isAllAnswered || mUser.getId().equals(userId)) && answered
                                            ? memberAnswer.getFinalAnswer()
                                            : null
                            )
                            .build();
                })
                .collect(Collectors.toList());

        return DailyQuestionResponseDto.builder()
                .dailyQuestionId(dailyQuestion.getDailyQuestionId())
                .questionId(dailyQuestion.getQuestion().getQuestionId())
                .questionNumber(dailyQuestion.getQuestion().getQuestionNumber())
                .questionText(dailyQuestion.getQuestion().getQuestionText())
                .counselingTechnique(dailyQuestion.getQuestion().getCounselingTechnique())
                .description(dailyQuestion.getQuestion().getDescription())
                .exampleAnswer(dailyQuestion.getQuestion().getExampleAnswer())
                .assignedDate(dailyQuestion.getAssignedDate())
                .isAllAnswered(isAllAnswered)
                .members(memberDtos)
                .build();
    }

    /**
     * 2. 답변 저장
     */
    @Transactional
    public void saveAnswer(Long userId, Long dailyQuestionId, AnswerRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus._USER_NOT_FOUND));

        DailyQuestion dailyQuestion = dailyQuestionRepository.findById(dailyQuestionId)
                .orElseThrow(() -> new CustomException(ErrorStatus._NOT_FOUND));

        // 같은 사람이 같은 질문에 여러 번 저장 못 하게
        answerRepository.findByDailyQuestionAndUser(dailyQuestion, user)
                .ifPresent(a -> { throw new CustomException(ErrorStatus._BAD_REQUEST); });

        // ✅ 여기서부터는 네가 바꾼 엔티티 필드에 맞춰서 세팅
        Answer answer = Answer.builder()
                .dailyQuestion(dailyQuestion)
                .user(user)
                .family(dailyQuestion.getFamily())     // ✅ 엔티티에 family 추가됐으니 꼭 넣어줌
                .originalAnswer(request.getContent())  // 원본
                .finalAnswer(request.getContent())     // 일단은 원본=최종으로 저장
                .isModified(false)
                .build();

        answerRepository.save(answer);

        // 저장 후 전원 완료 여부 체크
        List<FamilyMember> activeMembers =
                familyMemberRepository.findByFamilyAndIsActiveTrueWithUser(dailyQuestion.getFamily());
        List<Answer> answers = answerRepository.findByDailyQuestion(dailyQuestion);

        if (activeMembers.size() == answers.size()) {
            dailyQuestion.setIsAllAnswered(true);
        }
    }

    /**
     * 3. 같은 가족 답변 보기
     */
    @Transactional(readOnly = true)
    public List<AnswerDetailDto> getAnswers(Long userId, Long dailyQuestionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus._USER_NOT_FOUND));

        DailyQuestion dailyQuestion = dailyQuestionRepository.findById(dailyQuestionId)
                .orElseThrow(() -> new CustomException(ErrorStatus._NOT_FOUND));

        List<Answer> answers = answerRepository.findByDailyQuestion(dailyQuestion);

        boolean isAllAnswered = Boolean.TRUE.equals(dailyQuestion.getIsAllAnswered());

        return answers.stream()
                .map(a -> {
                    User answerUser = a.getUser();
                    return AnswerDetailDto.builder()
                            .userId(answerUser.getId())
                            .nickname(answerUser.getNickname())
                            .profileImageUrl(answerUser.getProfileImageUrl())
                            // ✅ 여기서도 finalAnswer로 내려줌
                            .content(
                                    isAllAnswered || answerUser.getId().equals(userId)
                                            ? a.getFinalAnswer()
                                            : null
                            )
                            .createdAt(a.getSubmittedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
