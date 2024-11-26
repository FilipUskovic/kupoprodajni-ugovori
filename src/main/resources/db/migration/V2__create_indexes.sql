CREATE INDEX IF NOT EXISTS idx_ugovor_broj ON ugovor(broj_ugovora);
CREATE INDEX IF NOT EXISTS idx_ugovor_status ON ugovor(status);
CREATE INDEX IF NOT EXISTS idx_ugovor_obrisan ON ugovor(obrisan);
CREATE INDEX IF NOT EXISTS idx_artikl_status ON artikl(status);
CREATE INDEX IF NOT EXISTS idx_artikl_ugovor ON artikl(ugovor_id);

