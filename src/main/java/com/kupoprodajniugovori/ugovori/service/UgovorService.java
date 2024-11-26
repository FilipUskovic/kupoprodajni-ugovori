package com.kupoprodajniugovori.ugovori.service;

import com.kupoprodajniugovori.ugovori.UgovorSearchCriteria;
import com.kupoprodajniugovori.ugovori.dto.*;
import com.kupoprodajniugovori.utils.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface UgovorService {

    List<UgovoriResponseDTO> findUgovori(UgovorSearchCriteria criteria, Pageable pageable, SortField sort);
    UgovoriResponseDTO createUgovor(CreateUgovorDTO createUgovorDTO);
    UgovoriResponseDTO updateUgovor(Long id, UpdateUgovorDTO updateUgovor);
    void deleteUgovor(Long id);
    UgovoriResponseDTO updateStatus(Long id, Status newStats);
    // ovo se ne trazi (opticionalno je)
    DetailsUgovorDTO findUgovorById(Long id);


}
