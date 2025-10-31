package com.akabazan.framework.data.config;

import com.akabazan.framework.data.spi.IdGenerator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Helper class to access Spring ApplicationContext from non-Spring managed classes
 * (e.g., Hibernate generators).
 */
@Component
public class SpringContextHelper implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        SpringContextHelper.applicationContext = context;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static IdGenerator getIdGenerator() {
        if (applicationContext == null) {
            return null;
        }
        try {
            return applicationContext.getBean(IdGenerator.class);
        } catch (BeansException e) {
            return null;
        }
    }
}

