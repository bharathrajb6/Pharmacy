package com.example.Pharmacy.service;

import com.example.Pharmacy.dtos.request.UserRequest;
import com.example.Pharmacy.dtos.responses.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse getUserDetails();

    UserResponse getUserDetails(String username);

    UserResponse updatePassword(UserRequest request);

    UserResponse updateUserDetails(UserRequest userRequest);

    Page<UserResponse> getAllUsers(Pageable pageable);
}
