# Unnamed Patterns and Variables

## Descripción

Unnamed Patterns and Variables (también conocido como Unnamed Variables) es una feature de Java 21 que permite usar el guión bajo (`_`) para indicar explícitamente que una variable o patrón no se utilizará. Esto mejora la legibilidad del código al hacer evidente la intención de ignorar ciertos valores.

Esta característica es una Preview Feature en Java 21, lo que significa que puede evolucionar en futuras versiones.

## Problema que Resuelve

### Antes de Java 21

En versiones anteriores, cuando no necesitabas usar una variable, tenías que:

#### 1. Crear Variables que Nunca Usas

```java
// ❌ Problema: Variables declaradas pero nunca usadas
try {
    int result = riskyOperation();
    return "Success";
} catch (IOException e) {  // 'e' nunca se usa
    return "Failed";
}

// Warnings del compilador:
// "Variable 'e' is never used"
```

#### 2. Nombres de Variables Confusos

```java
// ❌ Problema: Nombres genéricos sin significado
for (String item : items) {  // ¿Usamos 'item'?
    counter++;  // No, solo contamos
}

// O peor aún
for (String ignored : items) {
    counter++;
}
```

#### 3. Pattern Matching con Valores Ignorados

```java
// ❌ Problema: Variables en patterns que no se usan
if (obj instanceof Point(int x, int y)) {  // Solo usamos 'x'
    return x * 2;  // 'y' nunca se usa
}
```

#### 4. Lambdas con Parámetros Ignorados

```java
// ❌ Problema: Parámetros de lambda no usados
map.forEach((key, value) -> {  // Solo usamos 'value'
    System.out.println(value);  // 'key' nunca se usa
});
```

### Después de Java 21

```java
// ✅ Solución: Usar _ para indicar intención explícita de ignorar

// Excepciones
try {
    int result = riskyOperation();
    return "Success";
} catch (IOException _) {  // ✓ Claramente no nos importa la excepción
    return "Failed";
}

// Loops
for (String _ : items) {  // ✓ Claramente solo queremos iterar
    counter++;
}

// Pattern matching
if (obj instanceof Point(int x, int _)) {  // ✓ Ignoramos 'y' explícitamente
    return x * 2;
}

// Lambdas
map.forEach((_, value) -> {  // ✓ Ignoramos la key explícitamente
    System.out.println(value);
});
```

**Ventajas:**
- Intención explícita y clara
- Sin warnings del compilador
- Código más limpio y mantenible
- Se lee mejor: "este valor no importa"

## Casos de Uso

### 1. Exception Handling

Cuando capturamos una excepción pero no necesitamos sus detalles:

```java
/**
 * Intento de operación sin necesidad de detalles de error
 */
public boolean tryOperation() {
    try {
        performRiskyOperation();
        return true;
    } catch (Exception _) {
        // No necesitamos la excepción, solo sabemos que falló
        return false;
    }
}

/**
 * Múltiples excepciones que manejamos igual
 */
public String safeRead(String filename) {
    try {
        return Files.readString(Path.of(filename));
    } catch (IOException | SecurityException _) {
        // Ambas excepciones se manejan igual, detalles no importan
        return DEFAULT_CONTENT;
    }
}

/**
 * Try-with-resources donde no usamos el recurso directamente
 */
public void lockAndExecute(Runnable task) {
    try (var _ = acquireLock()) {  // Solo necesitamos el lock, no el objeto
        task.run();
    }  // Lock se libera automáticamente
}
```

### 2. For Loops

Cuando solo necesitamos iterar N veces o contar:

```java
/**
 * Iterar N veces sin usar el elemento
 */
public void repeatNTimes(int n, Runnable action) {
    for (int _ : IntStream.range(0, n).boxed().toList()) {
        action.run();
    }
}

/**
 * Contar elementos sin procesarlos
 */
public int countItems(Collection<?> items) {
    int count = 0;
    for (var _ : items) {
        count++;
    }
    return count;
}

/**
 * Esperar / delay con iteraciones
 */
public void busyWait(int iterations) {
    for (int _ : IntStream.range(0, iterations).boxed().toList()) {
        // Simular trabajo
        Thread.onSpinWait();
    }
}

/**
 * Calentar cache (warmup)
 */
public void warmupCache(List<String> keys, Cache cache) {
    // Primera pasada: cargar en cache
    for (var _ : keys) {
        cache.load();
    }

    // Segunda pasada: solo importa que se acceda
    for (var _ : keys) {
        cache.access();
    }
}
```

### 3. Pattern Matching

Cuando solo necesitas algunos componentes de un record:

```java
public record Point3D(double x, double y, double z) {}
public record Employee(String name, int age, String department, double salary) {}
public record DataTuple(String name, String ignoredField, int value) {}

/**
 * Extraer solo coordenada X
 */
public double getX(Object obj) {
    if (obj instanceof Point3D(double x, double _, double _)) {
        return x;  // Solo necesitamos x
    }
    throw new IllegalArgumentException("Not a Point3D");
}

/**
 * Procesar solo nombre y salario
 */
public String formatSalaryInfo(Object obj) {
    return switch (obj) {
        case Employee(String name, int _, String _, double salary) ->
            String.format("%s earns $%.2f", name, salary);
        default ->
            "Unknown";
    };
}

/**
 * Ignorar campos intermedios en tuplas
 */
public ProcessResult processTuple(DataTuple tuple) {
    return switch (tuple) {
        case DataTuple(String name, String _, int value) when value > 100 ->
            new ProcessResult(name, value, "High");
        case DataTuple(String name, String _, int value) ->
            new ProcessResult(name, value, "Normal");
    };
}

/**
 * Extraer solo tipo de figura, no dimensiones
 */
public String getShapeType(Object shape) {
    return switch (shape) {
        case Circle(double _) -> "Circle";
        case Rectangle(double _, double _) -> "Rectangle";
        case Triangle(double _, double _, double _) -> "Triangle";
        default -> "Unknown";
    };
}
```

### 4. Lambdas y Method References

Cuando un parámetro de lambda no se usa:

```java
/**
 * Procesar solo valores de un map
 */
public void processValues(Map<String, Data> map) {
    map.forEach((_, value) -> {
        process(value);  // Key no nos importa
    });
}

/**
 * Stream operations con múltiples parámetros
 */
public List<String> getNames(List<Employee> employees) {
    return employees.stream()
        .map((Employee emp) -> emp.name())  // Solo necesitamos el nombre
        .toList();

    // Con unnamed variable (si fuera BiFunction)
    // .map((_, name) -> name)  // Hipotético
}

/**
 * Completable futures - solo éxito/fallo
 */
public CompletableFuture<Void> processAsync(String data) {
    return CompletableFuture.supplyAsync(() -> heavyComputation(data))
        .thenAccept(_ -> {
            // No necesitamos el resultado, solo ejecutar algo al terminar
            notifyCompletion();
        })
        .exceptionally(_ -> {
            // No necesitamos la excepción, solo manejar el fallo
            notifyError();
            return null;
        });
}
```

### 5. Switch Expressions

Cuando el tipo importa pero no el valor:

```java
/**
 * Determinar categoría sin usar el valor específico
 */
public String categorize(Object obj) {
    return switch (obj) {
        case String _ -> "Text";
        case Integer _ -> "Number";
        case Double _ -> "Decimal";
        case Boolean _ -> "Flag";
        case null -> "Null";
        default -> "Other";
    };
}

/**
 * Validar tipo sin procesar contenido
 */
public boolean isNumeric(Object value) {
    return switch (value) {
        case Integer _, Long _, Double _, Float _ -> true;
        default -> false;
    };
}

/**
 * Router de comandos por tipo
 */
public void handleCommand(Command cmd) {
    switch (cmd) {
        case StartCommand _ -> startService();
        case StopCommand _ -> stopService();
        case RestartCommand _ -> restartService();
        case StatusCommand _ -> reportStatus();
    }
}
```

## Ejemplo Completo del Proyecto

```java
@Service
public class UnnamedPatternService {

    private static final Logger log = LoggerFactory.getLogger(UnnamedPatternService.class);

    /**
     * Uso de unnamed variables en exception handling
     */
    public String demonstrateUnnamedInException() {
        log.info("Demonstrating unnamed variables in exception handling");

        try {
            int result = 10 / 0;
            return "Success";
        } catch (ArithmeticException _) {
            // ✓ Usamos _ porque no necesitamos la excepción
            return "Caught ArithmeticException (exception details not needed)";
        }
    }

    /**
     * Uso de unnamed variables en loops
     */
    public int demonstrateUnnamedInLoop() {
        log.info("Demonstrating unnamed variables in loops");

        int count = 0;
        // ✓ Usar _ cuando no necesitamos el valor del elemento
        for (var _ : List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)) {
            count++;
        }
        return count;
    }

    /**
     * Uso de unnamed patterns en switch
     */
    public String demonstrateUnnamedInSwitch(Object obj) {
        return switch (obj) {
            case String _ -> "It's a string (value not needed)";
            case Integer _ -> "It's an integer (value not needed)";
            default -> "It's something else";
        };
    }

    /**
     * Procesar tupla ignorando campos con unnamed patterns
     */
    public ProcessResult processTuple(DataTuple tuple) {
        log.info("Processing tuple with unnamed patterns");

        // ✓ Usar unnamed pattern para valores que no necesitamos
        return switch (tuple) {
            case DataTuple(String name, String _, int value) when value > 100 ->
                new ProcessResult(name, value, "High value");
            case DataTuple(String name, String _, int value) ->
                new ProcessResult(name, value, "Normal value");
        };
    }

    public record DataTuple(String name, String ignoredField, int value) {}
    public record ProcessResult(String name, int value, String category) {}
}
```

## Comparación Detallada

### Antes vs Después

```java
// ============ EXCEPCIONES ============

// ❌ ANTES: Variable no usada genera warning
try {
    doSomething();
} catch (IOException e) {  // ← Warning: 'e' is never used
    return "Error";
}

// ✅ DESPUÉS: Intención clara sin warnings
try {
    doSomething();
} catch (IOException _) {  // ✓ Claramente no nos importa
    return "Error";
}

// ============ LOOPS ============

// ❌ ANTES: Variable confusa
for (String ignored : items) {
    counter++;
}

// ✅ DESPUÉS: Intención obvia
for (String _ : items) {
    counter++;
}

// ============ PATTERN MATCHING ============

// ❌ ANTES: Variables declaradas pero no usadas
if (point instanceof Point(int x, int y)) {  // ← Warning: 'y' is never used
    return x;
}

// ✅ DESPUÉS: Explícitamente ignorado
if (point instanceof Point(int x, int _)) {  // ✓ Claramente ignoramos y
    return x;
}

// ============ LAMBDAS ============

// ❌ ANTES: Parámetro no usado
map.forEach((key, value) -> {  // ← Warning: 'key' is never used
    System.out.println(value);
});

// ✅ DESPUÉS: Intención explícita
map.forEach((_, value) -> {  // ✓ Claramente ignoramos key
    System.out.println(value);
});
```

## Combinación con Otras Features

### 1. Unnamed Variables + Pattern Matching

```java
// Extraer solo algunos campos de records complejos
public record Order(String id, Customer customer, List<Item> items, Payment payment) {}
public record Customer(String name, String email, Address address) {}

public String getCustomerName(Order order) {
    if (order instanceof Order(String _, Customer(String name, String _, Address _), List _, Payment _)) {
        return name;  // Solo nos interesa el nombre
    }
    return "Unknown";
}
```

### 2. Unnamed Variables + Sealed Types

```java
public sealed interface Result permits Success, Warning, Error {}
public record Success(String data) implements Result {}
public record Warning(String message) implements Result {}
public record Error(String message, Exception cause) implements Result {}

// Solo nos importa el tipo, no el contenido
public boolean isOk(Result result) {
    return switch (result) {
        case Success _ -> true;
        case Warning _, Error _ -> false;
    };
}
```

### 3. Unnamed Variables + Try-with-Resources

```java
// Lock pattern
public void synchronized Operation(Runnable task) {
    try (var _ = acquireLock()) {
        task.run();
    }  // Lock se libera automáticamente
}

// Timing pattern
public long measureTime(Runnable task) {
    long start = System.currentTimeMillis();
    try (var _ = startMeasurement()) {
        task.run();
    }
    return System.currentTimeMillis() - start;
}
```

## Buenas Prácticas

### ✅ DO: Usar para Claridad de Intención

```java
// ✅ CORRECTO: Deja claro que el valor no importa
for (var _ : List.of(1, 2, 3)) {
    System.out.println("Iteration");
}

// ❌ EVITAR: Variable con nombre genérico
for (var ignored : List.of(1, 2, 3)) {
    System.out.println("Iteration");
}
```

### ✅ DO: Usar en Pattern Matching Parcial

```java
// ✅ CORRECTO: Solo necesitamos 'name' y 'age'
if (person instanceof Person(String name, int age, Address _, Department _)) {
    return String.format("%s is %d years old", name, age);
}
```

### ✅ DO: Usar en Exception Handling Simple

```java
// ✅ CORRECTO: Solo nos importa que falló
try {
    riskyOperation();
    return true;
} catch (Exception _) {
    return false;
}

// ❌ EVITAR: Si necesitas la excepción, no uses _
try {
    riskyOperation();
} catch (Exception _) {
    log.error("Error: " + _);  // ← Error! No puedes usar _
}
```

### ⚠️ CUIDADO: No Abusar

```java
// ⚠️ EVITAR: Demasiados _ hace difícil de leer
if (obj instanceof ComplexRecord(String _, int _, double _, boolean _, long _)) {
    // Quizás necesitas replantear tu diseño
}

// ✅ MEJOR: Si ignoras todo, quizás solo necesitas el tipo
if (obj instanceof ComplexRecord _) {
    // Más claro si solo importa el tipo
}
```

### ⚠️ CUIDADO: Mantener Legibilidad

```java
// ⚠️ Puede ser confuso
for (var _ : _) {  // ← ¿Qué es esto?
    process();
}

// ✅ MEJOR: Conserva contexto
for (var _ : items) {  // ← Claro: iteramos items
    process();
}
```

## Limitaciones

1. **Preview Feature**: Requiere `--enable-preview` en Java 21

2. **No Reutilizable**: No puedes usar `_` como variable real en el mismo scope

3. **Solo para Ignorar**: Si necesitas el valor después, no uses `_`

4. **Múltiples _**: Puedes tener múltiples `_` en el mismo pattern

## Configuración Requerida

Para usar esta feature en Java 21, necesitas habilitar preview features:

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

```java
// También al ejecutar
java --enable-preview MyApp
```

## Conclusión

Unnamed Patterns and Variables es una mejora pequeña pero significativa para la legibilidad del código:

- **Claridad**: Intención explícita de ignorar valores
- **Limpieza**: Sin warnings del compilador
- **Expresividad**: El código se lee mejor
- **Mantenibilidad**: Otros desarrolladores entienden la intención

Aunque es una Preview Feature, es probable que se convierta en estándar en futuras versiones.

## Referencias

- **JEP 443**: Unnamed Patterns and Variables (Preview)
- **Documentación oficial**: https://openjdk.org/jeps/443

## Ejemplos en el Proyecto

- Implementación: `src/main/java/com/monghit/java21test/features/unnamedpatterns/`
- Tests: `src/test/java/com/monghit/java21test/features/unnamedpatterns/`
- Endpoints: `/api/unnamed-patterns/**`
