package com.akabazan.repository.base;

import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import com.akabazan.repository.entity.AbstractEntity;

import java.io.Serializable;

public class BaseRepositoryImpl<T extends AbstractEntity, ID extends Serializable>
        extends SimpleJpaRepository<T, ID>
        implements BaseRepository<T, ID> {

    public BaseRepositoryImpl(Class<T> domainClass, jakarta.persistence.EntityManager em) {
        super(domainClass, em);
    }
}
