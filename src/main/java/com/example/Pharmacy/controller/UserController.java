package com.example.Pharmacy.controller;

import com.example.Pharmacy.dtos.request.UserRequest;
import com.example.Pharmacy.dtos.responses.UserResponse;
import com.example.Pharmacy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    @RequestMapping(value = "/user/details", method = RequestMethod.GET)
    public UserResponse getUserDetails() {
        return userService.getUserDetails();
    }

    @RequestMapping(value = "/user/updateDetails", method = RequestMethod.PUT)
    public UserResponse updateUserDetails(@RequestBody UserRequest request) {
        return userService.updateUserDetails(request);
    }

    @RequestMapping(value = "/user/updatePassword", method = RequestMethod.PUT)
    public UserResponse updatePassword(@RequestBody UserRequest request) {
        return userService.updatePassword(request);
    }
}
