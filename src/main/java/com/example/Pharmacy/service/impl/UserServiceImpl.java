package com.example.Pharmacy.service.impl;


import com.example.Pharmacy.dtos.request.UserRequest;
import com.example.Pharmacy.dtos.responses.UserResponse;
import com.example.Pharmacy.exception.UserException;
import com.example.Pharmacy.mapper.UserMapper;
import com.example.Pharmacy.model.User;
import com.example.Pharmacy.repo.UserRepository;
import com.example.Pharmacy.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static com.example.Pharmacy.messages.User.UserExceptionMessages.PASSWORD_CANNOT_BE_EMPTY;
import static com.example.Pharmacy.messages.User.UserExceptionMessages.USER_NOT_FOUND;
import static com.example.Pharmacy.messages.User.UserLogMessages.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Get the username of the current user
     *
     * @return
     */
    public String getUsername() {
        // Return the username of the current user
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /**
     * Get the details of the current user
     *
     * @return
     */
    @Override
    public UserResponse getUserDetails() {
        // Get the details of the current user
        User user = userRepository.findByUsername(getUsername()).orElseThrow(() -> {
            log.error(LOG_USER_NOT_FOUND);
            return new UserException(USER_NOT_FOUND);
        });

        // Return the user details
        return userMapper.toUserResponse(user);
    }

    /**
     * Get the details of a user by username
     *
     * @param username
     * @return
     */
    @Override
    public UserResponse getUserDetails(String username) {
        // Get the details of the user by username
        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            log.error(LOG_USER_NOT_FOUND);
            return new UserException(USER_NOT_FOUND);
        });

        // Return the user details
        return userMapper.toUserResponse(user);
    }

    /**
     * Update the password of the current user
     *
     * @param request
     * @return
     */
    @Override
    public UserResponse updatePassword(UserRequest request) {
        // Convert the request to user
        User user = userMapper.toUser(request);

        // Check if the password is not empty
        if (!user.getPassword().isEmpty()) {
            try {
                // Update the password
                userRepository.updatePassword(user.getPassword(), getUsername());
                log.info(LOG_PASSWORD_UPDATED_SUCCESSFULLY);
                return getUserDetails();
            } catch (Exception exception) {
                // If any exception come, throw the exception
                log.error(String.format(LOG_UNABLE_UPDATE_PASSWORD, exception.getMessage()));
                throw new UserException(exception.getMessage());
            }
        } else {
            // If password is empty, throw the exception
            log.error(LOG_PASSWORD_CANNOT_BE_EMPTY);
            throw new UserException(PASSWORD_CANNOT_BE_EMPTY);
        }
    }

    /**
     * Update the details of the current user
     *
     * @param userRequest
     * @return
     */
    @Override
    public UserResponse updateUserDetails(UserRequest userRequest) {
        // Convert the request to user
        User user = userMapper.toUser(userRequest);
        try {
            // Update the details of the user
            userRepository.updateUserDetails(user.getFirstName(), user.getLastName(), user.getEmail(), user.getContact(), getUsername());
            log.info(LOG_USER_DETAILS_UPDATED);
            return getUserDetails();
        } catch (Exception exception) {
            log.error(String.format(LOG_UNABLE_TO_UPDATE_USER_DETAILS, exception.getMessage()));
            throw new UserException(exception.getMessage());
        }
    }

    /**
     * Get all users
     *
     * @param pageable
     * @return
     */
    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        // Get all users
        Page<User> users = userRepository.findAll(pageable);
        return userMapper.toUserResponse(users);
    }
}
