CREATE TABLE accounts (
 id BINARY(16) PRIMARY KEY,
 email VARCHAR(254) NOT NULL UNIQUE,
 full_name VARCHAR(150) NOT NULL,
 phone VARCHAR(30) NOT NULL,
 role VARCHAR(32) NOT NULL,
 shift_type VARCHAR(16),
 shift_start VARCHAR(5),
 shift_end VARCHAR(5),
 time_zone VARCHAR(100) NOT NULL,
 password_hash VARCHAR(72) NOT NULL,
 active BOOLEAN NOT NULL,
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE auth_sessions (
 id BINARY(16) PRIMARY KEY,
 account_id BINARY(16) NOT NULL,
 token_hash VARCHAR(64) NOT NULL UNIQUE,
 expires_at DATETIME(6) NOT NULL,
 active BOOLEAN NOT NULL,
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE trust_links (
 id BINARY(16) PRIMARY KEY,
 worker_id BINARY(16) NOT NULL,
 contact_id BINARY(16) NOT NULL,
 status VARCHAR(32) NOT NULL,
 accepted_at DATETIME(6),
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0,
 UNIQUE(worker_id,contact_id),
 CHECK(worker_id <> contact_id)
);

CREATE TABLE notification_preferences (
 id BINARY(16) PRIMARY KEY,
 account_id BINARY(16) NOT NULL,
 channel VARCHAR(16) NOT NULL,
 enabled BOOLEAN NOT NULL,
 priority_start VARCHAR(5) NOT NULL,
 priority_end VARCHAR(5) NOT NULL,
 time_zone VARCHAR(100) NOT NULL,
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0,
 UNIQUE(account_id,channel)
);

CREATE TABLE password_resets (
 id BINARY(16) PRIMARY KEY,
 account_id BINARY(16) NOT NULL,
 token_hash VARCHAR(64) NOT NULL UNIQUE,
 expires_at DATETIME(6) NOT NULL,
 used BOOLEAN NOT NULL,
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE commutes (
 id BINARY(16) PRIMARY KEY,
 worker_id BINARY(16) NOT NULL,
 origin VARCHAR(500) NOT NULL,
 destination VARCHAR(500) NOT NULL,
 transport VARCHAR(50) NOT NULL,
 start_time DATETIME(6) NOT NULL,
 estimated_arrival_time DATETIME(6) NOT NULL,
 tolerance_minutes INTEGER NOT NULL,
 status VARCHAR(32) NOT NULL,
 arrival_time DATETIME(6),
 delay_until DATETIME(6),
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0,
 CHECK(estimated_arrival_time > start_time),
 CHECK(tolerance_minutes BETWEEN 0 AND 120)
);

CREATE TABLE telemetry_pings (
 id BINARY(16) PRIMARY KEY,
 commute_id BINARY(16) NOT NULL,
 latitude DOUBLE PRECISION NOT NULL,
 longitude DOUBLE PRECISION NOT NULL,
 speed DOUBLE PRECISION NOT NULL,
 recorded_at DATETIME(6) NOT NULL,
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE safety_incidents (
 id BINARY(16) PRIMARY KEY,
 commute_id BINARY(16) NOT NULL,
 worker_id BINARY(16) NOT NULL,
 type VARCHAR(32) NOT NULL,
 description VARCHAR(1500) NOT NULL,
 status VARCHAR(32) NOT NULL,
 reported_at DATETIME(6) NOT NULL,
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE alerts (
 id BINARY(16) PRIMARY KEY,
 commute_id BINARY(16) NOT NULL,
 incident_id BINARY(16),
 contact_id BINARY(16) NOT NULL,
 channel VARCHAR(16) NOT NULL,
 status VARCHAR(32) NOT NULL,
 message VARCHAR(2048) NOT NULL,
 delivered_at DATETIME(6),
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE service_points (
 id BINARY(16) PRIMARY KEY,
 reported_by BINARY(16) NOT NULL,
 name VARCHAR(150) NOT NULL,
 type VARCHAR(32) NOT NULL,
 latitude DOUBLE PRECISION NOT NULL,
 longitude DOUBLE PRECISION NOT NULL,
 time_zone VARCHAR(100) NOT NULL,
 status VARCHAR(32) NOT NULL,
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE service_opening_intervals (
 id BINARY(16) PRIMARY KEY,
 service_id BINARY(16) NOT NULL,
 day_of_week INTEGER NOT NULL,
 opens_at VARCHAR(5) NOT NULL,
 closes_at VARCHAR(5) NOT NULL,
 closes_next_day BOOLEAN NOT NULL,
 is24_hours BOOLEAN NOT NULL,
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0,
 CHECK(day_of_week BETWEEN 1 AND 7)
);

CREATE TABLE risk_zones (
 id BINARY(16) PRIMARY KEY,
 reported_by BINARY(16) NOT NULL,
 description VARCHAR(1500) NOT NULL,
 risk_level VARCHAR(16) NOT NULL,
 latitude DOUBLE PRECISION NOT NULL,
 longitude DOUBLE PRECISION NOT NULL,
 status VARCHAR(32) NOT NULL,
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE route_ratings (
 id BINARY(16) PRIMARY KEY,
 commute_id BINARY(16) NOT NULL,
 worker_id BINARY(16) NOT NULL,
 score INTEGER NOT NULL,
 comments VARCHAR(1000) NOT NULL,
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0,
 UNIQUE(commute_id,worker_id),
 CHECK(score BETWEEN 1 AND 5)
);

CREATE TABLE community_posts (
 id BINARY(16) PRIMARY KEY,
 author_id BINARY(16) NOT NULL,
 title VARCHAR(150) NOT NULL,
 content VARCHAR(2000) NOT NULL,
 district VARCHAR(100) NOT NULL,
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE service_votes (
 id BINARY(16) PRIMARY KEY,
 service_id BINARY(16) NOT NULL,
 account_id BINARY(16) NOT NULL,
 direction INTEGER NOT NULL,
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0,
 UNIQUE(service_id,account_id),
 CHECK(direction IN (-1,1))
);

CREATE TABLE rest_sessions (
 id BINARY(16) PRIMARY KEY,
 worker_id BINARY(16) NOT NULL,
 start_time DATETIME(6) NOT NULL,
 end_time DATETIME(6) NOT NULL,
 quality INTEGER NOT NULL,
 notes VARCHAR(1000) NOT NULL,
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0,
 CHECK(end_time > start_time),
 CHECK(quality BETWEEN 1 AND 5)
);

CREATE TABLE sleep_goals (
 id BINARY(16) PRIMARY KEY,
 worker_id BINARY(16) NOT NULL,
 target_minutes INTEGER NOT NULL,
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0,
 UNIQUE(worker_id),
 CHECK(target_minutes BETWEEN 60 AND 1440)
);

CREATE TABLE plans (
 id BINARY(16) PRIMARY KEY,
 code VARCHAR(100) NOT NULL UNIQUE,
 name VARCHAR(150) NOT NULL,
 monthly_price NUMERIC(12,2) NOT NULL,
 max_contacts INTEGER NOT NULL,
 max_workers INTEGER NOT NULL,
 active BOOLEAN NOT NULL,
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE subscriptions (
 id BINARY(16) PRIMARY KEY,
 worker_id BINARY(16) NOT NULL,
 plan_id BINARY(16) NOT NULL,
 status VARCHAR(32) NOT NULL,
 starts_at DATETIME(6) NOT NULL,
 ends_at DATETIME(6) NOT NULL,
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE payment_transactions (
 id BINARY(16) PRIMARY KEY,
 subscription_id BINARY(16) NOT NULL,
 worker_id BINARY(16) NOT NULL,
 amount NUMERIC(12,2) NOT NULL,
 currency VARCHAR(3) NOT NULL,
 reference VARCHAR(150) NOT NULL,
 status VARCHAR(32) NOT NULL,
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE collective_benefits (
 id BINARY(16) PRIMARY KEY,
 name VARCHAR(150) NOT NULL,
 description VARCHAR(1500) NOT NULL,
 plan_code VARCHAR(32) NOT NULL,
 active BOOLEAN NOT NULL,
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE benefit_claims (
 id BINARY(16) PRIMARY KEY,
 worker_id BINARY(16) NOT NULL,
 benefit_id BINARY(16) NOT NULL,
 code VARCHAR(100) NOT NULL,
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0,
 UNIQUE(worker_id,benefit_id),
 UNIQUE(code)
);

CREATE TABLE referral_codes (
 id BINARY(16) PRIMARY KEY,
 worker_id BINARY(16) NOT NULL,
 code VARCHAR(100) NOT NULL UNIQUE,
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0,
 UNIQUE(worker_id)
);

CREATE TABLE referral_uses (
 id BINARY(16) PRIMARY KEY,
 referral_id BINARY(16) NOT NULL,
 referred_worker_id BINARY(16) NOT NULL,
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0,
 UNIQUE(referred_worker_id)
);

CREATE TABLE community_reports (
 id BINARY(16) PRIMARY KEY,
 reported_by BINARY(16) NOT NULL,
 target_type VARCHAR(32) NOT NULL,
 target_id BINARY(16) NOT NULL,
 reason VARCHAR(1000) NOT NULL,
 status VARCHAR(32) NOT NULL,
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE moderation_actions (
 id BINARY(16) PRIMARY KEY,
 report_id BINARY(16) NOT NULL,
 moderator_id BINARY(16) NOT NULL,
 decision VARCHAR(32) NOT NULL,
 notes VARCHAR(1000) NOT NULL,
 created_at DATETIME(6) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0
);

ALTER TABLE auth_sessions ADD CONSTRAINT fk_auth_session_account_id FOREIGN KEY(account_id) REFERENCES accounts(id);

ALTER TABLE trust_links ADD CONSTRAINT fk_trust_link_worker_id FOREIGN KEY(worker_id) REFERENCES accounts(id);

ALTER TABLE trust_links ADD CONSTRAINT fk_trust_link_contact_id FOREIGN KEY(contact_id) REFERENCES accounts(id);

ALTER TABLE notification_preferences ADD CONSTRAINT fk_notification_preference_account_id FOREIGN KEY(account_id) REFERENCES accounts(id);

ALTER TABLE password_resets ADD CONSTRAINT fk_password_reset_account_id FOREIGN KEY(account_id) REFERENCES accounts(id);

ALTER TABLE telemetry_pings ADD CONSTRAINT fk_telemetry_ping_commute_id FOREIGN KEY(commute_id) REFERENCES commutes(id);

ALTER TABLE safety_incidents ADD CONSTRAINT fk_safety_incident_commute_id FOREIGN KEY(commute_id) REFERENCES commutes(id);

ALTER TABLE alerts ADD CONSTRAINT fk_alert_commute_id FOREIGN KEY(commute_id) REFERENCES commutes(id);

ALTER TABLE alerts ADD CONSTRAINT fk_alert_incident_id FOREIGN KEY(incident_id) REFERENCES safety_incidents(id);

ALTER TABLE service_opening_intervals ADD CONSTRAINT fk_service_opening_interval_service_id FOREIGN KEY(service_id) REFERENCES service_points(id);

ALTER TABLE service_votes ADD CONSTRAINT fk_service_vote_service_id FOREIGN KEY(service_id) REFERENCES service_points(id);

ALTER TABLE subscriptions ADD CONSTRAINT fk_subscription_plan_id FOREIGN KEY(plan_id) REFERENCES plans(id);

ALTER TABLE payment_transactions ADD CONSTRAINT fk_payment_transaction_subscription_id FOREIGN KEY(subscription_id) REFERENCES subscriptions(id);

ALTER TABLE benefit_claims ADD CONSTRAINT fk_benefit_claim_benefit_id FOREIGN KEY(benefit_id) REFERENCES collective_benefits(id);

ALTER TABLE referral_uses ADD CONSTRAINT fk_referral_use_referral_id FOREIGN KEY(referral_id) REFERENCES referral_codes(id);

ALTER TABLE moderation_actions ADD CONSTRAINT fk_moderation_action_report_id FOREIGN KEY(report_id) REFERENCES community_reports(id);

CREATE INDEX idx_auth_session_account_id ON auth_sessions(account_id);

CREATE INDEX idx_trust_link_worker_id ON trust_links(worker_id);

CREATE INDEX idx_trust_link_contact_id ON trust_links(contact_id);

CREATE INDEX idx_notification_preference_account_id ON notification_preferences(account_id);

CREATE INDEX idx_password_reset_account_id ON password_resets(account_id);

CREATE INDEX idx_commute_worker_id ON commutes(worker_id);

CREATE INDEX idx_telemetry_ping_commute_id ON telemetry_pings(commute_id);

CREATE INDEX idx_safety_incident_commute_id ON safety_incidents(commute_id);

CREATE INDEX idx_safety_incident_worker_id ON safety_incidents(worker_id);

CREATE INDEX idx_alert_commute_id ON alerts(commute_id);

CREATE INDEX idx_alert_incident_id ON alerts(incident_id);

CREATE INDEX idx_alert_contact_id ON alerts(contact_id);

CREATE INDEX idx_service_point_reported_by ON service_points(reported_by);

CREATE INDEX idx_service_opening_interval_service_id ON service_opening_intervals(service_id);

CREATE INDEX idx_risk_zone_reported_by ON risk_zones(reported_by);

CREATE INDEX idx_route_rating_commute_id ON route_ratings(commute_id);

CREATE INDEX idx_route_rating_worker_id ON route_ratings(worker_id);

CREATE INDEX idx_community_post_author_id ON community_posts(author_id);

CREATE INDEX idx_service_vote_service_id ON service_votes(service_id);

CREATE INDEX idx_service_vote_account_id ON service_votes(account_id);

CREATE INDEX idx_rest_session_worker_id ON rest_sessions(worker_id);

CREATE INDEX idx_sleep_goal_worker_id ON sleep_goals(worker_id);

CREATE INDEX idx_subscription_worker_id ON subscriptions(worker_id);

CREATE INDEX idx_subscription_plan_id ON subscriptions(plan_id);

CREATE INDEX idx_payment_transaction_subscription_id ON payment_transactions(subscription_id);

CREATE INDEX idx_payment_transaction_worker_id ON payment_transactions(worker_id);

CREATE INDEX idx_benefit_claim_worker_id ON benefit_claims(worker_id);

CREATE INDEX idx_benefit_claim_benefit_id ON benefit_claims(benefit_id);

CREATE INDEX idx_referral_code_worker_id ON referral_codes(worker_id);

CREATE INDEX idx_referral_use_referral_id ON referral_uses(referral_id);

CREATE INDEX idx_referral_use_referred_worker_id ON referral_uses(referred_worker_id);

CREATE INDEX idx_community_report_reported_by ON community_reports(reported_by);

CREATE INDEX idx_community_report_target_id ON community_reports(target_id);

CREATE INDEX idx_moderation_action_report_id ON moderation_actions(report_id);

CREATE INDEX idx_moderation_action_moderator_id ON moderation_actions(moderator_id);
