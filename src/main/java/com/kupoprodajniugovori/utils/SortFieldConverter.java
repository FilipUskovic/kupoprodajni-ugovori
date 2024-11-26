package com.kupoprodajniugovori.utils;

import com.kupoprodajniugovori.ugovori.dto.SortField;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SortFieldConverter implements Converter<String, SortField> {

    @Override
    public SortField convert(String source) {
        try {
            if (source.trim().isEmpty()) {
                return SortField.KUPAC;
            }

            String cleanValue = source.replaceAll("[\\[\\]\"]", "").trim().toUpperCase();
            return SortField.valueOf(cleanValue);
        } catch (IllegalArgumentException e) {
            return SortField.KUPAC; // default vrijednost
        }
    }
}
