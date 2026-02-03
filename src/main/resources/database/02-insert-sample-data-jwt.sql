--liquibase formatted sql
--changeset SpringSecurityJWT:2

INSERT INTO users (username, email, password) VALUES
('admin', 'admin@mail.com', '$2a$10$ENNheeHAMnpufOMeplZB9ewHCHg2NZEDmXPlj9mRIzaCoh0EBGFtC');
-- hasło: Admin123!

INSERT INTO users_roles (user_fk, role) VALUES
(1, 'ROLE_ADMIN');
