package io.github.son1004007.opsmate.demo;

import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/** 만료되거나 제거된 데모 세션을 안전하게 비우고 공개 시작 화면으로 돌려보낸다. */
@ControllerAdvice(assignableTypes = DemoController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DemoWebExceptionHandler {

    @ExceptionHandler(OpsMateException.class)
    public String handleOpsMate(OpsMateException exception, HttpServletRequest request) {
        if (exception.getCode() == ErrorCode.DEMO_CLOSED) {
            return "redirect:/?status=closed";
        }
        if (exception.getCode() == ErrorCode.DEMO_CAPACITY_REACHED
                || exception.getCode() == ErrorCode.RATE_LIMITED) {
            return "redirect:/?status=busy";
        }
        SecurityContextHolder.clearContext();
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
        return "redirect:/?status=expired";
    }
}
