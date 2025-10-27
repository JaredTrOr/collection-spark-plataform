package org.jared.trujillo.interfaces;

import java.util.List;
import java.util.Optional;

public interface CrudRepository <T, ID> {
    T create(T entity);

    List<T> findAll();

    Optional<T> findById(ID id);

    Optional<T> update(ID id, T entity);

    boolean delete(ID id);
}
