package com.example.Pharmacy.mapper;

import com.example.Pharmacy.dtos.request.UserRequest;
import com.example.Pharmacy.dtos.responses.UserResponse;
import com.example.Pharmacy.model.User;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserRequest request);

    UserResponse toUserResponse(User user);

    default Page<UserResponse> toUserResponse(Page<User> users) {
        List<UserResponse> responses = users.getContent()
                .stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(responses, users.getPageable(), users.getTotalElements());
    }
}
