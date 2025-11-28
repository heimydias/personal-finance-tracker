-- =====================================================
-- SEED DATA PARA TESTES - Personal Finance Tracker
-- Execute após as migrations principais
-- =====================================================

-- Desabilitar verificações de FK temporariamente
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- USUÁRIO
-- Email: admin@admin.com | Senha: admin123
-- =====================================================
INSERT INTO users (id, email, name, password, role, created_at, updated_at) VALUES
(UNHEX('49384217444C42DB899891D4461B92EC'), 'admin@admin.com', 'System Admin', '$2a$12$OIE79K0qQ/aMDYmLaDeQ6.KBQZm0icR936h3.hGH2NVStDtxq7M5i', 'ADMIN', NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- =====================================================
-- TRANSAÇÕES DE RECEITA (INCOME) - 15 registros
-- Total: R$ 115.150,00
-- =====================================================
INSERT INTO transactions (id, type, category, amount, transaction_date, description, user_id, created_by, transfer_type) VALUES
-- Junho 2025
(UNHEX('AFA993FDCD3B11F0A59A2EF125D91A51'), 'INCOME', 'Salário', 8500.00, '2025-06-05', 'Salário mensal', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
(UNHEX('AFA99594CD3B11F0A59A2EF125D91A51'), 'INCOME', 'Freelance', 1800.00, '2025-06-22', 'Projeto web', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
(UNHEX('132BCA53CD3D11F0A59A2EF125D91A51'), 'INCOME', 'Herança', 20000.00, '2025-06-10', 'Herança recebida', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
-- Julho 2025
(UNHEX('AFA99166CD3B11F0A59A2EF125D91A51'), 'INCOME', 'Salário', 8500.00, '2025-07-05', 'Salário mensal', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
(UNHEX('132BEE08CD3D11F0A59A2EF125D91A51'), 'INCOME', '13º Salário', 8500.00, '2025-07-15', '13º salário - 1ª parcela', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
-- Agosto 2025
(UNHEX('AFA93D0DCD3B11F0A59A2EF125D91A51'), 'INCOME', 'Salário', 8500.00, '2025-08-05', 'Salário mensal', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
(UNHEX('AFA93F08CD3B11F0A59A2EF125D91A51'), 'INCOME', 'Bônus', 3000.00, '2025-08-25', 'Bônus semestral', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
(UNHEX('132BF0E4CD3D11F0A59A2EF125D91A51'), 'INCOME', 'Venda', 15000.00, '2025-08-20', 'Venda de veículo usado', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
-- Setembro 2025
(UNHEX('AFA939E9CD3B11F0A59A2EF125D91A51'), 'INCOME', 'Salário', 8500.00, '2025-09-05', 'Salário mensal', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
(UNHEX('AFA93B12CD3B11F0A59A2EF125D91A51'), 'INCOME', 'Freelance', 2000.00, '2025-09-18', 'Consultoria', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
(UNHEX('132BF35DCD3D11F0A59A2EF125D91A51'), 'INCOME', 'PLR', 12000.00, '2025-09-20', 'Participação nos lucros', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
-- Outubro 2025
(UNHEX('AFA935D6CD3B11F0A59A2EF125D91A51'), 'INCOME', 'Salário', 8500.00, '2025-10-05', 'Salário mensal', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
(UNHEX('AFA93762CD3B11F0A59A2EF125D91A51'), 'INCOME', 'Dividendos', 350.00, '2025-10-20', 'Dividendos de ações', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
-- Novembro 2025
(UNHEX('AFA91117CD3B11F0A59A2EF125D91A51'), 'INCOME', 'Salário', 8500.00, '2025-11-05', 'Salário mensal', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
(UNHEX('AFA92DAFCD3B11F0A59A2EF125D91A51'), 'INCOME', 'Freelance', 1500.00, '2025-11-15', 'Projeto freelance', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL);

-- =====================================================
-- TRANSAÇÕES DE DESPESA (EXPENSE) - 21 registros
-- Total: R$ 27.380,00
-- =====================================================
INSERT INTO transactions (id, type, category, amount, transaction_date, description, user_id, created_by, transfer_type) VALUES
-- Junho 2025
(UNHEX('CDF38EE8CD3B11F0A59A2EF125D91A51'), 'EXPENSE', 'Aluguel', 2500.00, '2025-06-01', 'Aluguel mensal', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
(UNHEX('CDF394C1CD3B11F0A59A2EF125D91A51'), 'EXPENSE', 'Mercado', 1300.00, '2025-06-08', 'Compras do mês', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
(UNHEX('CDF39524CD3B11F0A59A2EF125D91A51'), 'EXPENSE', 'Manutenção', 650.00, '2025-06-20', 'Manutenção do carro', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
-- Julho 2025
(UNHEX('CDF38DD8CD3B11F0A59A2EF125D91A51'), 'EXPENSE', 'Aluguel', 2500.00, '2025-07-01', 'Aluguel mensal', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
(UNHEX('CDF38E2ECD3B11F0A59A2EF125D91A51'), 'EXPENSE', 'Mercado', 1150.00, '2025-07-06', 'Compras do mês', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
(UNHEX('CDF38E89CD3B11F0A59A2EF125D91A51'), 'EXPENSE', 'Saúde', 450.00, '2025-07-18', 'Farmácia', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
-- Agosto 2025
(UNHEX('CDF38C6ACD3B11F0A59A2EF125D91A51'), 'EXPENSE', 'Aluguel', 2500.00, '2025-08-01', 'Aluguel mensal', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
(UNHEX('CDF38D1CCD3B11F0A59A2EF125D91A51'), 'EXPENSE', 'Mercado', 1250.00, '2025-08-09', 'Compras do mês', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
(UNHEX('CDF38D7ECD3B11F0A59A2EF125D91A51'), 'EXPENSE', 'Viagem', 1800.00, '2025-08-15', 'Viagem fim de semana', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
-- Setembro 2025
(UNHEX('CDF389CFCD3B11F0A59A2EF125D91A51'), 'EXPENSE', 'Aluguel', 2500.00, '2025-09-01', 'Aluguel mensal', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
(UNHEX('CDF38A5ACD3B11F0A59A2EF125D91A51'), 'EXPENSE', 'Mercado', 1100.00, '2025-09-07', 'Compras do mês', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
(UNHEX('CDF38AC2CD3B11F0A59A2EF125D91A51'), 'EXPENSE', 'Educação', 500.00, '2025-09-10', 'Curso online', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
(UNHEX('CDF38B1BCD3B11F0A59A2EF125D91A51'), 'EXPENSE', 'Combustível', 400.00, '2025-09-20', 'Gasolina', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
-- Outubro 2025
(UNHEX('CDF3869DCD3B11F0A59A2EF125D91A51'), 'EXPENSE', 'Aluguel', 2500.00, '2025-10-01', 'Aluguel mensal', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
(UNHEX('CDF3871DCD3B11F0A59A2EF125D91A51'), 'EXPENSE', 'Mercado', 1350.00, '2025-10-08', 'Compras do mês', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
(UNHEX('CDF38798CD3B11F0A59A2EF125D91A51'), 'EXPENSE', 'Saúde', 280.00, '2025-10-14', 'Consulta médica', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
(UNHEX('CDF387F9CD3B11F0A59A2EF125D91A51'), 'EXPENSE', 'Lazer', 350.00, '2025-10-25', 'Cinema e restaurante', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
-- Novembro 2025
(UNHEX('CDF31DAECD3B11F0A59A2EF125D91A51'), 'EXPENSE', 'Aluguel', 2500.00, '2025-11-01', 'Aluguel mensal', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
(UNHEX('CDF37FA0CD3B11F0A59A2EF125D91A51'), 'EXPENSE', 'Mercado', 1200.00, '2025-11-10', 'Compras do mês', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
(UNHEX('CDF3811ECD3B11F0A59A2EF125D91A51'), 'EXPENSE', 'Combustível', 450.00, '2025-11-12', 'Gasolina', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL),
(UNHEX('CDF383E6CD3B11F0A59A2EF125D91A51'), 'EXPENSE', 'Internet', 150.00, '2025-11-15', 'Internet fibra', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', NULL);

-- =====================================================
-- TRANSAÇÕES DE TRANSFERÊNCIA PARA POUPANÇA (TRANSFER)
-- Total: R$ 60.000,00
-- =====================================================
INSERT INTO transactions (id, type, category, amount, transaction_date, description, user_id, created_by, transfer_type) VALUES
(UNHEX('EB0CCD15CD3C11F0A59A2EF125D91A51'), 'TRANSFER', 'POUPANÇA - Reserva de Emergência', 15000.00, '2025-06-01', 'Depósito em Poupança: Reserva de Emergência', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', 'DEPOSIT'),
(UNHEX('F3159EBACD3C11F0A59A2EF125D91A51'), 'TRANSFER', 'POUPANÇA - Aposentadoria', 25000.00, '2025-07-01', 'Depósito em Poupança: Aposentadoria', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', 'DEPOSIT'),
(UNHEX('F3B49A1CCD3C11F0A59A2EF125D91A51'), 'TRANSFER', 'POUPANÇA - Viagem Europa', 8000.00, '2025-08-15', 'Depósito em Poupança: Viagem Europa', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', 'DEPOSIT'),
(UNHEX('F42503EBCD3C11F0A59A2EF125D91A51'), 'TRANSFER', 'POUPANÇA - Investimentos', 12000.00, '2025-09-01', 'Depósito em Poupança: Investimentos', UNHEX('49384217444C42DB899891D4461B92EC'), 'system', 'DEPOSIT');

-- =====================================================
-- POUPANÇAS (SAVINGS) - 4 registros
-- Total: R$ 60.000,00
-- =====================================================
INSERT INTO savings (id, name, type, amount, interest_rate, description, user_id, deposit_transaction_id, transaction_date, created_by) VALUES
(UNHEX('DAED987BCD3B11F0A59A2EF125D91A51'), 'Reserva de Emergência', 'EMERGENCY_FUND', 15000.00, 1.00, 'Reserva para emergências - 6 meses de despesas', UNHEX('49384217444C42DB899891D4461B92EC'), UNHEX('EB0CCD15CD3C11F0A59A2EF125D91A51'), '2025-06-01', 'system'),
(UNHEX('DAEDAE95CD3B11F0A59A2EF125D91A51'), 'Aposentadoria', 'RETIREMENT', 25000.00, 0.80, 'Previdência privada', UNHEX('49384217444C42DB899891D4461B92EC'), UNHEX('F3159EBACD3C11F0A59A2EF125D91A51'), '2025-07-01', 'system'),
(UNHEX('DAEDB18FCD3B11F0A59A2EF125D91A51'), 'Viagem Europa', 'TRAVEL', 8000.00, 0.50, 'Juntando para viagem em 2026', UNHEX('49384217444C42DB899891D4461B92EC'), UNHEX('F3B49A1CCD3C11F0A59A2EF125D91A51'), '2025-08-15', 'system'),
(UNHEX('DAEDB24ACD3B11F0A59A2EF125D91A51'), 'Investimentos', 'INVESTMENT', 12000.00, 1.20, 'CDB e Tesouro Direto', UNHEX('49384217444C42DB899891D4461B92EC'), UNHEX('F42503EBCD3C11F0A59A2EF125D91A51'), '2025-09-01', 'system');

-- =====================================================
-- BALANCE (SALDO DO USUÁRIO)
-- =====================================================
INSERT INTO balance (id, user_id, total_income, total_expense, account_balance, savings_balance, total_balance, version) VALUES
(UNHEX('48D31EF3CD3D11F0A59A2EF125D91A51'), UNHEX('49384217444C42DB899891D4461B92EC'), 115150.00, 27380.00, 27770.00, 60000.00, 87770.00, 0)
ON DUPLICATE KEY UPDATE
  total_income = 115150.00,
  total_expense = 27380.00,
  account_balance = 27770.00,
  savings_balance = 60000.00,
  total_balance = 87770.00,
  updated_at = NOW();

-- Reabilitar verificações de FK
SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- RESUMO DOS DADOS
-- =====================================================
-- Usuário: admin@admin.com (senha: admin123)
--
-- INCOME:   15 transações = R$ 115.150,00
-- EXPENSE:  21 transações = R$  27.380,00
-- TRANSFER:  4 transações = R$  60.000,00 (depósitos em poupança)
--
-- POUPANÇAS (4):
--   - Reserva de Emergência: R$ 15.000 @ 1.00% a.m.
--   - Aposentadoria:         R$ 25.000 @ 0.80% a.m.
--   - Viagem Europa:         R$  8.000 @ 0.50% a.m.
--   - Investimentos:         R$ 12.000 @ 1.20% a.m.
--
-- BALANCE:
--   - Account Balance:  R$  27.770,00
--   - Savings Balance:  R$  60.000,00
--   - Total Balance:    R$  87.770,00
-- =====================================================