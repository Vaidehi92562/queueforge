package com.queueforge.scheduler;

import com.queueforge.common.BaseEntity;
import com.queueforge.project.Project;
import com.queueforge.queue.JobQueue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

@Entity
@Table(name = "scheduled_jobs")
public class ScheduledJob extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "queue_id", nullable = false)
    private JobQueue queue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, length = 140)
    private String name;

    @Column(nullable = false, name = "cron_expression", length = 120)
    private String cronExpression;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, name = "payload_template", columnDefinition = "jsonb")
    private Map<String, Object> payloadTemplate;

    @Column(nullable = false, name = "is_active")
    private boolean active = true;

    @Column(name = "next_run_at")
    private Instant nextRunAt;

    @Column(name = "last_run_at")
    private Instant lastRunAt;

    public ScheduledJob() {
    }

    public JobQueue getQueue() {
        return queue;
    }

    public void setQueue(JobQueue queue) {
        this.queue = queue;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Map<String, Object> getPayloadTemplate() {
        return payloadTemplate;
    }

    public void setPayloadTemplate(Map<String, Object> payloadTemplate) {
        this.payloadTemplate = payloadTemplate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getNextRunAt() {
        return nextRunAt;
    }

    public void setNextRunAt(Instant nextRunAt) {
        this.nextRunAt = nextRunAt;
    }

    public Instant getLastRunAt() {
        return lastRunAt;
    }

    public void setLastRunAt(Instant lastRunAt) {
        this.lastRunAt = lastRunAt;
    }
}
