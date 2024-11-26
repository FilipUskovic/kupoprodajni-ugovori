package com.kupoprodajniugovori.ugovori;

import com.kupoprodajniugovori.artikli.Artikl;
import com.kupoprodajniugovori.utils.audit.Auditable;
import com.kupoprodajniugovori.utils.Status;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "ugovor", indexes = {
        @Index(name = "idx_ugovor_broj", columnList = "brojUgovora"),
        @Index(name = "idx_ugovor_status", columnList = "status"),
        @Index(name = "idx_ugovor_obrisan", columnList = "obrisan")
})
//@Audited
//TODO dodao bi enverse za detaljnije pracanje promjena na bazi i revezije, te arhivske podatke
public class Ugovor extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String kupac;

    @Column(unique = true, nullable = false)
    private String brojUgovora;

    @Column(nullable = false)
    private LocalDate datumAkontacije;

    @Column(nullable = false)
    private LocalDate rokIsporuke;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    //safe delete
    boolean obrisan;

    @OneToMany(mappedBy = "ugovor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Artikl> artikli = new HashSet<>();

    // pomocne metode za upravljanje bidirekcionalnim vezema
    public void addArtikl(Artikl artikl) {
        artikli.add(artikl);
        artikl.setUgovor(this);
    }

    public void removeArtikl(Artikl artikl) {
        artikli.remove(artikl);
        artikl.setUgovor(null);
    }

    public Ugovor() {
    }

    public Ugovor(Long id, String kupac, String brojUgovora, LocalDate datumAkontacije, LocalDate rokIsporuke,
                  Status status, boolean obrisan, Set<Artikl> artikli) {
        this.id = id;
        this.kupac = kupac;
        this.brojUgovora = brojUgovora;
        this.datumAkontacije = datumAkontacije;
        this.rokIsporuke = rokIsporuke;
        this.status = status;
        this.obrisan = obrisan;
        this.artikli = artikli;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKupac() {
        return kupac;
    }

    public void setKupac(String kupac) {
        this.kupac = kupac;
    }

    public String getBrojUgovora() {
        return brojUgovora;
    }

    public void setBrojUgovora(String brojUgovora) {
        this.brojUgovora = brojUgovora;
    }

    public LocalDate getDatumAkontacije() {
        return datumAkontacije;
    }

    public void setDatumAkontacije(LocalDate datumAkontacije) {
        this.datumAkontacije = datumAkontacije;
    }

    public LocalDate getRokIsporuke() {
        return rokIsporuke;
    }

    public void setRokIsporuke(LocalDate rokIsporuke) {
        this.rokIsporuke = rokIsporuke;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isObrisan() {
        return obrisan;
    }

    public void setObrisan(boolean obrisan) {
        this.obrisan = obrisan;
    }

    public Set<Artikl> getArtikli() {
        return artikli;
    }

    public void setArtikli(Set<Artikl> artikli) {
        this.artikli = artikli;
    }

    @Override
    public String toString() {
        return "Ugovor{" +
                "id=" + id +
                ", kupac='" + kupac + '\'' +
                ", brojUgovora='" + brojUgovora + '\'' +
                ", datumAkontacije=" + datumAkontacije +
                ", rokIsporuke=" + rokIsporuke +
                ", status=" + status +
                ", obrisan=" + obrisan +
                ", artikli=" + artikli +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ugovor ugovor = (Ugovor) o;
        return Objects.equals(id, ugovor.id) && Objects.equals(brojUgovora, ugovor.brojUgovora);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, brojUgovora);
    }
}
