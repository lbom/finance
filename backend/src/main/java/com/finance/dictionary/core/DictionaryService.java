package com.finance.dictionary.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public abstract class DictionaryService<E> {

    private final JpaRepository<E, Long> repository;

    public DictionaryService(JpaRepository<E, Long> repository) {
        this.repository = repository;
    }

    public List<E> getList() {
        return repository.findAll();
    }
    public void save(E data) {
        repository.save(data);
    }
}