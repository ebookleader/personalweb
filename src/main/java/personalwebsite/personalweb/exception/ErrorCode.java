package personalwebsite.personalweb.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    /* 409 CONFLICT */
    ALREADY_SIGNUP(HttpStatus.CONFLICT, "이미 다른 아이디로 가입되어 있습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
