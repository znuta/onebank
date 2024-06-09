package com.onebank.onebank.dto.input;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiFieldError {

    private String field;
    private String message;
    private Object rejectedValue;
}
