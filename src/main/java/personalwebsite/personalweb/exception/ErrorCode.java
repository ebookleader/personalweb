package personalwebsite.personalweb.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    /* 400 BAD_REQUEST */
    EMPTY_OBJECT(HttpStatus.BAD_REQUEST, "데이터가 비어있습니다."),
    EMPTY_UPLOAD_FILE(HttpStatus.BAD_REQUEST, "업로드 파일이 비어있습니다."),
    COMMENT_USERNAME_NULL(HttpStatus.BAD_REQUEST, "댓글 작성자 이름이 비어있습니다."),

    /* 404 NOT FOUND */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "포스트를 찾을 수 없습니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "이미지를 찾을 수 없습니다."),
    UPLOAD_FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    ALARM_NOT_FOUND(HttpStatus.NOT_FOUND, "알림을 찾을 수 없습니다."),

    /* 409 CONFLICT */
    ALREADY_SIGNUP(HttpStatus.CONFLICT, "이미 다른 아이디로 가입되어 있습니다."),
    DUPLICATE_COMMENT_USERNAME(HttpStatus.CONFLICT, "중복된 댓글 닉네임입니다."),

    /* 500 INTERNAL_SERVER_ERROR */
    FAIL_STORE_FILE(HttpStatus.INTERNAL_SERVER_ERROR, "업로드 파일 저장에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
