package com.akabazan.framework.data.config;

import com.akabazan.framework.data.spi.IdGenerator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.util.Properties;
import java.util.UUID;

/**
 * Hibernate ValueGenerator that integrates with p2p_framework_data's IdGenerator
 * (currently UuidV7Generator) to generate time-ordered UUIDs.
 * 
 * This generator uses SpringContextHelper to access the IdGenerator bean.
 */
public class UuidV7ValueGenerator implements IdentifierGenerator {

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) {
        // No configuration needed, will get IdGenerator dynamically
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        IdGenerator idGenerator = SpringContextHelper.getIdGenerator();
        
        if (idGenerator != null) {
            try {
                return UUID.fromString(idGenerator.generate());
            } catch (Exception e) {
                // Fallback to random UUID on error
                return UUID.randomUUID();
            }
        }
        // Fallback to random UUID if IdGenerator bean is not available
        return UUID.randomUUID();
    }
}

