package familyConnection.domain.aianswerhelper.service;

import familyConnection.domain.aianswerhelper.AnswerAiHelper;
import familyConnection.domain.aianswerhelper.dto.AnswerDraftRequestDto;
import familyConnection.domain.aianswerhelper.dto.AnswerDraftResponseDto;
import familyConnection.domain.aianswerhelper.dto.AnswerFinalizeRequestDto;
import familyConnection.domain.family.entity.FamilyMember;
import familyConnection.domain.family.repository.FamilyMemberRepository;
import familyConnection.domain.level.service.FamilyAnswerLevelService;

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

import java.util.List;

/**
 * [AnswerSubmissionService]
 * - 오늘 질문에 대한 초안(draft) 제출 및 AI 피드백
 * - 최종 답변 제출 및 가족 레벨 반영
 */
@Service
@RequiredArgsConstructor
public class AnswerSubmissionService {

    private final DailyQuestionRepository dailyQuestionRepository;
    private final AnswerRepository answerRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final UserRepository userRepository;
    private final FamilyAnswerLevelService familyAnswerLevelService;
    private final AnswerAiHelper answerAiHelper;

    /**
     * 1️⃣ 초안 저장 + AI 피드백 요청
     */
    @Transactional
    public AnswerDraftResponseDto createDraftAnswer(Long userId, Long dailyQuestionId, AnswerDraftRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus._USER_NOT_FOUND));

        DailyQuestion dailyQuestion = dailyQuestionRepository.findById(dailyQuestionId)
                .orElseThrow(() -> new CustomException(ErrorStatus._NOT_FOUND));

        // 1) AI 호출 먼저
        var ai = answerAiHelper.analyze(
                dailyQuestion.getQuestion().getQuestionText(),
                request.getContent()
        );

        // 2) 기존 답변 있는지 확인
        Answer answer = answerRepository.findByDailyQuestionAndUser(dailyQuestion, user)
                .orElse(null);

        if (answer == null) {
            // 2-1) 없으면 새로 생성
            answer = Answer.builder()
                    .dailyQuestion(dailyQuestion)
                    .user(user)
                    .family(dailyQuestion.getFamily())
                    .originalAnswer(request.getContent())
                    .finalAnswer(request.getContent())  // 일단 원본과 동일
                    .aiFeedback(String.join("\n", ai.feedback()))
                    .isModified(false)
                    .build();
        } else {
            // 2-2) 있으면 초안만 덮어쓰기 (draft 다시 보낸 상황)
            answer.setOriginalAnswer(request.getContent());
            // 최종은 아직 사용자가 안 눌렀으니 일단 같이 맞춰둠
            answer.setFinalAnswer(request.getContent());
            answer.setAiFeedback(String.join("\n", ai.feedback()));
            answer.setIsModified(false);
        }

        answerRepository.save(answer);

        return AnswerDraftResponseDto.builder()
                .originalAnswer(request.getContent())
                .improvedAnswer(ai.improvedAnswer())
                .emotion(ai.emotion())
                .feedback(ai.feedback())
                .build();
    }

    /**
     * 2️⃣ 최종 답변 제출
     */
    @Transactional
    public void finalizeAnswer(Long userId, Long dailyQuestionId, AnswerFinalizeRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus._USER_NOT_FOUND));

        DailyQuestion dailyQuestion = dailyQuestionRepository.findById(dailyQuestionId)
                .orElseThrow(() -> new CustomException(ErrorStatus._NOT_FOUND));

        Answer answer = answerRepository.findByDailyQuestionAndUser(dailyQuestion, user)
                .orElseThrow(() -> new CustomException(ErrorStatus._NOT_FOUND));

        // 최종 답변 확정
        answer.setFinalAnswer(request.getFinalContent());
        answer.setIsModified(true);

        // 가족 포인트/레벨 갱신
        familyAnswerLevelService.increaseAnswerCount(dailyQuestion.getFamily());

        // 가족 모두 답변 완료 여부 확인
        List<FamilyMember> activeMembers =
                familyMemberRepository.findByFamilyAndIsActiveTrueWithUser(dailyQuestion.getFamily());
        List<Answer> answers = answerRepository.findByDailyQuestion(dailyQuestion);

        if (activeMembers.size() == answers.size()) {
            dailyQuestion.setIsAllAnswered(true);
        }
    }
}