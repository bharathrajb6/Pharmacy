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
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /**
     * Get the details of the current user
     *
     * @return
     */
    @Override
    public UserResponse getUserDetails() {
        User user = userRepository.findByUsername(getUsername()).orElseThrow(() -> {
            log.error("User not found");
            return new UserException("User not found");
        });
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
        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            return new UserException("User not found");
        });
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
        User user = userMapper.toUser(request);
        if (!user.getPassword().isEmpty()) {
            try {
                userRepository.updatePassword(user.getPassword(), getUsername());
                return getUserDetails();
            } catch (Exception exception) {
                throw new UserException(exception.getMessage());
            }
        } else {
            return null;
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
        User user = userMapper.toUser(userRequest);
        try {
            userRepository.updateUserDetails(user.getFirstName(), user.getLastName(), user.getEmail(), user.getContact(), getUsername());
        } catch (Exception exception) {
            throw new UserException(exception.getMessage());
        }
        return getUserDetails();
    }

    /**
     * Get all users
     *
     * @param pageable
     * @return
     */
    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return userMapper.toUserResponse(users);
    }
}
