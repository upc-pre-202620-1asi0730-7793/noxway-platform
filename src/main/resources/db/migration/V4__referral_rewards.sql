CREATE TABLE referral_rewards (
 id BINARY(16) PRIMARY KEY,
 worker_id BINARY(16) NOT NULL,
 referral_use_id BINARY(16) NOT NULL,
 days INTEGER NOT NULL,
 status VARCHAR(32) NOT NULL,
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0
);
ALTER TABLE referral_rewards ADD CONSTRAINT unique_referral_reward UNIQUE(worker_id,referral_use_id);
ALTER TABLE referral_rewards ADD CONSTRAINT fk_reward_referral FOREIGN KEY(referral_use_id) REFERENCES referral_uses(id);
