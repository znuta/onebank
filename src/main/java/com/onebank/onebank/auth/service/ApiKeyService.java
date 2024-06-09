package com.onebank.onebank.auth.service;

import com.onebank.onebank.auth.dto.output.ApiKeyResponseDTO;
import com.onebank.onebank.auth.entity.ApiKey;

public interface ApiKeyService {
    ApiKeyResponseDTO generateApiKey(String username);

    ApiKey findByKey(String key);

    String generateNewKey();
}
