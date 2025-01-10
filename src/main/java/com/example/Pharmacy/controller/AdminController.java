package com.example.Pharmacy.controller;

import com.example.Pharmacy.dtos.responses.UserResponse;
import com.example.Pharmacy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    /**
     * This method is used to get the user data
     *
     * @return
     */
    @RequestMapping(value = "/user/details", method = RequestMethod.GET)
    public UserResponse getUserData() {
        return userService.getUserDetails();
    }

    /**
     * Get user data by username
     *
     * @param username
     * @return
     */
    @RequestMapping(value = "/user/{username}", method = RequestMethod.GET)
    public UserResponse getUserData(@PathVariable String username) {
        return userService.getUserDetails(username);
    }

    /**
     * Get all users data
     *
     * @return
     */
    @RequestMapping(value = "user/all", method = RequestMethod.GET)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

}
