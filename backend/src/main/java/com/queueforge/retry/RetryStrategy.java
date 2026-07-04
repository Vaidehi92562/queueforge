package com.queueforge.retry;

public enum RetryStrategy {
    FIXED_DELAY,
    LINEAR_BACKOFF,
    EXPONENTIAL_BACKOFF
}
