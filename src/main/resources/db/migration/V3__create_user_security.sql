CREATE TABLE korisnici (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE korisnici_role (
    korisnik_id BIGINT NOT NULL REFERENCES korisnici(id) ON DELETE CASCADE,
    roles VARCHAR(50) NOT NULL
);

-- Index za brže pretraživanje po username-u tijekom autentifikacije
CREATE INDEX idx_korisnici_username ON korisnici(username);