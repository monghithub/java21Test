# Scoped Values (Preview)

## Descripción

Scoped Values es una Preview Feature en Java 21 que proporciona una alternativa moderna y eficiente a `ThreadLocal`. Permite compartir datos inmutables de forma segura dentro de un scope limitado, con mejor performance y sin los problemas de memory leaks que pueden ocurrir con ThreadLocal.

**IMPORTANTE**: Esta es una preview feature que requiere `--enable-preview` y puede cambiar en futuras versiones.

## Problema que Resuelve

### Antes de Java 21: ThreadLocal

ThreadLocal ha sido la forma estándar de mantener datos por thread, pero tiene varios problemas:

#### 1. Memory Leaks

```java
// ❌ Problema: ThreadLocal puede causar memory leaks
public class UserContext {
    private static final ThreadLocal<User> CURRENT_USER = new ThreadLocal<>();

    public static void setUser(User user) {
        CURRENT_USER.set(user);
    }

    public static User getUser() {
        return CURRENT_USER.get();
    }

    public static void clear() {
        CURRENT_USER.remove();  // ¡Fácil de olvidar!
    }
}

// Uso
UserContext.setUser(user);
try {
    // hacer trabajo...
} finally {
    UserContext.clear();  // Si olvidas esto → memory leak
}
```

**Problemas:**
- Fácil olvidar `remove()` → memory leak
- Datos mutables pueden causar bugs sutiles
- Alto overhead de memoria con virtual threads
- No es evidente cuándo el valor está disponible

#### 2. Performance con Virtual Threads

```java
// ❌ Problema: ThreadLocal + millones de virtual threads = alto consumo de memoria
ThreadLocal<Context> threadLocal = new ThreadLocal<>();

// Con 1 millón de virtual threads:
for (int i = 0; i < 1_000_000; i++) {
    Thread.ofVirtual().start(() -> {
        threadLocal.set(new Context());  // 1 millón de copias de Context!
        // trabajo...
        threadLocal.remove();
    });
}
```

#### 3. Alcance No Claro

```java
// ❌ Problema: No es claro dónde el ThreadLocal está disponible
ThreadLocal<String> requestId = new ThreadLocal<>();

public void handleRequest() {
    requestId.set(UUID.randomUUID().toString());

    doWork1();  // ¿Tiene acceso? Sí
    doWork2();  // ¿Tiene acceso? Sí

    // Llamada asíncrona en otro thread
    executor.submit(() -> {
        doWork3();  // ¿Tiene acceso? ¡NO! Diferente thread
    });
}
```

#### 4. Inmutabilidad No Garantizada

```java
// ❌ Problema: ThreadLocal permite mutación accidental
ThreadLocal<List<String>> data = new ThreadLocal<>();
data.set(new ArrayList<>());

// Otro código puede modificar la lista
List<String> list = data.get();
list.add("oops");  // Modificación accidental
```

### Con Scoped Values (Preview)

```java
// ✅ Solución: Scoped Values con alcance claro e inmutabilidad
public class UserContext {
    private static final ScopedValue<User> CURRENT_USER = ScopedValue.newInstance();

    public static void runWithUser(User user, Runnable task) {
        ScopedValue.where(CURRENT_USER, user).run(task);
    }  // ← user automáticamente no disponible después del scope

    public static User getUser() {
        return CURRENT_USER.get();
    }
}

// Uso
UserContext.runWithUser(user, () -> {
    // user disponible aquí
    doWork();
});
// user NO disponible aquí - automáticamente limpiado
```

**Ventajas:**
- Inmutable por diseño
- Scope explícito y claro
- Auto-limpieza (sin memory leaks)
- Mejor performance con virtual threads
- Propagación automática a subtasks

## Alternativa Actual: ThreadLocal

Como Scoped Values está en preview, el proyecto usa ThreadLocal como alternativa:

```java
@Service
public class ScopedValueService {

    private static final Logger log = LoggerFactory.getLogger(ScopedValueService.class);

    // Usando ThreadLocal como alternativa a ScopedValue
    private static final ThreadLocal<RequestContext> CONTEXT = new ThreadLocal<>();

    /**
     * Procesa una request con contexto
     */
    public String processWithContext(RequestContext context) {
        log.info("Processing request with context: {}", context.requestId());

        try {
            CONTEXT.set(context);
            return performOperation();
        } finally {
            CONTEXT.remove();  // ¡Importante! Previene memory leak
        }
    }

    /**
     * Obtiene el contexto actual
     */
    public RequestContext getCurrentContext() {
        RequestContext context = CONTEXT.get();
        if (context == null) {
            throw new IllegalStateException("No context available");
        }
        return context;
    }

    /**
     * Realiza una operación usando el contexto
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
     * Demuestra scopes anidados
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
```

## ScopedValue API (Preview)

### Creación y Binding

```java
// Crear ScopedValue
public static final ScopedValue<User> CURRENT_USER = ScopedValue.newInstance();

// Bind y ejecutar
ScopedValue.where(CURRENT_USER, user).run(() -> {
    // user disponible aquí
    User u = CURRENT_USER.get();
    processUser(u);
});

// O con resultado
String result = ScopedValue.where(CURRENT_USER, user).call(() -> {
    return "User: " + CURRENT_USER.get().name();
});
```

### Múltiples Bindings

```java
public static final ScopedValue<User> CURRENT_USER = ScopedValue.newInstance();
public static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();
public static final ScopedValue<Locale> LOCALE = ScopedValue.newInstance();

// Bind múltiples valores
ScopedValue.where(CURRENT_USER, user)
           .where(REQUEST_ID, requestId)
           .where(LOCALE, Locale.US)
           .run(() -> {
               User u = CURRENT_USER.get();
               String id = REQUEST_ID.get();
               Locale loc = LOCALE.get();
               // todos disponibles aquí
           });
```

### Propagación a Subtasks

```java
// ScopedValues se propagan automáticamente a subtasks
ScopedValue.where(CURRENT_USER, user).run(() -> {

    // Scope original
    User u1 = CURRENT_USER.get();

    // Lanzar subtask - el valor se propaga automáticamente
    Thread.ofVirtual().start(() -> {
        User u2 = CURRENT_USER.get();  // ✓ Mismo user, automáticamente
        // trabajar con u2...
    }).join();

    // Con StructuredTaskScope también funciona
    try (var scope = new StructuredTaskScope<>()) {
        scope.fork(() -> {
            User u3 = CURRENT_USER.get();  // ✓ Propagado automáticamente
            return processUser(u3);
        });
        scope.join();
    }
});
```

## Casos de Uso

### 1. Request Context en Web Applications

```java
/**
 * Mantener contexto de HTTP request
 */
public class RequestContext {
    private static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();
    private static final ScopedValue<User> CURRENT_USER = ScopedValue.newInstance();
    private static final ScopedValue<Locale> LOCALE = ScopedValue.newInstance();

    public static void executeInContext(
            String requestId,
            User user,
            Locale locale,
            Runnable task) {

        ScopedValue.where(REQUEST_ID, requestId)
                   .where(CURRENT_USER, user)
                   .where(LOCALE, locale)
                   .run(task);
    }

    public static String getRequestId() {
        return REQUEST_ID.get();
    }

    public static User getCurrentUser() {
        return CURRENT_USER.get();
    }

    public static Locale getLocale() {
        return LOCALE.get();
    }
}

// Uso en un endpoint
@GetMapping("/api/order/{id}")
public Order getOrder(@PathVariable String id) {
    String requestId = UUID.randomUUID().toString();
    User user = getCurrentUser();
    Locale locale = getLocaleFromHeaders();

    return RequestContext.executeInContext(requestId, user, locale, () -> {
        // Todo el código aquí tiene acceso al contexto
        log.info("Processing request {}", RequestContext.getRequestId());
        return orderService.getOrder(id);
    });
}
```

### 2. Transaction Context

```java
/**
 * Contexto de transacción de base de datos
 */
public class TransactionContext {
    private static final ScopedValue<Transaction> CURRENT_TX = ScopedValue.newInstance();

    public static <T> T executeInTransaction(Callable<T> task) throws Exception {
        Transaction tx = beginTransaction();

        try {
            return ScopedValue.where(CURRENT_TX, tx).call(() -> {
                T result = task.call();
                tx.commit();
                return result;
            });
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }
    }

    public static Transaction getCurrentTransaction() {
        return CURRENT_TX.get();
    }
}

// Uso
TransactionContext.executeInTransaction(() -> {
    userRepository.save(user);
    // La transacción está disponible automáticamente
    orderRepository.save(order);
    return order;
});
```

### 3. Security Context

```java
/**
 * Contexto de seguridad con permisos
 */
public class SecurityContext {
    private static final ScopedValue<Principal> PRINCIPAL = ScopedValue.newInstance();
    private static final ScopedValue<Set<Permission>> PERMISSIONS = ScopedValue.newInstance();

    public static void executeAsUser(Principal principal, Runnable task) {
        Set<Permission> permissions = loadPermissions(principal);

        ScopedValue.where(PRINCIPAL, principal)
                   .where(PERMISSIONS, Set.copyOf(permissions))  // Inmutable
                   .run(task);
    }

    public static void checkPermission(Permission required) {
        if (!PERMISSIONS.get().contains(required)) {
            throw new SecurityException("Permission denied: " + required);
        }
    }

    public static Principal getPrincipal() {
        return PRINCIPAL.get();
    }
}

// Uso
SecurityContext.executeAsUser(principal, () -> {
    SecurityContext.checkPermission(Permission.READ_ORDERS);
    orderService.getOrders();
});
```

### 4. Logging Context

```java
/**
 * Contexto para logging correlacionado
 */
public class LoggingContext {
    private static final ScopedValue<String> TRACE_ID = ScopedValue.newInstance();
    private static final ScopedValue<String> SPAN_ID = ScopedValue.newInstance();

    public static void executeWithTracing(String traceId, Runnable task) {
        String spanId = UUID.randomUUID().toString();

        ScopedValue.where(TRACE_ID, traceId)
                   .where(SPAN_ID, spanId)
                   .run(task);
    }

    public static String getTraceId() {
        return TRACE_ID.orElse("no-trace");
    }

    public static String getSpanId() {
        return SPAN_ID.orElse("no-span");
    }

    public static String formatLogMessage(String message) {
        return String.format("[trace=%s, span=%s] %s",
            getTraceId(), getSpanId(), message);
    }
}

// Uso
LoggingContext.executeWithTracing(traceId, () -> {
    log.info(LoggingContext.formatLogMessage("Processing order"));
    orderService.process();  // Logs automáticamente incluyen trace/span
});
```

### 5. Feature Flags Context

```java
/**
 * Contexto de feature flags
 */
public class FeatureFlagContext {
    private static final ScopedValue<Map<String, Boolean>> FLAGS = ScopedValue.newInstance();

    public static void executeWithFlags(Map<String, Boolean> flags, Runnable task) {
        ScopedValue.where(FLAGS, Map.copyOf(flags))  // Inmutable
                   .run(task);
    }

    public static boolean isEnabled(String featureFlag) {
        return FLAGS.get().getOrDefault(featureFlag, false);
    }
}

// Uso
Map<String, Boolean> flags = loadFlagsForUser(user);
FeatureFlagContext.executeWithFlags(flags, () -> {
    if (FeatureFlagContext.isEnabled("new-checkout")) {
        useNewCheckout();
    } else {
        useOldCheckout();
    }
});
```

## Comparación: ThreadLocal vs ScopedValue

| Aspecto | ThreadLocal | ScopedValue (Preview) |
|---------|-------------|----------------------|
| Mutabilidad | Mutable | Inmutable |
| Limpieza | Manual (remove()) | Automática |
| Memory Leaks | Posible | Imposible |
| Alcance | No claro | Explícito |
| Performance | Buena | Mejor |
| Virtual Threads | Alto overhead | Optimizado |
| Propagación | No | Sí (automática) |
| Inheritance | InheritableThreadLocal | Automático |

### Ejemplo Comparativo

```java
// ========== ThreadLocal ==========
ThreadLocal<User> threadLocal = new ThreadLocal<>();

public void processWithThreadLocal(User user) {
    try {
        threadLocal.set(user);

        doWork();  // user disponible

        // Lanzar thread hijo
        executor.submit(() -> {
            User u = threadLocal.get();  // ❌ null - no se hereda
        });

    } finally {
        threadLocal.remove();  // ¡No olvidar!
    }
}

// ========== ScopedValue ==========
ScopedValue<User> scopedValue = ScopedValue.newInstance();

public void processWithScopedValue(User user) {
    ScopedValue.where(scopedValue, user).run(() -> {

        doWork();  // user disponible

        // Lanzar subtask
        executor.submit(() -> {
            User u = scopedValue.get();  // ✓ user disponible automáticamente
        });

    });  // Auto-limpieza automática
}
```

## Beneficios de Scoped Values

### 1. Inmutabilidad por Defecto

```java
// ✓ Los valores son inmutables dentro del scope
ScopedValue<List<String>> DATA = ScopedValue.newInstance();

ScopedValue.where(DATA, List.of("A", "B", "C")).run(() -> {
    List<String> list = DATA.get();
    list.add("D");  // ❌ UnsupportedOperationException - inmutable
});
```

### 2. Scope Claro y Explícito

```java
// ✓ Es obvio dónde el valor está disponible
ScopedValue.where(USER, user).run(() -> {
    // Disponible aquí dentro del run()
    User u = USER.get();
});
// No disponible aquí - fuera del scope
```

### 3. Propagación Automática

```java
// ✓ Se propaga automáticamente a subtasks estructuradas
ScopedValue.where(USER, user).run(() -> {
    try (var scope = new StructuredTaskScope<>()) {
        scope.fork(() -> {
            USER.get();  // ✓ Disponible automáticamente
        });
    }
});
```

### 4. Performance con Virtual Threads

```java
// ✓ Optimizado para millones de virtual threads
for (int i = 0; i < 1_000_000; i++) {
    Thread.ofVirtual().start(() -> {
        ScopedValue.where(DATA, value).run(() -> {
            // Bajo overhead de memoria
        });
    });
}
```

## Buenas Prácticas (Cuando se Estabilice)

### ✅ DO: Usar para Datos Inmutables

```java
// ✅ CORRECTO: Datos inmutables
ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();
ScopedValue<User> CURRENT_USER = ScopedValue.newInstance();  // Records son inmutables

// ❌ EVITAR: Datos mutables
ScopedValue<List<String>> MUTABLE_LIST = ScopedValue.newInstance();
// Mejor: usar List.copyOf() para hacerlo inmutable
```

### ✅ DO: Usar static final para ScopedValue Instances

```java
// ✅ CORRECTO: static final
public static final ScopedValue<User> CURRENT_USER = ScopedValue.newInstance();

// ❌ INCORRECTO: instancia no static
private ScopedValue<User> currentUser = ScopedValue.newInstance();
```

### ✅ DO: Documentar el Scope

```java
// ✅ CORRECTO: Documentar dónde está disponible
/**
 * Current authenticated user.
 * Available within the scope of {@link #executeWithUser(User, Runnable)}.
 */
public static final ScopedValue<User> CURRENT_USER = ScopedValue.newInstance();
```

### ⚠️ CUIDADO: No Acceder Fuera del Scope

```java
// ⚠️ ERROR: Acceder fuera del scope
User user = ...;
ScopedValue.where(CURRENT_USER, user).run(() -> {
    // disponible aquí
});

User u = CURRENT_USER.get();  // ❌ NoSuchElementException - fuera del scope
```

### ⚠️ CUIDADO: Hacer Copias Inmutables

```java
// ⚠️ EVITAR: Pasar colecciones mutables
List<String> mutableList = new ArrayList<>();
ScopedValue.where(DATA, mutableList).run(() -> {
    // mutableList puede ser modificada externamente
});

// ✅ MEJOR: Copiar como inmutable
ScopedValue.where(DATA, List.copyOf(mutableList)).run(() -> {
    // Garantizado inmutable
});
```

## Limitaciones

1. **Preview Feature**: Requiere --enable-preview, puede cambiar

2. **Solo Inmutable**: No apropiado para datos que necesitan mutar

3. **No Reemplaza Todo**: ThreadLocal sigue siendo útil para algunos casos

4. **Learning Curve**: Paradigma diferente de ThreadLocal

## Configuración

```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <release>21</release>
        <compilerArgs>
            <arg>--enable-preview</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

```bash
# Ejecución
java --enable-preview MyApp
```

## Migración de ThreadLocal a ScopedValue

```java
// ANTES: ThreadLocal
public class OldContext {
    private static final ThreadLocal<User> USER = new ThreadLocal<>();

    public static void setUser(User user) {
        USER.set(user);
    }

    public static User getUser() {
        return USER.get();
    }

    public static void clear() {
        USER.remove();
    }
}

// Uso
OldContext.setUser(user);
try {
    doWork();
} finally {
    OldContext.clear();
}

// DESPUÉS: ScopedValue
public class NewContext {
    private static final ScopedValue<User> USER = ScopedValue.newInstance();

    public static void executeWithUser(User user, Runnable task) {
        ScopedValue.where(USER, user).run(task);
    }

    public static User getUser() {
        return USER.get();
    }
}

// Uso
NewContext.executeWithUser(user, () -> {
    doWork();
});
```

## Conclusión

Scoped Values es una evolución moderna de ThreadLocal que:

- **Previene**: Memory leaks automáticamente
- **Garantiza**: Inmutabilidad por diseño
- **Optimiza**: Performance con virtual threads
- **Clarifica**: Alcance explícito del contexto

Aunque está en preview, representa el futuro de context propagation en Java. Mientras tanto:

- **Usa** ThreadLocal con cuidado (siempre `remove()`)
- **Monitorea** la evolución de Scoped Values
- **Prepárate** para migrar cuando se estabilice

Es probable que se convierta en estándar en Java 22 o 23.

## Referencias

- **JEP 446**: Scoped Values (Preview)
- **JEP 429**: Scoped Values (Incubator)
- **Documentación oficial**: https://openjdk.org/jeps/446

## Ejemplos en el Proyecto

- Implementación: `src/main/java/com/monghit/java21test/features/scopedvalues/`
- Tests: `src/test/java/com/monghit/java21test/features/scopedvalues/`
- Endpoints: `/api/scoped-values/**`

**Nota**: El proyecto usa ThreadLocal como alternativa estable hasta que Scoped Values se gradúe de preview.
