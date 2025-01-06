package com.example.Pharmacy.service;

import com.example.Pharmacy.dtos.request.UserRequest;

public interface AuthenticationService {

    String register(UserRequest request);

    String authenticate(UserRequest request);
}
