package com.akabazan.repository.base;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import com.akabazan.repository.entity.AbstractEntity;

import java.io.Serializable;

public class BaseRepositoryImpl<T extends AbstractEntity, ID extends Serializable>
        extends SimpleJpaRepository<T, ID>
        implements BaseRepository<T, ID> {

    @PersistenceContext
    private EntityManager em;

    public BaseRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.em = em;
    }

    @Override
    public void softDelete(T entity) {
        entity.setDeletedAt(java.time.LocalDateTime.now());
        save(entity);
    }
}
