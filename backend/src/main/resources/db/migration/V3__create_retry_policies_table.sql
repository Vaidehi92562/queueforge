CREATE TABLE retry_policies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(120) NOT NULL UNIQUE,
    strategy VARCHAR(40) NOT NULL,
    max_attempts INTEGER NOT NULL,
    base_delay_seconds INTEGER NOT NULL,
    max_delay_seconds INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_retry_max_attempts
        CHECK (max_attempts >= 1),

    CONSTRAINT chk_retry_base_delay
        CHECK (base_delay_seconds >= 0),

    CONSTRAINT chk_retry_max_delay
        CHECK (max_delay_seconds >= base_delay_seconds)
);

CREATE INDEX idx_retry_policies_strategy ON retry_policies(strategy);
