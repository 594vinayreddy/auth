package com.ewallet.auth_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEvent implements Serializable {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private BigDecimal phoneNumber;
    private Date dateOfBirth;
}
