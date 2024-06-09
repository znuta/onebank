package com.onebank.onebank.auth.service.implementation;

import com.onebank.onebank.auth.dto.output.UserAuthResponseDTO;
import com.onebank.onebank.auth.entity.AppUser;
import com.onebank.onebank.auth.repository.UserRepository;
import com.onebank.onebank.auth.service.UserService;
import com.onebank.onebank.auth.utilities.UserUtil;
import com.onebank.onebank.dto.enums.Status;
import com.onebank.onebank.payment.dto.output.PaymentAccountDTO;
import com.onebank.onebank.payment.service.PaymentAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final UserUtil userUtil;
    private final PaymentAccountService paymentAccountService;

    /**
     * Constructor for UserServiceImp
     *
     * @param userRepository        Repository for managing users
     * @param userUtil              Utility for user-related operations
     * @param paymentAccountService Service for managing payment accounts
     */
    @Autowired
    public UserServiceImp(UserRepository userRepository, UserUtil userUtil,@Lazy PaymentAccountService paymentAccountService) {
        this.userRepository = userRepository;
        this.userUtil = userUtil;
        this.paymentAccountService = paymentAccountService;
    }

    /**
     * Loads user details by username.
     *
     * @param username The username of the user
     * @return UserDetails of the user
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), userUtil.getAuthority(user));
    }

    /**
     * Retrieves user details along with their payment accounts.
     *
     * @param username The username of the user
     * @return UserAuthResponseDTO containing user details and payment accounts
     * @throws UsernameNotFoundException if the user is not found
     */
    public UserAuthResponseDTO getUserDetails(String username) {
        AppUser user = findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        List<PaymentAccountDTO> paymentAccounts = paymentAccountService.getPaymentAccounts(user);

        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("user", UserAuthResponseDTO.fromEntity(user));
        userResponse.put("paymentAccounts", paymentAccounts);

        return new UserAuthResponseDTO(Status.SUCCESS, userResponse);
    }

    /**
     * Finds a user by username.
     *
     * @param username The username to search for
     * @return Optional containing the user if found
     */
    public Optional<AppUser> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Finds a user by ID.
     *
     * @param id The ID to search for
     * @return Optional containing the user if found
     */
    public Optional<AppUser> findById(Long id) {
        return userRepository.findById(id);
    }
}
