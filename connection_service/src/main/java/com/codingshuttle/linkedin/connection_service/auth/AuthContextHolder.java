package com.codingshuttle.linkedin.connection_service.auth;

public class AuthContextHolder {

    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();

    public static Long getCurrrentUserId() {
        return currentUserId.get();
    }

    public static void setCurrrentUserId(Long userId) {
        currentUserId.set(userId);
    }

    public static void removeCurrrentUserId() {
        currentUserId.remove();
    }
}