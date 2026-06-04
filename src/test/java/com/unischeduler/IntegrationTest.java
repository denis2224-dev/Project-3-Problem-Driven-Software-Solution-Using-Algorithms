package com.unischeduler;

import com.unischeduler.config.AsyncSyncConfiguration;
import com.unischeduler.config.DatabaseTestcontainer;
import com.unischeduler.config.JacksonConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = {
        UniSchedulerApp.class,
        JacksonConfiguration.class,
        AsyncSyncConfiguration.class,
        com.unischeduler.config.JacksonHibernateConfiguration.class,
    }
)
@ImportTestcontainers(DatabaseTestcontainer.class)
public @interface IntegrationTest {}
