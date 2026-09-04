-- Seed Initial Events for NeonPulse Ticketing
INSERT INTO events (id, name, base_price, total_capacity, available_seats, status)
VALUES 
    ('EVT-001', 'Neon Cyberpunk Electronic Festival 2026', 45000.0, 50, 32, 'ACTIVE'),
    ('EVT-002', 'Rock Alternativo: Chilean Indie Summit', 32000.0, 40, 5, 'ACTIVE'),
    ('EVT-003', 'Global Tech & Cloud Dev Conference', 85000.0, 20, 0, 'SOLD_OUT'),
    ('EVT-004', 'Sinfonía Anime & Videojuegos en Vivo', 28000.0, 60, 48, 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

-- Seed Default Customer
INSERT INTO customers (id, name, email, tier)
VALUES 
    ('CUST-001', 'Alan Turing', 'alan.turing@enigma.org', 'VIP'),
    ('CUST-002', 'Ada Lovelace', 'ada.lovelace@analytical.org', 'PREMIUM'),
    ('CUST-003', 'Grace Hopper', 'grace.hopper@navy.mil', 'REGULAR')
ON CONFLICT (id) DO NOTHING;
