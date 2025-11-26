package com.swulion.crossnote.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

@RestControllerAdvice
public class SseExceptionHandler {

    /**
     * SSE 연결 타임아웃/끊김 예외는 클라이언트 연결 특성이므로
     * 별도 응답을 보내지 않고 조용히 무시
     */
    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public void handleSseTimeout() {
        // SSE는 timeout이 자주 발생하므로 아무 응답도 하지 않음
    }
}
