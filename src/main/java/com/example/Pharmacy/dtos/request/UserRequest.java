package com.example.Pharmacy.dtos.request;

import com.example.Pharmacy.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private String contact;
    private Role role;

}
