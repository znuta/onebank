package com.onebank.onebank.auth.dto.output;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.onebank.onebank.auth.entity.AppUser;
import com.onebank.onebank.dto.enums.Status;
import com.onebank.onebank.dto.output.StandardResponseDTO;
import com.onebank.onebank.payment.entity.PaymentAccount;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAuthResponseDTO extends StandardResponseDTO {

    private String token;
    private Object data;
    private Long id;
    private String username;
    private List<PaymentAccount> paymentAccounts;

    public UserAuthResponseDTO(Status status) {
        super(status);
    }

    public UserAuthResponseDTO(Status status, Object data) {
        super(status);
        this.data = data;
    }
    public UserAuthResponseDTO(Status status, String token, Object data) {
        super(status);
        this.token = token;
        this.data = data;
    }
    public static UserAuthResponseDTO fromEntity(AppUser user) {
        UserAuthResponseDTO dto = new UserAuthResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        return dto;
    }



}


