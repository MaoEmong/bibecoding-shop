package com.example.myproject.common;

import jakarta.servlet.http.HttpSession;

public final class SessionUtil {

    public static final String SESSION_USER_ID = "USER_ID";
    public static final String SESSION_LAST_ORDER_ID = "LAST_ORDER_ID";

    private SessionUtil() {
    }

    public static Long requireUserId(HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return userId;
    }
}
