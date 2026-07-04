package com.queueforge.queue;

import com.queueforge.common.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/queues")
public class QueueController {

    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<QueueResponse>> createQueue(
            @Valid @RequestBody CreateQueueRequest request
    ) {
        QueueResponse response = queueService.createQueue(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Queue created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<QueueResponse>>> getQueuesByProject(
            @RequestParam @NotNull UUID projectId
    ) {
        List<QueueResponse> queues = queueService.getQueuesByProject(projectId);

        return ResponseEntity
                .ok(ApiResponse.success("Queues fetched successfully", queues));
    }

    @PatchMapping("/{queueId}/pause")
    public ResponseEntity<ApiResponse<QueueResponse>> pauseQueue(
            @PathVariable UUID queueId
    ) {
        QueueResponse response = queueService.pauseQueue(queueId);

        return ResponseEntity
                .ok(ApiResponse.success("Queue paused successfully", response));
    }

    @PatchMapping("/{queueId}/resume")
    public ResponseEntity<ApiResponse<QueueResponse>> resumeQueue(
            @PathVariable UUID queueId
    ) {
        QueueResponse response = queueService.resumeQueue(queueId);

        return ResponseEntity
                .ok(ApiResponse.success("Queue resumed successfully", response));
    }

    @GetMapping("/{queueId}/stats")
    public ResponseEntity<ApiResponse<QueueStatsResponse>> getQueueStats(
            @PathVariable UUID queueId
    ) {
        QueueStatsResponse response = queueService.getQueueStats(queueId);

        return ResponseEntity
                .ok(ApiResponse.success("Queue statistics fetched successfully", response));
    }
}
