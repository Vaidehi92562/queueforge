package com.queueforge.worker;

public class CompleteJobRequest {

    private String resultMessage = "Job completed successfully";

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
}
