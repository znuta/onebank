package com.onebank.onebank.auth.service.implementation;

import com.onebank.onebank.auth.dto.output.ApiKeyResponseDTO;
import com.onebank.onebank.auth.entity.ApiKey;
import com.onebank.onebank.auth.entity.AppUser;
import com.onebank.onebank.auth.repository.ApiKeyRepository;
import com.onebank.onebank.auth.service.ApiKeyService;
import com.onebank.onebank.auth.service.UserService;
import com.onebank.onebank.dto.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ApiKeyServiceImp implements ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final UserService userService;

    /**
     * Constructor for ApiKeyServiceImp
     *
     * @param apiKeyRepository Repository for managing API keys
     * @param userService      Service for managing users
     */
    @Autowired
    public ApiKeyServiceImp(ApiKeyRepository apiKeyRepository, UserService userService) {
        this.apiKeyRepository = apiKeyRepository;
        this.userService = userService;
    }

    /**
     * Generates a new API key for a user.
     *
     * @param username The username for which to generate the API key
     * @return ApiKeyResponseDTO containing the status and the new API key
     * @throws UsernameNotFoundException if the user is not found
     */
    public ApiKeyResponseDTO generateApiKey(String username) {
        Optional<AppUser> user = userService.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        // Deactivate existing API keys for the user
        apiKeyRepository.deactivateApiKeys(user.get().getId());

        // Create new API key
        ApiKey newApiKey = new ApiKey();
        newApiKey.setKey(generateNewKey());
        newApiKey.setUser(user.get());
        newApiKey.setDeleted(false);

        // Save new API key to the repository
        ApiKey apiKeyObject = apiKeyRepository.save(newApiKey);

        // Return response with the new API key
        return new ApiKeyResponseDTO(Status.SUCCESS, ApiKeyResponseDTO.fromEntity(apiKeyObject));
    }

    /**
     * Finds an active API key.
     *
     * @param key The API key to search for
     * @return The ApiKey entity if found and active, null otherwise
     */
    public ApiKey findByKey(String key) {
        return apiKeyRepository.findByKeyAndDeletedFalse(key);
    }

    /**
     * Generates a new random API key.
     *
     * @return A new unique API key
     */
    public String generateNewKey() {
        return UUID.randomUUID().toString();
    }
}
