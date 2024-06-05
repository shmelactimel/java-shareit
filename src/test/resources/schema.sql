CREATE TABLE IF NOT EXISTS users (
    id bigint generated by default as identity PRIMARY KEY,
    name varchar(50),
    email varchar(300) NOT NULL,
    CONSTRAINT UNIQUE_EMAIL UNIQUE (email)
    );

CREATE TABLE IF NOT EXISTS requests (
    id bigint generated by default as identity PRIMARY KEY,
    user_id bigint NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    description varchar(1000) NOT NULL,
    created timestamp NOT NULL
    );

CREATE TABLE IF NOT EXISTS items (
    id bigint generated by default as identity PRIMARY KEY,
    user_id bigint NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    request_id bigint REFERENCES requests(id) ON DELETE SET NULL,
    name varchar(100) NOT NULL,
    description varchar(1000) NOT NULL,
    available bool NOT NULL
    );

CREATE TABLE IF NOT EXISTS booking (
    id bigint generated by default as identity PRIMARY KEY,
    booker_id bigint NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    item_id bigint NOT NULL REFERENCES items(id) ON DELETE CASCADE,
    status varchar(50) NOT NULL,
    start_booking timestamp NOT NULL,
    end_booking timestamp NOT NULL
    );

CREATE TABLE IF NOT EXISTS comments (
    id bigint generated by default as identity PRIMARY KEY,
    author_id bigint NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    item_id bigint NOT NULL REFERENCES items(id) ON DELETE CASCADE,
    text varchar(1000) NOT NULL,
    created timestamp NOT NULL
    );