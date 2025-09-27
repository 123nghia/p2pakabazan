package com.akabazan.repository.base;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import com.akabazan.repository.entity.AbstractEntity;

import java.io.Serializable;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T extends AbstractEntity, ID extends Serializable> 
        extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    // Soft delete
    default void softDelete(T entity) {
        entity.setDeletedAt(java.time.LocalDateTime.now());
        save(entity);
    }

    default void softDeleteById(ID id) {
        findById(id).ifPresent(this::softDelete);
    }

    // Override findById để loại bỏ entity đã soft deleted
    default Optional<T> findByIdActive(ID id) {
        return findById(id)
                .filter(e -> e.getDeletedAt() == null);
    }
}