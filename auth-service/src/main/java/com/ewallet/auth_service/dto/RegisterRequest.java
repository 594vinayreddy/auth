package com.ewallet.auth_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private BigDecimal phoneNumber;
    private Date dateOfBirth;
}
