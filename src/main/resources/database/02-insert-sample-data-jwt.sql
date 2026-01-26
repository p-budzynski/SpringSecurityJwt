--liquibase formatted sql
--changeset SpringSecurityJWT:2

INSERT INTO roles (role_name) VALUES
('ROLE_USER'),
('ROLE_MANAGER'),
('ROLE_ADMIN');


INSERT INTO users (username, email, password) VALUES
('admin', 'admin@mail.com', '$2a$10$Ly8asZel10larfXaonDCl.3LahMsGkHDrdeU6/7hn1Ax641xVHJDC');


INSERT INTO users_roles (user_fk, role_fk) VALUES
(1, 3);
