package admin.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import member.dto.MemberDTO;

public class ErrorLogStore {

    private static final int MAX_LOG_COUNT = 100;
    private static final LinkedList<ErrorLogEntry> logs = new LinkedList<>();

    private ErrorLogStore() {
    }

    public static synchronized void add(HttpServletRequest request, Throwable error) {
        logs.addFirst(new ErrorLogEntry(
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                resolveUserId(request),
                error.getClass().getName(),
                error.getMessage(),
                stackTraceToString(error)));

        while (logs.size() > MAX_LOG_COUNT) {
            logs.removeLast();
        }
    }

    public static synchronized List<ErrorLogEntry> getLogs() {
        return Collections.unmodifiableList(new ArrayList<>(logs));
    }

    public static synchronized void clear() {
        logs.clear();
    }

    private static String resolveUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Object loginMember = session.getAttribute("loginMember");
        if (loginMember instanceof MemberDTO) {
            return ((MemberDTO) loginMember).getUserId();
        }

        return null;
    }

    private static String stackTraceToString(Throwable error) {
        StringWriter writer = new StringWriter();
        error.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
