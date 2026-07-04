package com.queueforge.metrics;

import com.queueforge.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    private final MetricsService metricsService;

    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping("/dashboard")
    public ApiResponse<DashboardMetricsResponse> getDashboardMetrics() {
        DashboardMetricsResponse response = metricsService.getDashboardMetrics();
        return ApiResponse.success("Dashboard metrics fetched successfully", response);
    }
}
