CREATE TABLE subscription_schema.referral_rewards (
 id UUID PRIMARY KEY,
 worker_id UUID NOT NULL,
 referral_use_id UUID NOT NULL,
 days INTEGER NOT NULL,
 status VARCHAR(2048) NOT NULL,
 created_at TIMESTAMP WITH TIME ZONE NOT NULL,
 version BIGINT NOT NULL DEFAULT 0
);
ALTER TABLE subscription_schema.referral_rewards ADD CONSTRAINT unique_referral_reward UNIQUE(worker_id,referral_use_id);
ALTER TABLE subscription_schema.referral_rewards ADD CONSTRAINT fk_reward_referral FOREIGN KEY(referral_use_id) REFERENCES subscription_schema.referral_uses(id);
