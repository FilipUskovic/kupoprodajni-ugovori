package com.kupoprodajniugovori.ugovori;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // iako ne treba  stavlajti, dobra je praksa
public interface UgovorRepository extends JpaRepository<Ugovor, Long>, JpaSpecificationExecutor<Ugovor> {

    Optional<Ugovor> findByIdAndObrisanFalse(Long id);
    boolean existsByBrojUgovoraAndObrisanFalse(String brojUgovora);

    @Query("SELECT COUNT(u) FROM Ugovor u WHERE EXTRACT(YEAR FROM u.datumAkontacije) = :year")
    long countByYear(@Param("year") int year);

    @Modifying
    @Query("UPDATE Ugovor u SET u.obrisan = true WHERE u.id = :id")
    void softDelete(@Param("id") Long id);




}
