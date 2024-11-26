package com.kupoprodajniugovori.artikli;

import com.kupoprodajniugovori.ugovori.Ugovor;
import com.kupoprodajniugovori.utils.Status;
import com.kupoprodajniugovori.utils.audit.Auditable;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "artikl", indexes = {
        @Index(name = "idx_artikl_status", columnList = "status")
})
//@Audited
//TODO dodao bi enverse za detaljnije pracanje promjena na bazi i revezije, te arhivske podatke
public class Artikl extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String naziv;

    private String dobavljac;

    private Integer kolicina;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ugovor_id")
    private Ugovor ugovor;


    public Artikl() {
    }

    public Artikl(Long id, String naziv, String dobavljac, Integer kolicina, Status status, Ugovor ugovor) {
        this.id = id;
        this.naziv = naziv;
        this.dobavljac = dobavljac;
        this.kolicina = kolicina;
        this.status = status;
        this.ugovor = ugovor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getDobavljac() {
        return dobavljac;
    }

    public void setDobavljac(String dobavljac) {
        this.dobavljac = dobavljac;
    }

    public Integer getKolicina() {
        return kolicina;
    }

    public void setKolicina(Integer kolicina) {
        this.kolicina = kolicina;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Ugovor getUgovor() {
        return ugovor;
    }

    public void setUgovor(Ugovor ugovor) {
        this.ugovor = ugovor;
    }

    @Override
    public String toString() {
        return "Artikl{" +
                "id=" + id +
                ", naziv='" + naziv + '\'' +
                ", dobavljac='" + dobavljac + '\'' +
                ", kolicina=" + kolicina +
                ", status=" + status +
                ", ugovor=" + ugovor +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artikl artikl = (Artikl) o;
        return Objects.equals(id, artikl.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
