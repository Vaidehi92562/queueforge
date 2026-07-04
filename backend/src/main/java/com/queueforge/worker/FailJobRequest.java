package com.queueforge.worker;

import jakarta.validation.constraints.NotBlank;

public class FailJobRequest {

    @NotBlank(message = "Error message is required")
    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
