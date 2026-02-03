--liquibase formatted sql
--changeset SpringSecurityJWT:1


CREATE TABLE users (
id BIGSERIAL PRIMARY KEY,
username VARCHAR(255) NOT NULL UNIQUE,
email VARCHAR(255) NOT NULL UNIQUE,
password VARCHAR(255) NOT NULL
);


CREATE TABLE users_roles (
    user_fk BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,

    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_fk) REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (user_fk, role)
);
