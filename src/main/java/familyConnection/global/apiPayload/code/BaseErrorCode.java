package familyConnection.global.apiPayload.code;

public interface BaseErrorCode {
  ErrorReasonDto getReason();
  ErrorReasonDto getReasonHttpStatus();
}
