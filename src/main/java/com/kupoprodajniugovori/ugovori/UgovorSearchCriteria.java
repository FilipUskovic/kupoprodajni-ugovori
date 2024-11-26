package com.kupoprodajniugovori.ugovori;

import com.kupoprodajniugovori.utils.Status;

import java.time.LocalDate;

public record UgovorSearchCriteria(
        String kupac,
        Boolean active,
        LocalDate datumAkontacijeOd,
        LocalDate datumAkontacijeDo,
        Status status,
        String brojUgovora
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String kupac;
        private Boolean active;
        private LocalDate datumAkontacijeOd;
        private LocalDate datumAkontacijeDo;
        private Status status;
        private String brojUgovora;

        public Builder kupac(String kupac) {
            this.kupac = kupac;
            return this;
        }

        public Builder active(Boolean active) {
            this.active = active;
            return this;
        }

        public Builder datumAkontacijeOd(LocalDate datumAkontacijeOd) {
            this.datumAkontacijeOd = datumAkontacijeOd;
            return this;
        }

        public Builder datumAkontacijeDo(LocalDate datumAkontacijeDo) {
            this.datumAkontacijeDo = datumAkontacijeDo;
            return this;
        }

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public Builder brojUgovora(String brojUgovora) {
            this.brojUgovora = brojUgovora;
            return this;
        }

        public UgovorSearchCriteria build() {
            return new UgovorSearchCriteria(
                    kupac,
                    active,
                    datumAkontacijeOd,
                    datumAkontacijeDo,
                    status,
                    brojUgovora
            );
        }
    }
}
