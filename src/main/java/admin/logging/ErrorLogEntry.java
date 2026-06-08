package admin.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorLogEntry {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final LocalDateTime occurredAt;
    private final String method;
    private final String requestUri;
    private final String queryString;
    private final String userId;
    private final String exceptionType;
    private final String message;
    private final String stackTrace;

    public ErrorLogEntry(String method, String requestUri, String queryString, String userId,
            String exceptionType, String message, String stackTrace) {
        this.occurredAt = LocalDateTime.now();
        this.method = method;
        this.requestUri = requestUri;
        this.queryString = queryString;
        this.userId = userId;
        this.exceptionType = exceptionType;
        this.message = message;
        this.stackTrace = stackTrace;
    }

    public String getOccurredAtText() {
        return occurredAt.format(FORMATTER);
    }

    public String getMethod() {
        return method;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getUserId() {
        return userId;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public String getMessage() {
        return message;
    }

    public String getStackTrace() {
        return stackTrace;
    }
}
