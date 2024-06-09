package com.onebank.onebank.auth.repository;

import com.onebank.onebank.auth.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    ApiKey findByKey(String key);
    @Query("SELECT a FROM ApiKey a WHERE a.key = :key AND a.deleted = false")
    ApiKey findByKeyAndDeletedFalse(String key);

    @Modifying
    @Transactional
    @Query("UPDATE ApiKey a SET a.deleted = true WHERE a.user.id = :userId")
    void deactivateApiKeys(Long userId);
}

