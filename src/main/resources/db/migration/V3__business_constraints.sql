ALTER TABLE subscription_schema.payment_transactions ADD CONSTRAINT unique_payment_reference UNIQUE(reference);
ALTER TABLE users_network_schema.accounts ADD CONSTRAINT valid_account_role CHECK(role IN ('WORKER','TRUSTED_CONTACT','MODERATOR','ADMIN'));
ALTER TABLE users_network_schema.accounts ADD CONSTRAINT valid_worker_shift CHECK(role <> 'WORKER' OR (shift_type IS NOT NULL AND shift_type IN ('DAY','NIGHT','ROTATING')));
ALTER TABLE community_data_schema.service_points ADD CONSTRAINT valid_service_lat CHECK(latitude BETWEEN -90 AND 90);
ALTER TABLE community_data_schema.service_points ADD CONSTRAINT valid_service_lon CHECK(longitude BETWEEN -180 AND 180);
