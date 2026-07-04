package com.queueforge.job;

import com.queueforge.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "job_logs")
public class JobLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "execution_id")
    private JobExecution execution;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "log_level", length = 20)
    private JobLogLevel logLevel;

    @Column(nullable = false, length = 3000)
    private String message;

    public JobLog() {
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public JobExecution getExecution() {
        return execution;
    }

    public void setExecution(JobExecution execution) {
        this.execution = execution;
    }

    public JobLogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(JobLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
