package com.kupoprodajniugovori.artikli;

import com.kupoprodajniugovori.utils.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtiklRepository extends JpaRepository<Artikl, Long> {

    List<Artikl> findByUgovorIdAndUgovorObrisanFalse(Long ugovorId);
    boolean existsByIdAndUgovorIdAndUgovorObrisanFalse(Long id, Long ugovorId);

    Page<Artikl> findAllByUgovorObrisanFalse(Pageable pageable);


    @Modifying
    @Query("UPDATE Artikl a SET a.status = :status WHERE a.ugovor.id = :ugovorId")
    void updateStatusForUgovor(@Param("ugovorId") Long ugovorId, @Param("status") Status status);
}
