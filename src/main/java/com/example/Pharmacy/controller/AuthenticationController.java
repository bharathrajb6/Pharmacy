package com.example.Pharmacy.controller;

import com.example.Pharmacy.dtos.request.UserRequest;
import com.example.Pharmacy.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * This method is used to register a new user
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@RequestBody UserRequest request) {
        return authenticationService.register(request);
    }

    /**
     * This method is used to authenticate a user
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String authenticate(@RequestBody UserRequest request) {
        return authenticationService.authenticate(request);
    }
}
