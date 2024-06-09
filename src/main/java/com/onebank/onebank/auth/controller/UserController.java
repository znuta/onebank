package com.onebank.onebank.auth.controller;

import com.onebank.onebank.auth.dto.output.ApiKeyResponseDTO;
import com.onebank.onebank.auth.dto.output.UserAuthResponseDTO;
import com.onebank.onebank.auth.service.ApiKeyService;
import com.onebank.onebank.auth.service.UserService;
import com.onebank.onebank.basicController.Controller;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController extends Controller {

    private ApiKeyService apiKeyService;
    private UserService userService;

    public UserController(ApiKeyService apiKeyService, UserService userService) {
        this.apiKeyService = apiKeyService;
        this.userService = userService;
    }

    @GetMapping("/")
    public UserAuthResponseDTO getUser(Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return  updateHttpStatus(userService.getUserDetails(user.getUsername()));
    }

    @GetMapping("/generateApiKey")
    public ApiKeyResponseDTO generateApiKey(Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return  updateHttpStatus(apiKeyService.generateApiKey(user.getUsername()));
    }

}
