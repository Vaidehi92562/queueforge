package com.queueforge.retry;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RetryPolicyService {

    private final RetryPolicyRepository retryPolicyRepository;

    public RetryPolicyService(RetryPolicyRepository retryPolicyRepository) {
        this.retryPolicyRepository = retryPolicyRepository;
    }

    @Transactional
    public RetryPolicyResponse createRetryPolicy(CreateRetryPolicyRequest request) {
        String normalizedName = request.getName().trim();

        if (retryPolicyRepository.existsByName(normalizedName)) {
            throw new IllegalArgumentException("Retry policy with this name already exists");
        }

        if (request.getMaxDelaySeconds() < request.getBaseDelaySeconds()) {
            throw new IllegalArgumentException("Max delay seconds must be greater than or equal to base delay seconds");
        }

        RetryPolicy retryPolicy = new RetryPolicy();
        retryPolicy.setName(normalizedName);
        retryPolicy.setStrategy(request.getStrategy());
        retryPolicy.setMaxAttempts(request.getMaxAttempts());
        retryPolicy.setBaseDelaySeconds(request.getBaseDelaySeconds());
        retryPolicy.setMaxDelaySeconds(request.getMaxDelaySeconds());

        RetryPolicy savedPolicy = retryPolicyRepository.save(retryPolicy);

        return toResponse(savedPolicy);
    }

    @Transactional(readOnly = true)
    public List<RetryPolicyResponse> getRetryPolicies() {
        return retryPolicyRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private RetryPolicyResponse toResponse(RetryPolicy retryPolicy) {
        return new RetryPolicyResponse(
                retryPolicy.getId(),
                retryPolicy.getName(),
                retryPolicy.getStrategy(),
                retryPolicy.getMaxAttempts(),
                retryPolicy.getBaseDelaySeconds(),
                retryPolicy.getMaxDelaySeconds(),
                retryPolicy.getCreatedAt(),
                retryPolicy.getUpdatedAt()
        );
    }
}
