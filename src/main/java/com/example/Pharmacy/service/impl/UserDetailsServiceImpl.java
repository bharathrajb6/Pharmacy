package com.example.Pharmacy.service.impl;

import com.example.Pharmacy.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.example.Pharmacy.messages.User.UserExceptionMessages.USER_NOT_FOUND;
import static com.example.Pharmacy.messages.User.UserLogMessages.LOG_USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * This method is used to load the user details based on username
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() ->
        {
            log.error(LOG_USER_NOT_FOUND);
            return new UsernameNotFoundException(USER_NOT_FOUND);
        });
    }
}
