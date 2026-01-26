--liquibase formatted sql
--changeset SpringSecurityJWT:1

CREATE TABLE roles (
id BIGSERIAL PRIMARY KEY,
role_name VARCHAR(255) NOT NULL UNIQUE
);


CREATE TABLE users (
id BIGSERIAL PRIMARY KEY,
username VARCHAR(255) NOT NULL UNIQUE,
email VARCHAR(255) NOT NULL UNIQUE,
password VARCHAR(255) NOT NULL
);


CREATE TABLE users_roles (
    user_fk BIGINT NOT NULL,
    role_fk BIGINT NOT NULL,
    PRIMARY KEY (user_fk, role_fk),
    CONSTRAINT fk_user FOREIGN KEY (user_fk) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_fk) REFERENCES roles(id) ON DELETE CASCADE
);
