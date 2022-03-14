package com.github.filechka.rpttracker.repository;

import java.util.Collection;

public interface CommonRepository<T> {
    public T save(T domain);
    public Iterable<T> save(Collection<T> domains);
    public void delete(String id);
    public T findById(String id);
    public Iterable<T> findAll();
}
