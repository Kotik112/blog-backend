CREATE TYPE contact_status AS ENUM ('PENDING', 'RESOLVED', 'IGNORED');

CREATE TABLE contact_us_tbl (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    status contact_status NOT NULL DEFAULT 'PENDING',
    user_agent TEXT,
    internal_note TEXT
);