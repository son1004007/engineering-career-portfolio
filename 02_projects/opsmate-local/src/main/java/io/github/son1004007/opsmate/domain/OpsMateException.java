package io.github.son1004007.opsmate.domain;

public class OpsMateException extends RuntimeException {

    private final ErrorCode code;

    public OpsMateException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public OpsMateException(ErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }
}
