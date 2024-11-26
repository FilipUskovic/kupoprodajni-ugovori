package com.kupoprodajniugovori.ugovori;

import com.kupoprodajniugovori.utils.Status;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class UgovorSpecification {

    public static Specification<Ugovor> createSpecification(UgovorSearchCriteria criteria) {
        return Specification
                .where(notDeleted())
                .and(kupacLike(criteria.kupac()))
                .and(isActive(criteria.active()))
                .and(datumAkontacijeBetween(
                        criteria.datumAkontacijeOd(),
                        criteria.datumAkontacijeDo()))
                .and(statusEquals(criteria.status()))
                .and(brojUgovoraLike(criteria.brojUgovora()));
    }

    private static Specification<Ugovor> notDeleted() {
        return (root, query, cb) -> cb.equal(root.get("obrisan"), false);
    }

    private static Specification<Ugovor> kupacLike(String kupac) {
        return (root, query, cb) -> {
            if (StringUtils.isBlank(kupac)) return null;
            return cb.like(
                    cb.lower(root.get("kupac")),
                    "%" + kupac.toLowerCase() + "%"
            );
        };
    }

    private static Specification<Ugovor> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) return null;
            if (active) {
                return root.get("status").in(Status.KREIRANO, Status.NARUČENO);
            }
            return cb.equal(root.get("status"), Status.ISPORUČENO);
        };
    }

    private static Specification<Ugovor> datumAkontacijeBetween(
            LocalDate dateFrom, LocalDate dateTo) {
        return (root, query, cb) -> {
            if (dateFrom == null && dateTo == null) return null;

            if (dateFrom != null && dateTo != null) {
                return cb.between(root.get("datumAkontacije"), dateFrom, dateTo);
            }

            if (dateFrom != null) {
                return cb.greaterThanOrEqualTo(root.get("datumAkontacije"), dateFrom);
            }

            return cb.lessThanOrEqualTo(root.get("datumAkontacije"), dateTo);
        };
    }

    private static Specification<Ugovor> statusEquals(Status status) {
        return (root, query, cb) -> {
            if (status == null) return null;
            return cb.equal(root.get("status"), status);
        };
    }

    private static Specification<Ugovor> brojUgovoraLike(String brojUgovora) {
        return (root, query, cb) -> {
            if (StringUtils.isBlank(brojUgovora)) return null;
            return cb.like(
                    cb.lower(root.get("brojUgovora")),
                    "%" + brojUgovora.toLowerCase() + "%"
            );
        };
    }
}
