package edu.eci.dosw.DOSW_Library.tdd.persistence.port;

import java.util.List;
import java.util.Optional;

/**
 * Contrato CRUD genérico para la capa de persistencia (independiente de JPA o Mongo).
 */
public interface GenericRepository<T, ID> {

    T save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    void deleteById(ID id);

    boolean existsById(ID id);
}
