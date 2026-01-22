# Virtual Threads (Project Loom)

## Descripción

Virtual Threads es una de las características más revolucionarias de Java 21. Son threads ligeros que permiten crear millones de hilos con un overhead mínimo, transformando radicalmente la forma en que escribimos aplicaciones concurrentes en Java.

A diferencia de los platform threads tradicionales (threads del sistema operativo), los virtual threads son gestionados por la JVM y no están directamente vinculados a threads del OS, lo que permite crearlos y destruirlos con un costo prácticamente nulo.

## Problema que Resuelve

### Antes de Java 21

En versiones anteriores de Java, teníamos principalmente dos enfoques para manejar concurrencia:

#### 1. Platform Threads (Threads tradicionales)
```java
// ❌ Problema: Cada thread es costoso (1MB de stack, mapeo 1:1 con OS threads)
ExecutorService executor = Executors.newFixedThreadPool(200);
for (int i = 0; i < 10000; i++) {
    final int taskId = i;
    executor.submit(() -> {
        // Con pool limitado, estas tareas esperan en cola
        performIOOperation(taskId);
    });
}
```

**Limitaciones:**
- Cada thread consume ~1MB de memoria de stack
- Mapeo 1:1 con threads del sistema operativo (limitados a miles)
- Context switching costoso
- Thread pools limitados causan cuellos de botella

#### 2. Async/Callbacks (CompletableFuture, Reactive)
```java
// ❌ Problema: Código complejo, difícil de depurar, "callback hell"
CompletableFuture.supplyAsync(() -> fetchUser(userId))
    .thenCompose(user -> fetchOrders(user.getId()))
    .thenCompose(orders -> fetchOrderDetails(orders))
    .thenApply(details -> processDetails(details))
    .exceptionally(ex -> handleError(ex));
```

**Limitaciones:**
- Código difícil de leer y mantener
- Stack traces confusos
- Debugging complicado
- Curva de aprendizaje alta

### Después de Java 21

```java
// ✅ Solución: Código simple, escalable y eficiente
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (int i = 0; i < 1_000_000; i++) {  // ¡Un millón de threads!
        final int taskId = i;
        executor.submit(() -> {
            performIOOperation(taskId);  // Código síncrono simple
        });
    }
}
```

**Ventajas:**
- Millones de threads sin problema
- Código síncrono simple y legible
- Stack traces completos y claros
- Compatible con código existente

## Casos de Uso Ideales

Virtual Threads son perfectos para:

### 1. Operaciones I/O Bound
```java
public TaskResult processRequest(String requestId) {
    // Operaciones I/O bloqueantes se benefician enormemente
    String userData = httpClient.get("/users/" + requestId);      // I/O
    String orderData = database.query("SELECT * FROM orders");    // I/O
    String paymentData = externalApi.fetchPayment(requestId);     // I/O

    return combineData(userData, orderData, paymentData);
}
```

### 2. Microservicios y APIs REST
```java
@RestController
public class OrderController {

    // Cada request en un virtual thread
    @GetMapping("/orders/{id}")
    public Order getOrder(@PathVariable String id) {
        // Llamadas bloqueantes simples, sin async complicado
        User user = userService.getUser(id);
        Payment payment = paymentService.getPayment(id);
        Shipping shipping = shippingService.getShipping(id);

        return buildOrder(user, payment, shipping);
    }
}
```

### 3. Procesamiento Paralelo de Datos
```java
public List<ProcessedData> processLargeDataset(List<Data> dataset) {
    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
        return dataset.stream()
            .map(data -> executor.submit(() -> processData(data)))
            .map(future -> {
                try {
                    return future.get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            })
            .toList();
    }
}
```

### 4. Web Scraping y Crawling
```java
public Map<String, String> scrapeWebsites(List<String> urls) {
    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
        Map<String, Future<String>> futures = new ConcurrentHashMap<>();

        // Crear un virtual thread por URL (incluso miles)
        for (String url : urls) {
            futures.put(url, executor.submit(() -> fetchWebpage(url)));
        }

        return futures.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    try {
                        return entry.getValue().get();
                    } catch (Exception e) {
                        return "Error: " + e.getMessage();
                    }
                }
            ));
    }
}
```

## Comparación Detallada

### Rendimiento

| Aspecto | Platform Threads | Virtual Threads |
|---------|-----------------|-----------------|
| Memoria por thread | ~1 MB | ~1 KB |
| Límite práctico | Miles | Millones |
| Tiempo de creación | Milisegundos | Microsegundos |
| Context switch | Costoso (kernel) | Muy barato (JVM) |
| Bloques I/O | Bloquea OS thread | Solo bloquea virtual thread |

### Código de Ejemplo: Servidor Web

#### Con Platform Threads
```java
// Pool limitado: 200 threads para 10,000 requests concurrentes
ExecutorService executor = Executors.newFixedThreadPool(200);

// Request 201+ esperan en cola → latencia alta
public Response handleRequest(Request req) {
    return executor.submit(() -> {
        String data = blockingDatabaseCall();  // Bloquea OS thread
        return processData(data);
    }).get();
}
```

#### Con Virtual Threads
```java
// Sin límite: un virtual thread por request
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

// Cada request tiene su propio thread → latencia baja
public Response handleRequest(Request req) {
    return executor.submit(() -> {
        String data = blockingDatabaseCall();  // Solo bloquea virtual thread
        return processData(data);
    }).get();
}
```

## Características Técnicas

### 1. Mounting y Unmounting
```java
// Virtual threads se "montan" en carrier threads (platform threads)
Thread.startVirtualThread(() -> {
    System.out.println("Running on: " + Thread.currentThread());

    // Cuando hace I/O, se "desmonta" del carrier thread
    Thread.sleep(1000);  // ← Aquí el carrier thread queda libre

    // Al terminar I/O, se "monta" en otro carrier thread (puede ser diferente)
    System.out.println("After I/O: " + Thread.currentThread());
});
```

### 2. Identificación de Virtual Threads
```java
public void processTask() {
    Thread current = Thread.currentThread();

    if (current.isVirtual()) {
        System.out.println("Running on virtual thread: " + current.getName());
    } else {
        System.out.println("Running on platform thread: " + current.getName());
    }
}
```

### 3. Integración con API Existente
```java
// Virtual threads implementan la misma API que platform threads
Thread virtualThread = Thread.ofVirtual()
    .name("my-virtual-thread")
    .start(() -> {
        // Todo el código Thread existente funciona
        System.out.println("Thread name: " + Thread.currentThread().getName());
        Thread.sleep(100);
    });

virtualThread.join();  // API familiar
```

## Ejemplo Completo del Proyecto

```java
@Service
public class VirtualThreadService {

    private static final Logger log = LoggerFactory.getLogger(VirtualThreadService.class);

    /**
     * Ejecuta miles de tareas concurrentemente con virtual threads
     */
    public List<TaskResult> executeConcurrentTasks(int numberOfTasks) {
        log.info("Executing {} tasks with Virtual Threads", numberOfTasks);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Callable<TaskResult>> tasks = new ArrayList<>();

            // Crear miles o millones de tareas sin problema
            for (int i = 0; i < numberOfTasks; i++) {
                final int taskId = i;
                tasks.add(() -> executeTask(taskId));
            }

            // Ejecutar todas en paralelo
            List<Future<TaskResult>> futures = executor.invokeAll(tasks);

            // Recolectar resultados
            List<TaskResult> results = new ArrayList<>();
            for (Future<TaskResult> future : futures) {
                results.add(future.get());
            }

            return results;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error executing concurrent tasks", e);
        }
    }

    /**
     * Benchmark: Virtual Threads vs Platform Threads
     */
    public BenchmarkResult benchmark(int numberOfTasks) {
        // Test con Virtual Threads
        long virtualStart = System.currentTimeMillis();
        List<TaskResult> virtualResults = executeConcurrentTasks(numberOfTasks);
        long virtualDuration = System.currentTimeMillis() - virtualStart;

        // Test con Platform Threads (pool limitado)
        long platformStart = System.currentTimeMillis();
        List<TaskResult> platformResults = executeWithPlatformThreads(numberOfTasks);
        long platformDuration = System.currentTimeMillis() - platformStart;

        double speedup = (double) platformDuration / virtualDuration;

        return new BenchmarkResult(
            numberOfTasks,
            virtualDuration,
            platformDuration,
            speedup
        );
    }

    private TaskResult executeTask(int taskId) {
        LocalDateTime startTime = LocalDateTime.now();
        Thread currentThread = Thread.currentThread();

        try {
            // Simular operación I/O bloqueante
            Thread.sleep(100);

            LocalDateTime endTime = LocalDateTime.now();
            String result = String.format("Task %d completed", taskId);

            return TaskResult.success(
                taskId,
                currentThread.getName(),
                currentThread.isVirtual(),  // ✅ Siempre true con virtual threads
                startTime,
                endTime,
                result
            );
        } catch (InterruptedException e) {
            LocalDateTime endTime = LocalDateTime.now();
            return TaskResult.failure(taskId, currentThread.getName(),
                currentThread.isVirtual(), startTime, endTime, e.getMessage());
        }
    }
}
```

## Buenas Prácticas

### ✅ DO: Usar para I/O Bound

```java
// ✅ CORRECTO: Operaciones I/O
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(() -> {
        String data = httpClient.get("/api/data");  // I/O
        database.save(data);                        // I/O
        sendEmail(data);                            // I/O
    });
}
```

### ❌ DON'T: Usar para CPU Bound

```java
// ❌ INCORRECTO: Operaciones intensivas de CPU
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(() -> {
        // Cálculos intensivos no se benefician de virtual threads
        for (int i = 0; i < 1_000_000_000; i++) {
            result += Math.sqrt(i);  // CPU bound
        }
    });
}

// ✅ MEJOR: Usar platform threads o ForkJoinPool para CPU bound
ForkJoinPool.commonPool().submit(() -> {
    // Operaciones CPU intensivas
});
```

### ✅ DO: Evitar Thread Pools Limitados

```java
// ✅ CORRECTO: Dejar que virtual threads escalen libremente
Executors.newVirtualThreadPerTaskExecutor()

// ❌ INCORRECTO: Limitar virtual threads pierde el beneficio
Executors.newFixedThreadPool(1000)  // ← No hacer esto con virtual threads
```

### ✅ DO: Usar try-with-resources

```java
// ✅ CORRECTO: Auto-cierre del executor
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    // Tareas...
}  // ← Automáticamente espera a que terminen todas las tareas

// ❌ INCORRECTO: Olvidar cerrar
var executor = Executors.newVirtualThreadPerTaskExecutor();
// ... usar executor ...
// ← Fuga de recursos si no se cierra
```

### ⚠️ CUIDADO: Operaciones Sincronizadas

```java
// ⚠️ PRECAUCIÓN: synchronized puede causar "pinning"
synchronized (lock) {
    // Si hay I/O aquí, el virtual thread queda "pinned" al carrier thread
    blockingOperation();  // ← Reduce el beneficio de virtual threads
}

// ✅ MEJOR: Usar ReentrantLock
ReentrantLock lock = new ReentrantLock();
lock.lock();
try {
    blockingOperation();  // Sin pinning
} finally {
    lock.unlock();
}
```

## Cuándo NO Usar Virtual Threads

1. **Operaciones CPU-intensive**: Si el código hace muchos cálculos sin I/O, platform threads o ForkJoinPool son mejores

2. **ThreadLocal intensivo**: Virtual threads + ThreadLocal puede consumir mucha memoria si se crean millones

3. **Código con synchronized extensivo**: Puede causar "pinning" y perder el beneficio

4. **Proyectos legacy con recursos muy limitados**: En casos muy específicos donde la JVM tiene restricciones extremas

## Migración desde Platform Threads

```java
// ANTES
ExecutorService executor = Executors.newFixedThreadPool(200);

// DESPUÉS (cambio simple)
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

// El resto del código permanece igual ✅
```

## Conclusión

Virtual Threads representa un cambio de paradigma en la programación concurrente de Java:

- **Simplicidad**: Código síncrono fácil de leer y mantener
- **Escalabilidad**: Millones de threads sin problema
- **Rendimiento**: Ideal para aplicaciones I/O bound
- **Compatibilidad**: Se integra con APIs existentes

Es la solución que Java necesitaba para competir con lenguajes como Go en aplicaciones altamente concurrentes, sin sacrificar la simplicidad del código.

## Referencias

- **JEP 444**: Virtual Threads
- **Project Loom**: https://openjdk.org/projects/loom/
- **Documentación oficial**: https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html

## Ejemplos en el Proyecto

- Implementación: `src/main/java/com/monghit/java21test/features/virtualthreads/`
- Tests: `src/test/java/com/monghit/java21test/features/virtualthreads/`
- Endpoints: `/api/virtual-threads/**`
