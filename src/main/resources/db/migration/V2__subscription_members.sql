CREATE TABLE subscription_schema.subscription_members (
 id UUID PRIMARY KEY,
 subscription_id UUID NOT NULL,
 worker_id UUID NOT NULL,
 status VARCHAR(2048) NOT NULL,
 created_at TIMESTAMP WITH TIME ZONE NOT NULL,
 version BIGINT NOT NULL DEFAULT 0
);
ALTER TABLE subscription_schema.subscription_members ADD CONSTRAINT unique_subscription_member UNIQUE(subscription_id,worker_id);
ALTER TABLE subscription_schema.subscription_members ADD CONSTRAINT fk_member_subscription FOREIGN KEY(subscription_id) REFERENCES subscription_schema.subscriptions(id);
CREATE INDEX idx_members_worker ON subscription_schema.subscription_members(worker_id);
