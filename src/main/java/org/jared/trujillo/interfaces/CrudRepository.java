package org.jared.trujillo.interfaces;

import org.jared.trujillo.dto.Page;

import java.util.List;
import java.util.Optional;

public interface CrudRepository <T, ID> {
    T create(T entity);

    List<T> findAll();

    Page<T> findAllPaginated(int page, int limit);

    Optional<T> findById(ID id);

    Optional<T> update(ID id, T entity);

    boolean delete(ID id);

    boolean softDelete(ID id);
}
