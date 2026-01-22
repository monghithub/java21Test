# Structured Concurrency (Preview)

## Descripción

Structured Concurrency es una Preview Feature en Java 21 que introduce una nueva forma de manejar concurrencia tratando las tareas concurrentes como una unidad estructurada. Garantiza que si una tarea crea subtareas, todas ellas completen (o fallen) juntas antes de que la tarea principal continúe.

**IMPORTANTE**: Esta es una preview/incubator feature que requiere `--enable-preview` y puede cambiar en futuras versiones.

## Problema que Resuelve

### Antes de Java 21

El manejo tradicional de tareas concurrentes tiene varios problemas:

#### 1. Fugas de Recursos (Thread Leaks)

```java
// ❌ Problema: Si una tarea falla, las otras continúan indefinidamente
public Result fetchData() throws Exception {
    ExecutorService executor = Executors.newCachedThreadPool();

    Future<String> future1 = executor.submit(() -> fetchFromAPI1());
    Future<String> future2 = executor.submit(() -> fetchFromAPI2());
    Future<String> future3 = executor.submit(() -> fetchFromAPI3());

    try {
        String data1 = future1.get();  // Si falla aquí...
        String data2 = future2.get();  // ...nunca llegamos a cancelar estas
        String data3 = future3.get();

        return new Result(data1, data2, data3);
    } finally {
        executor.shutdown();  // Espera a que TODAS terminen, aunque no las necesitemos
    }
}
```

**Problemas:**
- Si `future1.get()` falla, `future2` y `future3` siguen ejecutándose inútilmente
- Desperdicio de recursos (CPU, red, memoria)
- El executor espera a todas las tareas aunque no las necesitemos
- Difícil cancelar tareas en progreso

#### 2. Manejo de Errores Complejo

```java
// ❌ Problema: Manejo de errores y cancelación manual
public Result fetchParallel() {
    ExecutorService executor = Executors.newFixedThreadPool(3);

    Future<String> f1 = executor.submit(() -> fetchFromAPI1());
    Future<String> f2 = executor.submit(() -> fetchFromAPI2());
    Future<String> f3 = executor.submit(() -> fetchFromAPI3());

    try {
        String data1 = f1.get();
        String data2 = f2.get();
        String data3 = f3.get();

        return new Result(data1, data2, data3);

    } catch (Exception e) {
        // Cancelar manualmente las tareas pendientes
        f1.cancel(true);
        f2.cancel(true);
        f3.cancel(true);

        throw new RuntimeException(e);

    } finally {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
```

**Problemas:**
- Código verboso y propenso a errores
- Fácil olvidar cancelar tareas
- Manejo de timeout complicado
- Limpieza de recursos manual

#### 3. Debugging Difícil

```java
// ❌ Problema: Stack traces no muestran la relación parent-child
ExecutorService executor = Executors.newCachedThreadPool();

executor.submit(() -> {
    // Si falla aquí, el stack trace no muestra quién lo lanzó
    throw new RuntimeException("Error in subtask");
});

// Stack trace pierde contexto del thread padre
```

### Con Structured Concurrency (Preview)

```java
// ✅ Solución: Tareas estructuradas con StructuredTaskScope
public Result fetchData() throws Exception {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

        // Lanzar subtareas
        Subtask<String> subtask1 = scope.fork(() -> fetchFromAPI1());
        Subtask<String> subtask2 = scope.fork(() -> fetchFromAPI2());
        Subtask<String> subtask3 = scope.fork(() -> fetchFromAPI3());

        // Esperar a que TODAS completen o una falle
        scope.join()           // Espera a que todas terminen
             .throwIfFailed(); // Lanza excepción si alguna falló

        // Todas completaron exitosamente
        return new Result(
            subtask1.get(),
            subtask2.get(),
            subtask3.get()
        );

    }  // Auto-cierre: Cancela tareas pendientes automáticamente
}
```

**Ventajas:**
- Cancelación automática si una falla
- Limpieza de recursos garantizada (try-with-resources)
- Código más simple y claro
- Stack traces mejores con contexto parent-child
- Sin thread leaks

## Alternativa Actual: ExecutorService con Virtual Threads

Como Structured Concurrency está en preview, el proyecto usa ExecutorService con Virtual Threads:

```java
@Service
public class StructuredConcurrencyService {

    /**
     * Obtiene datos de múltiples fuentes en paralelo
     * Alternativa usando ExecutorService tradicional
     */
    public AggregatedResult fetchParallelData() {
        log.info("Fetching data from multiple sources in parallel");

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            // Lanzar tareas en paralelo
            Future<String> future1 = executor.submit(() -> fetchFromSource1());
            Future<String> future2 = executor.submit(() -> fetchFromSource2());
            Future<String> future3 = executor.submit(() -> fetchFromSource3());

            // Recolectar resultados
            String data1 = future1.get();
            String data2 = future2.get();
            String data3 = future3.get();

            return new AggregatedResult(data1, data2, data3, "Success");

        } catch (InterruptedException | ExecutionException e) {
            log.error("Error fetching parallel data", e);
            return new AggregatedResult("Error", "Error", "Error", e.getMessage());
        }
    }

    private String fetchFromSource1() throws InterruptedException {
        Thread.sleep(100);
        return "Data from source 1";
    }

    private String fetchFromSource2() throws InterruptedException {
        Thread.sleep(150);
        return "Data from source 2";
    }

    private String fetchFromSource3() throws InterruptedException {
        Thread.sleep(120);
        return "Data from source 3";
    }

    public record AggregatedResult(
        String source1Data,
        String source2Data,
        String source3Data,
        String status
    ) {}
}
```

## StructuredTaskScope Variants (Preview)

### 1. ShutdownOnFailure

Cancela todas las tareas si alguna falla:

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Subtask<String> user = scope.fork(() -> fetchUser(userId));
    Subtask<List<Order>> orders = scope.fork(() -> fetchOrders(userId));
    Subtask<Payment> payment = scope.fork(() -> fetchPayment(userId));

    // Si alguna falla, las otras se cancelan automáticamente
    scope.join().throwIfFailed();

    return new UserData(user.get(), orders.get(), payment.get());
}
```

### 2. ShutdownOnSuccess

Cancela todas las tareas cuando la primera completa exitosamente:

```java
// Útil para "race conditions" - la más rápida gana
try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
    scope.fork(() -> fetchFromServer1());
    scope.fork(() -> fetchFromServer2());
    scope.fork(() -> fetchFromServer3());

    // Espera a que la primera complete
    scope.join();

    // Retorna el resultado de la ganadora (las otras se cancelan)
    return scope.result();
}
```

### 3. Custom Shutdown Policy

```java
// Política personalizada: cancela si >2 fallan
class ShutdownOnTwoFailures extends StructuredTaskScope<String> {
    private final AtomicInteger failures = new AtomicInteger();

    @Override
    protected void handleComplete(Subtask<? extends String> subtask) {
        if (subtask.state() == Subtask.State.FAILED) {
            if (failures.incrementAndGet() >= 2) {
                shutdown();  // Cancelar todas
            }
        }
    }
}

try (var scope = new ShutdownOnTwoFailures()) {
    scope.fork(() -> task1());
    scope.fork(() -> task2());
    scope.fork(() -> task3());
    scope.join();
    // Continúa si máximo 1 falla
}
```

## Casos de Uso

### 1. Agregación de Datos de Múltiples Fuentes

```java
/**
 * Obtener datos de usuario de múltiples servicios
 */
public UserProfile buildUserProfile(String userId) throws Exception {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

        // Todas estas llamadas en paralelo
        Subtask<User> userTask = scope.fork(() ->
            userService.getUser(userId));

        Subtask<List<Order>> ordersTask = scope.fork(() ->
            orderService.getOrders(userId));

        Subtask<Address> addressTask = scope.fork(() ->
            addressService.getAddress(userId));

        Subtask<PaymentInfo> paymentTask = scope.fork(() ->
            paymentService.getPaymentInfo(userId));

        // Esperar a todas o fallar si alguna falla
        scope.join().throwIfFailed();

        // Todas completaron, construir perfil
        return new UserProfile(
            userTask.get(),
            ordersTask.get(),
            addressTask.get(),
            paymentTask.get()
        );
    }
}
```

### 2. Validación Paralela

```java
/**
 * Validar datos desde múltiples fuentes en paralelo
 */
public ValidationResult validateOrder(Order order) throws Exception {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

        Subtask<Boolean> inventoryCheck = scope.fork(() ->
            inventoryService.isAvailable(order.items()));

        Subtask<Boolean> paymentCheck = scope.fork(() ->
            paymentService.validatePayment(order.payment()));

        Subtask<Boolean> shippingCheck = scope.fork(() ->
            shippingService.canShipTo(order.address()));

        Subtask<Boolean> fraudCheck = scope.fork(() ->
            fraudService.checkOrder(order));

        scope.join().throwIfFailed();

        boolean allValid = inventoryCheck.get() &&
                          paymentCheck.get() &&
                          shippingCheck.get() &&
                          fraudCheck.get();

        return new ValidationResult(allValid, "Validation complete");
    }
}
```

### 3. Fastest Response (Race Condition)

```java
/**
 * Obtener datos del servidor que responda más rápido
 */
public Data fetchFromFastestServer(List<String> servers) throws Exception {
    try (var scope = new StructuredTaskScope.ShutdownOnSuccess<Data>()) {

        // Lanzar request a todos los servidores
        for (String server : servers) {
            scope.fork(() -> fetchFromServer(server));
        }

        // Esperar a que el primero responda
        scope.join();

        // Las otras requests se cancelan automáticamente
        return scope.result();
    }
}
```

### 4. Fan-Out/Fan-In Pattern

```java
/**
 * Procesar lista de items en paralelo y agregar resultados
 */
public Summary processItems(List<Item> items) throws Exception {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

        // Fork una tarea por cada item
        List<Subtask<ProcessedItem>> subtasks = items.stream()
            .map(item -> scope.fork(() -> processItem(item)))
            .toList();

        // Esperar a que todas completen
        scope.join().throwIfFailed();

        // Agregar resultados
        List<ProcessedItem> results = subtasks.stream()
            .map(Subtask::get)
            .toList();

        return new Summary(results);
    }
}
```

### 5. Timeout Handling

```java
/**
 * Ejecutar tareas con timeout global
 */
public Result fetchWithTimeout(long timeoutMs) throws Exception {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

        Subtask<String> data1 = scope.fork(() -> slowOperation1());
        Subtask<String> data2 = scope.fork(() -> slowOperation2());

        // Join con timeout
        scope.joinUntil(Instant.now().plusMillis(timeoutMs));

        // Si no terminaron, throwIfFailed() lanzará excepción
        scope.throwIfFailed();

        return new Result(data1.get(), data2.get());

    } catch (TimeoutException e) {
        // Todas las subtareas se cancelan automáticamente
        throw new RuntimeException("Operation timed out", e);
    }
}
```

## Beneficios de Structured Concurrency

### 1. Limpieza de Recursos Garantizada

```java
// try-with-resources garantiza que las tareas se cancelen
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    scope.fork(() -> task1());
    scope.fork(() -> task2());
    scope.join();
}  // ← Automáticamente cancela tareas pendientes
```

### 2. Mejor Observabilidad

```java
// Stack traces muestran relación parent-child
Thread.currentThread().getStackTrace()
// Muestra:
//   at task1()
//   at StructuredTaskScope.fork()
//   at fetchData()  ← El contexto del padre se mantiene
```

### 3. Cancellation Propagation

```java
// Si el thread padre se interrumpe, las subtareas también
Thread.currentThread().interrupt();
// ↓ Todas las subtareas del scope se cancelan automáticamente
```

### 4. Error Handling Simplificado

```java
// No necesitas try-catch por cada subtarea
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    scope.fork(() -> mayFail1());
    scope.fork(() -> mayFail2());
    scope.join().throwIfFailed();  // Maneja todas las excepciones
}
```

## Comparación Detallada

| Aspecto | ExecutorService | Structured Concurrency |
|---------|----------------|------------------------|
| Cancelación | Manual | Automática |
| Limpieza | Manual (finally) | Automática (try-with-resources) |
| Error handling | try-catch por tarea | Unificado con throwIfFailed() |
| Thread leaks | Posible | Imposible |
| Stack traces | Desconectados | Contexto preservado |
| Timeout | Complejo | joinUntil() simple |
| Código | Verbose | Conciso |

## Buenas Prácticas (Cuando se Estabilice)

### ✅ DO: Usar try-with-resources

```java
// ✅ CORRECTO: Garantiza limpieza
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    // tareas...
    scope.join();
}

// ❌ INCORRECTO: Sin try-with-resources puede haber leaks
var scope = new StructuredTaskScope.ShutdownOnFailure();
// ... usar scope ...
// ← Olvidar cerrar = thread leak
```

### ✅ DO: Elegir la Política Correcta

```java
// ✅ CORRECTO: ShutdownOnFailure para operaciones dependientes
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    // Todas necesarias - si una falla, cancelar las demás
}

// ✅ CORRECTO: ShutdownOnSuccess para race conditions
try (var scope = new StructuredTaskScope.ShutdownOnSuccess<Data>()) {
    // Solo necesitamos una - cancelar el resto cuando una complete
}
```

### ✅ DO: Usar con Virtual Threads

```java
// ✅ CORRECTO: Combinar con virtual threads para máxima escalabilidad
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    // Cada fork crea un virtual thread - puedes crear millones
    for (int i = 0; i < 1_000_000; i++) {
        scope.fork(() -> processItem(i));
    }
    scope.join();
}
```

### ⚠️ CUIDADO: No Olvidar join()

```java
// ⚠️ ERROR: Olvidar join() - las tareas pueden no haber completado
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Subtask<String> task = scope.fork(() -> fetchData());
    return task.get();  // ❌ Puede fallar - la tarea puede no haber completado
}

// ✅ CORRECTO
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Subtask<String> task = scope.fork(() -> fetchData());
    scope.join().throwIfFailed();  // ✓ Espera a que complete
    return task.get();
}
```

## Limitaciones Actuales

1. **Preview Feature**: Requiere --enable-preview, puede cambiar

2. **API en Evolución**: Puede haber cambios en futuras versiones

3. **Complejidad para Casos Simples**: Para 1-2 tareas puede ser overkill

4. **Learning Curve**: Nuevo paradigma para desarrolladores Java

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
            <arg>--add-modules</arg>
            <arg>jdk.incubator.concurrent</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

```bash
# Ejecución
java --enable-preview --add-modules jdk.incubator.concurrent MyApp
```

## Conclusión

Structured Concurrency representa un cambio fundamental en cómo manejamos concurrencia:

- **Seguridad**: Garantiza limpieza de recursos
- **Simplicidad**: Código más claro y mantenible
- **Robustez**: Previene thread leaks y otros problemas comunes
- **Observabilidad**: Mejor debugging y monitoring

Aunque está en preview, es una dirección muy prometedora. Mientras tanto:

- **Usa** ExecutorService con Virtual Threads
- **Monitorea** la evolución de esta feature
- **Prepárate** para adoptarla cuando se estabilice

Es probable que se convierta en la forma estándar de manejar concurrencia en Java.

## Referencias

- **JEP 453**: Structured Concurrency (Preview)
- **JEP 437**: Structured Concurrency (Second Incubator)
- **JEP 428**: Structured Concurrency (Incubator)
- **Documentación oficial**: https://openjdk.org/jeps/453

## Ejemplos en el Proyecto

- Implementación: `src/main/java/com/monghit/java21test/features/structuredconcurrency/`
- Tests: `src/test/java/com/monghit/java21test/features/structuredconcurrency/`
- Endpoints: `/api/structured-concurrency/**`

**Nota**: El proyecto usa ExecutorService con Virtual Threads como alternativa estable hasta que Structured Concurrency se gradúe de preview.
