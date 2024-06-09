package com.onebank.onebank.auth.dto.input;
import lombok.Data;
import javax.validation.constraints.NotEmpty;


@Data
public class LoginRequest {
    @NotEmpty(message = "username")
    private String username;
    @NotEmpty(message = "password")
    private String password;

}