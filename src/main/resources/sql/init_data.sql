-- Initialize credit card onboarding rules data
-- Using fixed timestamp for initialization (H2 compatible)
INSERT INTO credit_card_onboarding_rules (criteria, mandatory_pass, score_contribution, score_type, score, created_time, updated_time, version, operator, status) VALUES
('Employment Verification', FALSE, 1.00, 0, 0.20, 1700000000000, 1700000000000, 0, 'system', 1),
('Compliance Check', FALSE, 1.00, 0, 0.20, 1700000000000, 1700000000000, 0, 'system', 1),
('Identity Verification', TRUE, 1.00, 0, 0.20, 1700000000000, 1700000000000, 0, 'system', 1),
('Risk Evaluation', FALSE, 0.2000, 1, 0.00, 1700000000000, 1700000000000, 0, 'system', 1),
('Behavioral Analysis', FALSE, 0.2000, 1, 0.00, 1700000000000, 1700000000000, 0, 'system', 1);
