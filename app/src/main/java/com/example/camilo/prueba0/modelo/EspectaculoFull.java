package com.example.camilo.prueba0.modelo;

import java.util.List;

/**
 * Created by User on 12/06/2017.
 */

public class EspectaculoFull
{
    private List<Espectaculo> content;
    private String last;
    private String totalElements;
    private String totalPages;
    private String size;
    private String number;
    private String sort;
    private String numberOfElements;


    public List<Espectaculo> getContent() {
        return content;
    }

    public void setContent(List<Espectaculo> content) {
        this.content = content;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(String totalElements) {
        this.totalElements = totalElements;
    }

    public String getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(String numberOfElements) {
        this.numberOfElements = numberOfElements;
    }
}
