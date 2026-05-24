package com.codingshuttle.linkedin.connection_service.auth;

public class AuthContextHolder {

    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();

    public static Long getCurrrentUserId() {
        return currentUserId.get();
    }

    static void setCurrrentUserId(Long userId) {
        currentUserId.set(userId);
    }

    static void removeCurrrentUserId() {
        currentUserId.remove();
    }
}