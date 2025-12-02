CREATE
    EXTENSION IF NOT EXISTS postgis;


CREATE SEQUENCE users_sequence
    START WITH 1
    INCREMENT BY 1
    NO CYCLE;

CREATE TABLE users
(
    id                     BIGINT PRIMARY KEY    DEFAULT nextval('users_sequence'),
    email                  VARCHAR(255) UNIQUE,
    phone_number           VARCHAR(20) UNIQUE,
    password_hash          TEXT         NOT NULL,
    username               VARCHAR(100) NOT NULL UNIQUE,
    first_name             VARCHAR(100),
    last_name              VARCHAR(100),
    role                   VARCHAR(20)  NOT NULL DEFAULT 'USER'
        CHECK ( role IN ('USER', 'MODERATOR', 'ADMIN') ),
    hide_number_in_profile BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at             TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ,
    status                 VARCHAR(20)  NOT NULL DEFAULT 'PENDING'
        CHECK ( status IN ('PENDING', 'ACTIVE', 'SUSPENDED'))
        CONSTRAINT unactive_user_when_email_or_phone_empty
            CHECK ( (status = 'PENDING') OR ((email IS NOT NULL) OR (phone_number IS NOT NULL)) )
);


CREATE SEQUENCE location_types_sequence
    START WITH 1
    INCREMENT BY 1
    NO CYCLE;

CREATE TABLE location_types
(
    id          BIGINT PRIMARY KEY DEFAULT nextval('location_types_sequence'),
    code        VARCHAR(50) UNIQUE NOT NULL,
    sms_code    VARCHAR(10) UNIQUE NOT NULL,
    name        VARCHAR(50) NOT NULL,
    description VARCHAR(255)
);


CREATE SEQUENCE aid_points_sequence
    START WITH 1
    INCREMENT BY 1
    NO CYCLE;


CREATE TABLE aid_points
(
    id               BIGINT PRIMARY KEY              DEFAULT nextval('aid_points_sequence'),
    name             VARCHAR(255)           NOT NULL,
    description      TEXT,
    location_type_id BIGINT                 NOT NULL REFERENCES location_types (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    location         GEOGRAPHY(Point, 4326) NOT NULL,
    created_by       BIGINT REFERENCES users (id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    status           VARCHAR(20)            NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    show_point       BOOLEAN                         DEFAULT FALSE,
    address          VARCHAR(255),
    created_at       TIMESTAMPTZ            NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ            NOT NULL DEFAULT now()
);


CREATE INDEX idx_aid_points_location
    ON aid_points USING GIST (location);


CREATE SEQUENCE aid_point_contacts_sequence
    START WITH 1
    INCREMENT BY 1
    NO CYCLE;


CREATE TABLE aid_point_contacts
(
    id           BIGINT PRIMARY KEY    DEFAULT nextval('aid_point_contacts_sequence'),
    aid_point_id BIGINT       NOT NULL REFERENCES aid_points (id) ON DELETE CASCADE,
    full_name    VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20)  NOT NULL,
    role         VARCHAR(100) NOT NULL DEFAULT 'creator',
    hide         BOOLEAN               DEFAULT FALSE,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT unique_contact_per_point UNIQUE (aid_point_id, phone_number)
);


CREATE SEQUENCE auth_tokens_sequence
    START WITH 1
    INCREMENT BY 1
    NO CYCLE;


CREATE TABLE auth_tokens
(
    id         BIGINT PRIMARY KEY DEFAULT nextval('auth_tokens_sequence'),
    user_id    BIGINT       NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    token      VARCHAR(255) NOT NULL,
    expires_at TIMESTAMPTZ,
    revoked    BOOLEAN
);


CREATE TYPE contact_method AS ENUM ('PHONE', 'EMAIL');


CREATE SEQUENCE verification_codes_sequence
    START WITH 1
    INCREMENT BY 1
    NO CYCLE;


CREATE TABLE verification_codes
(
    id          BIGINT PRIMARY KEY      DEFAULT nextval('verification_codes_sequence'),
    user_id     BIGINT         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    code        VARCHAR(10)    NOT NULL,
    type        contact_method NOT NULL,
    destination VARCHAR(255)   NOT NULL,
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT now(),
    expires_at  TIMESTAMPTZ    NOT NULL,
    used        BOOLEAN        NOT NULL DEFAULT FALSE
);


CREATE SEQUENCE service_types_sequence
    START WITH 1
    INCREMENT BY 1
    NO CYCLE;


CREATE TABLE service_types
(
    id          BIGINT PRIMARY KEY DEFAULT nextval('service_types_sequence'),
    code        VARCHAR(50) UNIQUE NOT NULL,
    sms_code    VARCHAR(10) UNIQUE NOT NULL,
    name        VARCHAR(50) NOT NULL,
    description VARCHAR(255)
);


CREATE SEQUENCE aid_point_services_sequence
    START WITH 1
    INCREMENT BY 1
    NO CYCLE;


CREATE TABLE aid_point_services
(
    id              BIGINT PRIMARY KEY DEFAULT nextval('aid_point_services_sequence'),
    aid_point_id    BIGINT REFERENCES aid_points (id),
    service_type_id BIGINT REFERENCES service_types (id)
);


CREATE TYPE sms_direction AS ENUM ('IN', 'OUT');

CREATE TYPE sms_status AS ENUM ('RECEIVED', 'PROCESSED', 'FAILED', 'SENT');


CREATE SEQUENCE sms_messages_sequence
    START WITH 1
    INCREMENT BY 1
    NO CYCLE;


CREATE TABLE sms_messages
(
    id           BIGINT PRIMARY KEY DEFAULT nextval('sms_messages_sequence'),
    direction    sms_direction NOT NULL,
    phone_number VARCHAR(20),
    message_text VARCHAR(255),
    status       sms_status,
    received_at  TIMESTAMPTZ,
    processed_at TIMESTAMPTZ
);


CREATE SEQUENCE moderation_logs_sequence
    START WITH 1
    INCREMENT BY 1
    NO CYCLE;

CREATE TABLE moderation_logs
(
    id               BIGINT PRIMARY KEY   DEFAULT nextval('moderation_logs_sequence'),
    moderator_id     BIGINT      NOT NULL REFERENCES users (id),
    moderator_action VARCHAR(20) NOT NULL
        CHECK ( moderator_action IN ('APPROVE', 'REJECT')),
    comment          TEXT,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);


CREATE SEQUENCE comments_sequence
    START WITH 1
    INCREMENT BY 1
    NO CYCLE;

CREATE TABLE comments
(
    id           BIGINT PRIMARY KEY   DEFAULT nextval('comments_sequence'),
    aid_point_id BIGINT      NOT NULL REFERENCES aid_points (id) ON DELETE CASCADE,
    user_id      BIGINT      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    text         TEXT        NOT NULL,
    rating       SMALLINT CHECK (rating >= 1 AND rating <= 5),
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);


CREATE SEQUENCE photos_sequence
    START WITH 1
    INCREMENT BY 1
    NO CYCLE;

CREATE TABLE photos
(
    id           BIGINT PRIMARY KEY   DEFAULT nextval('photos_sequence'),
    aid_point_id BIGINT REFERENCES aid_points (id) ON DELETE CASCADE,
    comment_id   BIGINT REFERENCES comments (id) ON DELETE CASCADE,
    image_data   BYTEA       NOT NULL,
    is_primary   BOOLEAN     NOT NULL DEFAULT FALSE,
    uploaded_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT not_null_aid_point_id_or_comment_id CHECK (
        (aid_point_id IS NOT NULL) OR (comment_id IS NOT NULL)
        )
);


CREATE SEQUENCE draft_photos_sequence
    START WITH 1
    INCREMENT BY 1
    NO CYCLE;

CREATE TABLE draft_photo
(
    id          BIGINT PRIMARY KEY   DEFAULT nextval('draft_photos_sequence'),
    uploaded_by BIGINT      NOT NULL REFERENCES users (id),
    image_data  BYTEA       NOT NULL,
    uploaded_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    expires_at  TIMESTAMPTZ NOT NULL
);

