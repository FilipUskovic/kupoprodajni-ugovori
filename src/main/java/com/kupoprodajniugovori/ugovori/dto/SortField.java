package com.kupoprodajniugovori.ugovori.dto;


public enum SortField {
    KUPAC("kupac"),
    BROJ_UGOVORA("brojUgovora"),
    DATUM_AKONTACIJE("datumAkontacije");

    private final String fieldName;

    SortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

}
