package com.monghit.java21test.config;

import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;

import java.util.concurrent.Executors;

/**
 * Configuración para habilitar Virtual Threads en toda la aplicación.
 *
 * Esta configuración:
 * - Configura Tomcat para usar Virtual Threads en el manejo de requests HTTP
 * - Configura el AsyncTaskExecutor de Spring para usar Virtual Threads
 *
 * Los Virtual Threads (Project Loom) permiten manejar miles o millones de
 * threads concurrentes con un overhead mínimo de memoria y CPU.
 */
@Configuration
public class VirtualThreadConfig {

    /**
     * Configura el protocol handler de Tomcat para usar Virtual Threads.
     * Esto hace que cada request HTTP sea manejado por un Virtual Thread.
     */
    @Bean
    public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer() {
        return protocolHandler -> {
            protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        };
    }

    /**
     * Configura el AsyncTaskExecutor de Spring para usar Virtual Threads.
     * Esto permite que todas las operaciones asíncronas de Spring usen Virtual Threads.
     */
    @Bean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    public AsyncTaskExecutor asyncTaskExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }
}
