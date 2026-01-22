package com.monghit.java21test.features.scopedvalues.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Servicio que demuestra Scoped Values (Preview Feature en Java 21).
 *
 * NOTA: Scoped Values es una feature preview/incubator en Java 21.
 * Esta implementación usa ThreadLocal como alternativa para demostrar el concepto.
 */
@Service
public class ScopedValueService {

    private static final Logger log = LoggerFactory.getLogger(ScopedValueService.class);

    // Usando ThreadLocal como alternativa a ScopedValue
    private static final ThreadLocal<RequestContext> CONTEXT = new ThreadLocal<>();

    /**
     * Procesa una request con contexto.
     */
    public String processWithContext(RequestContext context) {
        log.info("Processing request with context: {}", context.requestId());

        try {
            CONTEXT.set(context);
            return performOperation();
        } finally {
            CONTEXT.remove();
        }
    }

    /**
     * Obtiene el contexto actual.
     */
    public RequestContext getCurrentContext() {
        RequestContext context = CONTEXT.get();
        if (context == null) {
            throw new IllegalStateException("No context available");
        }
        return context;
    }

    /**
     * Realiza una operación usando el contexto.
     */
    private String performOperation() {
        RequestContext context = getCurrentContext();
        return String.format(
            "Processed request %s for user %s at %s",
            context.requestId(),
            context.userId(),
            context.timestamp()
        );
    }

    /**
     * Demuestra scopes anidados.
     */
    public String nestedScopes(RequestContext outerContext, RequestContext innerContext) {
        log.info("Demonstrating nested scopes");

        try {
            CONTEXT.set(outerContext);
            String outerResult = "Outer: " + getCurrentContext().requestId();

            // Simular scope anidado
            CONTEXT.set(innerContext);
            String innerResult = "Inner: " + getCurrentContext().requestId();

            return outerResult + " | " + innerResult;
        } finally {
            CONTEXT.remove();
        }
    }

    public record RequestContext(
        String requestId,
        String userId,
        String timestamp
    ) {}
}
