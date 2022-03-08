package personalwebsite.personalweb.web.advice;

import org.springframework.http.HttpStatus;

public class RestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private HttpStatus httpStatus;
    private String message;

    public RestException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
