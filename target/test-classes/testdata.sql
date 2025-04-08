-- Indsæt billettyper
INSERT INTO ticket_types (type) VALUES
                                    ('Standard'),
                                    ('VIP'),
                                    ('Early Bird'),
                                    ('Student'),
                                    ('Gruppe');

-- Indsæt brugere med hashet passwords
INSERT INTO users (first_name, last_name, email, password, role, location, active) VALUES
                                                                                       ('Anders', 'Jensen', 'admin@easv.dk', 'password123', 1, 'Esbjerg', 1),
                                                                                       ('Mette', 'Nielsen', 'mette@easv.dk', 'password123', 2, 'Esbjerg', 1),
                                                                                       ('Lars', 'Pedersen', 'lars@easv.dk', 'password123', 2, 'Esbjerg', 1),
                                                                                       ('Sofie', 'Hansen', 'sofie@easv.dk', 'password123', 2, 'Sønderborg', 1),
                                                                                       ('Mikkel', 'Christensen', 'mikkel@easv.dk', 'password123', 2, 'Sønderborg', 0);

-- Indsæt begivenheder (events)
INSERT INTO events (title, description, date, starts_at, location, active) VALUES
                                                                               ('Forårskoncert 2025', 'Oplev skolens talenter ved den årlige forårskoncert.', '2025-05-15', '19:00', 'EASV Auditorium, Esbjerg', 1),
                                                                               ('IT-Karrieredag', 'Mød virksomheder og hør om karrieremuligheder inden for IT.', '2025-04-20', '10:00', 'EASV Multisal, Esbjerg', 1),
                                                                               ('Design Workshop', 'Praktisk workshop om moderne designprincipper.', '2025-06-10', '13:00', 'EASV Designlokale, Sønderborg', 1),
                                                                               ('Dimissionsfest 2025', 'Afslutningsfest for årets dimittender.', '2025-06-25', '17:00', 'Esbjerg Musikhus', 1),
                                                                               ('AI Seminar', 'Seminar om de seneste trends inden for kunstig intelligens.', '2025-05-05', '09:00', 'EASV Auditorium, Esbjerg', 1),
                                                                               ('Sommerfest 2025', 'Årets store sommerfest for alle studerende og ansatte.', '2025-07-01', '16:00', 'EASV Gården, Esbjerg', 1),
                                                                               ('Game Jam Weekend', 'Weekend med intensiv spiludvikling i teams.', '2025-09-15', '08:00', 'EASV IT-Lab, Esbjerg', 0);

-- Tilknyt koordinatorer til events
INSERT INTO events_coordinators (event_id, user_id) VALUES
                                                        (1, 2), -- Mette koordinerer Forårskoncert
                                                        (2, 2), -- Mette koordinerer også IT-Karrieredag
                                                        (3, 4), -- Sofie koordinerer Design Workshop
                                                        (4, 3), -- Lars koordinerer Dimissionsfest
                                                        (5, 3), -- Lars koordinerer AI Seminar
                                                        (6, 2), -- Mette koordinerer Sommerfest
                                                        (7, 4); -- Sofie koordinerer Game Jam Weekend (inaktiv event)

-- Indsæt billetter
INSERT INTO tickets (name, type) VALUES
                                     ('Standard Adgang', 1),
                                     ('VIP Adgang', 2),
                                     ('Early Bird', 3),
                                     ('Studerende', 4),
                                     ('Gruppe (min. 5 personer)', 5);

-- Tilknyt billetter til events med priser
INSERT INTO ticket_events (ticket_id, event_id, price) VALUES
-- Forårskoncert billetter
(1, 1, 50.00),  -- Standard
(4, 1, 25.00),  -- Studerende

-- IT-Karrieredag billetter
(1, 2, 0.00),   -- Gratis standard adgang
(3, 2, 0.00),   -- Gratis early bird

-- Design Workshop billetter
(1, 3, 150.00), -- Standard
(2, 3, 250.00), -- VIP
(4, 3, 75.00),  -- Studerende

-- Dimissionsfest billetter
(1, 4, 200.00), -- Standard
(2, 4, 350.00), -- VIP
(5, 4, 150.00), -- Gruppe

-- AI Seminar billetter
(1, 5, 300.00), -- Standard
(2, 5, 500.00), -- VIP
(3, 5, 200.00), -- Early Bird
(4, 5, 150.00), -- Studerende

-- Sommerfest billetter
(1, 6, 100.00), -- Standard
(5, 6, 75.00),  -- Gruppe

-- Game Jam Weekend billetter
(1, 7, 250.00), -- Standard
(4, 7, 150.00); -- Studerende

-- Indsæt købte billetter
INSERT INTO tickets_bought (ticket_event_id, customer_email, amount, total_price, uuid) VALUES
-- Forårskoncert køb
(1, 'peter@example.com', 2, 100.00, 'a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p6'),
(2, 'maria@student.dk', 1, 25.00, 'b2c3d4e5-f6g7-h8i9-j0k1-l2m3n4o5p6q7'),

-- IT-Karrieredag køb
(3, 'jakob@example.com', 1, 0.00, 'c3d4e5f6-g7h8-i9j0-k1l2-m3n4o5p6q7r8'),
(4, 'morten@company.dk', 5, 0.00, 'd4e5f6g7-h8i9-j0k1-l2m3-n4o5p6q7r8s9'),

-- Design Workshop køb
(5, 'julie@example.com', 1, 150.00, 'e5f6g7h8-i9j0-k1l2-m3n4-o5p6q7r8s9t0'),
(6, 'kasper@design.dk', 2, 500.00, 'f6g7h8i9-j0k1-l2m3-n4o5-p6q7r8s9t0u1'),
(7, 'laura@student.dk', 3, 225.00, 'g7h8i9j0-k1l2-m3n4-o5p6-q7r8s9t0u1v2'),

-- Dimissionsfest køb
(8, 'nikolaj@example.com', 2, 400.00, 'h8i9j0k1-l2m3-n4o5-p6q7-r8s9t0u1v2w3'),
(9, 'sofie@example.com', 1, 350.00, 'i9j0k1l2-m3n4-o5p6-q7r8-s9t0u1v2w3x4'),
(10, 'class2025@easv.dk', 10, 1500.00, 'j0k1l2m3-n4o5-p6q7-r8s9-t0u1v2w3x4y5');