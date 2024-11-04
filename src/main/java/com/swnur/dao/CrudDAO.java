package com.swnur.dao;

import java.util.List;
import java.util.Optional;

public interface CrudDAO<T, ID> {

    Optional<T> findByID(ID id);

    List<T> findAll();

    T insert(T entity);

    Optional<T> update(T entity);

    void delete(ID id);

}
