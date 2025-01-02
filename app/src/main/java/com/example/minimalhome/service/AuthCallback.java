package com.example.minimalhome.service;

public interface AuthCallback {
    void onSuccess(String token);
    void onError(String message);
    void onComplete();
}
