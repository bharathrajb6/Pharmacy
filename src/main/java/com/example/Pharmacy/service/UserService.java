package com.example.Pharmacy.service;

import com.example.Pharmacy.dtos.request.UserRequest;
import com.example.Pharmacy.dtos.responses.UserResponse;

public interface UserService {

    public UserResponse getUserDetails();

    UserResponse updatePassword(UserRequest request);

    UserResponse updateUserDetails(UserRequest userRequest);
}
