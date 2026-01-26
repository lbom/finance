package com.finance.app;

public interface UserModule {
    Long retrieveUserId();
    void hasAuthority(Long personId);
}
