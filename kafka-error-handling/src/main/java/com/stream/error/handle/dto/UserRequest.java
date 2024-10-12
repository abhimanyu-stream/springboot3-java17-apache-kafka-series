package com.stream.error.handle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String ipAddress;
}