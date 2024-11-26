CREATE TABLE IF NOT EXISTS ugovor (
                                      id BIGSERIAL PRIMARY KEY,
                                      kupac VARCHAR(255) NOT NULL,
                                      broj_ugovora VARCHAR(255) NOT NULL UNIQUE,
                                      datum_akontacije DATE NOT NULL,
                                      rok_isporuke DATE NOT NULL,
                                      status VARCHAR(50) NOT NULL,
                                      obrisan BOOLEAN DEFAULT FALSE,
                                      created_by VARCHAR(255) NOT NULL,
                                      created_date TIMESTAMP NOT NULL,
                                      last_modified_by VARCHAR(255),
                                      last_modified_date TIMESTAMP,
                                      version BIGINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE ugovor IS 'Tabliva za spremanje kupoprodajni ugovora';


CREATE TABLE IF NOT EXISTS artikl (
                                      id BIGSERIAL PRIMARY KEY,
                                      naziv VARCHAR(255) NOT NULL,
                                      dobavljac VARCHAR(255) NOT NULL,
                                      kolicina INTEGER NOT NULL,
                                      status VARCHAR(50) NOT NULL,
                                      ugovor_id BIGINT REFERENCES ugovor(id),
                                      created_by VARCHAR(255) NOT NULL,
                                      created_date TIMESTAMP NOT NULL,
                                      last_modified_by VARCHAR(255),
                                      last_modified_date TIMESTAMP,
                                      version BIGINT NOT NULL DEFAULT 0
);


COMMENT ON TABLE artikl IS 'Tabliva za spremanje artikl-a ugovora';
