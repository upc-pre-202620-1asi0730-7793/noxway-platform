CREATE TABLE subscription_members (
 id BINARY(16) PRIMARY KEY,
 subscription_id BINARY(16) NOT NULL,
 worker_id BINARY(16) NOT NULL,
 status VARCHAR(32) NOT NULL,
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0
);
ALTER TABLE subscription_members ADD CONSTRAINT unique_subscription_member UNIQUE(subscription_id,worker_id);
ALTER TABLE subscription_members ADD CONSTRAINT fk_member_subscription FOREIGN KEY(subscription_id) REFERENCES subscriptions(id);
CREATE INDEX idx_members_worker ON subscription_members(worker_id);
