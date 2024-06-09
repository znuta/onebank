package com.onebank.onebank.dto.output;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.onebank.onebank.dto.enums.Status;
import com.onebank.onebank.dto.input.ApiFieldError;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class StandardResponseDTO implements Serializable {


    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Status status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<ApiFieldError> errors;

    public StandardResponseDTO() {
    }

    public List<ApiFieldError> getErrors() {
        return errors;
    }

    public void setErrors(List<ApiFieldError> errors) {
        this.errors = errors;
    }

    public StandardResponseDTO(Status status) {
        this.status = status;
    }

    public StandardResponseDTO(Status status, List<ApiFieldError> errors) {
        this.status = status;
        this.errors = errors;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
