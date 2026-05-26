CREATE TABLE IF NOT EXISTS news (
    id BIGSERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    summary      TEXT,
    content      TEXT,
    image_url    VARCHAR(500),
    published_at TIMESTAMP,
    author       VARCHAR(255),
    status       VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS testimonials (
    id BIGSERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    photo_url     VARCHAR(500) NOT NULL,
    instagram_url VARCHAR(500),
    facebook_url  VARCHAR(500),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS contact_requests (
    id BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    phone      VARCHAR(50)  NOT NULL,
    purpose    VARCHAR(30)  NOT NULL,
    status     VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);
