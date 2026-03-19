-- Seed watchlist with popular NSE stocks
MERGE INTO watchlist (symbol, company_name, sector, added_at) KEY (symbol) VALUES ('RELIANCE', 'Reliance Industries Ltd', 'Energy', CURRENT_TIMESTAMP);
MERGE INTO watchlist (symbol, company_name, sector, added_at) KEY (symbol) VALUES ('TCS', 'Tata Consultancy Services', 'IT', CURRENT_TIMESTAMP);
MERGE INTO watchlist (symbol, company_name, sector, added_at) KEY (symbol) VALUES ('HDFCBANK', 'HDFC Bank Ltd', 'Bank', CURRENT_TIMESTAMP);
MERGE INTO watchlist (symbol, company_name, sector, added_at) KEY (symbol) VALUES ('INFY', 'Infosys Ltd', 'IT', CURRENT_TIMESTAMP);
MERGE INTO watchlist (symbol, company_name, sector, added_at) KEY (symbol) VALUES ('ICICIBANK', 'ICICI Bank Ltd', 'Bank', CURRENT_TIMESTAMP);
