CREATE TABLE users_roles (
                             id INT IDENTITY(1,1) PRIMARY KEY,
                             name NVARCHAR(255)
);

CREATE TABLE ticket_types (
                              id INT IDENTITY(1,1) PRIMARY KEY,
                              type NVARCHAR(255)
);

CREATE TABLE events (
                        id INT IDENTITY(1,1) PRIMARY KEY,
                        title NVARCHAR(255),
                        description NVARCHAR(255),
                        date DATETIME,
                        starts_at NVARCHAR(255),
                        location NVARCHAR(255),
                        active BIT DEFAULT 1
);

CREATE TABLE users (
                       id INT IDENTITY(1,1) PRIMARY KEY,
                       first_name NVARCHAR(255),
                       last_name NVARCHAR(255),
                       email NVARCHAR(255),
                       password NVARCHAR(255),
                       role INT,
                       location NVARCHAR(255),
                       active BIT DEFAULT 1,
                       CONSTRAINT FK_role FOREIGN KEY (role) REFERENCES users_roles(id)
);

CREATE TABLE tickets (
                         id INT IDENTITY(1,1) PRIMARY KEY,
                         name NVARCHAR(255),
                         type INT,
                         CONSTRAINT FK_type FOREIGN KEY (type) REFERENCES ticket_types(id)
);

CREATE TABLE ticket_events (
                               id INT IDENTITY(1,1) PRIMARY KEY,
                               ticket_id INT,
                               event_id INT,
                               price FLOAT,
                               CONSTRAINT FK_ticket_id FOREIGN KEY (ticket_id) REFERENCES tickets(id),
                               CONSTRAINT FK_ticket_events_event_id FOREIGN KEY (event_id) REFERENCES events(id)
);

CREATE TABLE tickets_bought (
                                ticket_event_id INT,
                                customer_email NVARCHAR(255),
                                amount INT,
                                total_price FLOAT,
                                uuid NVARCHAR(150),
                                CONSTRAINT FK_ticket_event_id FOREIGN KEY (ticket_event_id) REFERENCES ticket_events(id)
);

CREATE TABLE events_coordinators (
                                     event_id INT,
                                     user_id INT,
                                     CONSTRAINT FK_event_id FOREIGN KEY (event_id) REFERENCES events(id),
                                     CONSTRAINT FK_user_id FOREIGN KEY (user_id) REFERENCES users(id)
);

INSERT INTO users_roles (name) VALUES ('Administrator'), ('Koordinator');