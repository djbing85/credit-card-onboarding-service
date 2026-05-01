-- Create credit card onboarding table
CREATE TABLE IF NOT EXISTS credit_card_onboarding (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    emirates_id_number VARCHAR(64) NOT NULL DEFAULT '',
    name VARCHAR(128) NOT NULL DEFAULT '',
    mobile_number VARCHAR(32) NOT NULL DEFAULT '',
    nationality VARCHAR(64) NOT NULL DEFAULT '',
    address VARCHAR(512) NOT NULL DEFAULT '',
    income DECIMAL(16,2) NOT NULL DEFAULT 0.00,
    employment_details VARCHAR(512) NOT NULL DEFAULT '',
    requested_credit_limit DECIMAL(16,2) NOT NULL DEFAULT 0.00,
    bank_statement VARCHAR(256) NOT NULL DEFAULT '',
    verified_result VARCHAR(20) NOT NULL DEFAULT '',
    verified_score VARCHAR(32) NOT NULL DEFAULT '',
    verified_detail VARCHAR(1024) NOT NULL DEFAULT '',
    verified_time BIGINT NOT NULL DEFAULT 0,
    created_time BIGINT NOT NULL DEFAULT 0,
    updated_time BIGINT NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    operator VARCHAR(32) NOT NULL DEFAULT ''
);

-- Add table and column comments
COMMENT ON TABLE credit_card_onboarding IS 'Credit Card Onboarding Application Table';
COMMENT ON COLUMN credit_card_onboarding.id IS 'Auto-increment ID';
COMMENT ON COLUMN credit_card_onboarding.emirates_id_number IS 'Unique national identification number';
COMMENT ON COLUMN credit_card_onboarding.name IS 'Full name of the applicant';
COMMENT ON COLUMN credit_card_onboarding.mobile_number IS 'Primary mobile number of the applicant';
COMMENT ON COLUMN credit_card_onboarding.nationality IS 'Nationality of the applicant';
COMMENT ON COLUMN credit_card_onboarding.address IS 'Residential address';
COMMENT ON COLUMN credit_card_onboarding.income IS 'Annual income';
COMMENT ON COLUMN credit_card_onboarding.employment_details IS 'Current employment status and employer';
COMMENT ON COLUMN credit_card_onboarding.requested_credit_limit IS 'Desired credit limit by the applicant';
COMMENT ON COLUMN credit_card_onboarding.bank_statement IS 'Last 6 months bank statement of the applicant';
COMMENT ON COLUMN credit_card_onboarding.verified_result IS 'Verified result: true/false';
COMMENT ON COLUMN credit_card_onboarding.verified_score IS 'Verified score: 0.0000 ~ 1.0000';
COMMENT ON COLUMN credit_card_onboarding.verified_detail IS 'Verified detail';
COMMENT ON COLUMN credit_card_onboarding.verified_time IS 'Verified time';
COMMENT ON COLUMN credit_card_onboarding.created_time IS 'Creation time';
COMMENT ON COLUMN credit_card_onboarding.updated_time IS 'Update time';
COMMENT ON COLUMN credit_card_onboarding.version IS 'Version number';
COMMENT ON COLUMN credit_card_onboarding.status IS 'Status: 0 disabled, 1 enabled, 9 deleted';
COMMENT ON COLUMN credit_card_onboarding.operator IS 'Operator';

-- Create credit card onboarding rules table
CREATE TABLE IF NOT EXISTS credit_card_onboarding_rules (
    id INT AUTO_INCREMENT PRIMARY KEY,
    criteria VARCHAR(64) NOT NULL DEFAULT '',
    mandatory_pass BOOLEAN NOT NULL DEFAULT FALSE,
    score_contribution DECIMAL(5,4) NOT NULL DEFAULT 0.0000,
    score_type SMALLINT NOT NULL DEFAULT 0,
    score DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    created_time BIGINT NOT NULL DEFAULT 0,
    updated_time BIGINT NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    operator VARCHAR(32) NOT NULL DEFAULT ''
);

-- Add table and column comments
COMMENT ON TABLE credit_card_onboarding_rules IS 'Credit Card Onboarding Rules Table';
COMMENT ON COLUMN credit_card_onboarding_rules.id IS 'Auto-increment ID';
COMMENT ON COLUMN credit_card_onboarding_rules.criteria IS 'Evaluation criteria';
COMMENT ON COLUMN credit_card_onboarding_rules.mandatory_pass IS 'Mandatory pass requirement';
COMMENT ON COLUMN credit_card_onboarding_rules.score_contribution IS 'Score contribution weight';
COMMENT ON COLUMN credit_card_onboarding_rules.score_type IS 'Score type: 0 boolean, 1 decimal';
COMMENT ON COLUMN credit_card_onboarding_rules.score IS 'Score value';
COMMENT ON COLUMN credit_card_onboarding_rules.created_time IS 'Creation time';
COMMENT ON COLUMN credit_card_onboarding_rules.updated_time IS 'Update time';
COMMENT ON COLUMN credit_card_onboarding_rules.version IS 'Version number';
COMMENT ON COLUMN credit_card_onboarding_rules.status IS 'Status: 0 disabled, 1 enabled, 9 deleted';
COMMENT ON COLUMN credit_card_onboarding_rules.operator IS 'Operator';
