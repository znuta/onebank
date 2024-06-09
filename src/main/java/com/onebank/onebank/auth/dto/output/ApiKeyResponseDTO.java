package com.onebank.onebank.auth.dto.output;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.onebank.onebank.auth.entity.ApiKey;
import com.onebank.onebank.dto.enums.Status;
import com.onebank.onebank.dto.output.StandardResponseDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiKeyResponseDTO extends StandardResponseDTO {

    private String token;
    private Object data;
    private Long id;
    private String key;
    private UserAuthResponseDTO user;
    public ApiKeyResponseDTO(Status status) {
        super(status);
    }
    public ApiKeyResponseDTO(Status status, Object data) {
        super(status);
        this.data = data;
    }
    public static ApiKeyResponseDTO fromEntity(ApiKey apiKey) {
        ApiKeyResponseDTO dto = new ApiKeyResponseDTO();
        dto.setId(apiKey.getId());
        dto.setKey(apiKey.getKey());
        dto.setUser(UserAuthResponseDTO.fromEntity(apiKey.getUser())); // Convert User entity to UserResponseDTO
        return dto;
    }

}
