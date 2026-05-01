-- Test data for edge cases
-- Note: This script may be executed multiple times due to different ApplicationContext configurations
-- (e.g., tests with @MockitoBean create separate contexts)

-- Clean up existing test data to ensure idempotency
DELETE FROM credit_card_onboarding WHERE emirates_id_number IN ('784-1990-1234567-1', '784-1990-1234567-2', '784-1990-1234567-3');

-- Normal case
INSERT INTO credit_card_onboarding (emirates_id_number, name, mobile_number, nationality, address, income, employment_details, requested_credit_limit, bank_statement, created_time, updated_time, operator) VALUES
('784-1990-1234567-1', 'John Doe', '+971501234567', 'UAE', 'Dubai Marina', 240000.00, 'Employed at Tech Corp', 50000.00, 'statement_normal.pdf', CAST(EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000 AS BIGINT), CAST(EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000 AS BIGINT), 'test_user');

-- Low income case (should fail risk evaluation)
INSERT INTO credit_card_onboarding (emirates_id_number, name, mobile_number, nationality, address, income, employment_details, requested_credit_limit, bank_statement, created_time, updated_time, operator) VALUES
('784-1990-1234567-2', 'Jane Smith', '+971501234568', 'UAE', 'Abu Dhabi', 60000.00, 'Employed at Retail Co', 10000.00, 'statement_low_income.pdf', CAST(EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000 AS BIGINT), CAST(EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000 AS BIGINT), 'test_user');

-- Unemployed case (should fail employment verification)
INSERT INTO credit_card_onboarding (emirates_id_number, name, mobile_number, nationality, address, income, employment_details, requested_credit_limit, bank_statement, created_time, updated_time, operator) VALUES
('784-1990-1234567-3', 'Bob Johnson', '+971501234569', 'UAE', 'Sharjah', 0.00, 'Unemployed', 5000.00, 'statement_unemployed.pdf', CAST(EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000 AS BIGINT), CAST(EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000 AS BIGINT), 'test_user');
