package com.trvanket.app.service;

public interface ValidateService {
    boolean isValidUser(String accessToken);
    boolean isValidAdmin(String accessToken);

}
