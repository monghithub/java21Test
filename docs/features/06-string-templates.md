# String Templates (Preview)

## Descripción

String Templates es una Preview Feature en Java 21 que permitiría interpolación de expresiones directamente en strings de manera segura y eficiente. Aunque está disponible como preview, muchas implementaciones optan por usar `String.format()` o text blocks como alternativa hasta que la feature se estabilice.

**IMPORTANTE**: Esta feature está en preview y puede cambiar en futuras versiones. Requiere `--enable-preview` para usarse.

## Problema que Resuelve

### Antes de Java 21

La composición de strings dinámicos era verbose y propensa a errores:

#### 1. Concatenación Manual

```java
// ❌ Problema: Verbose, difícil de leer, propenso a errores
String name = "Alice";
int age = 30;
String city = "New York";

String message = "User " + name + " is " + age + " years old and lives in " + city;
// Resultado: "User Alice is 30 years old and lives in New York"

// ❌ Con múltiples líneas es peor
String report = "==================== REPORT ====================\n" +
                "Name: " + name + "\n" +
                "Age: " + age + "\n" +
                "City: " + city + "\n" +
                "================================================\n";
```

**Problemas:**
- Muy verbose
- Fácil olvidar espacios
- Difícil de mantener
- Errores tipográficos comunes

#### 2. String.format()

```java
// ❌ Problema: Separación entre formato y valores
String message = String.format("User %s is %d years old and lives in %s",
    name, age, city);

// ❌ Errores de tipo no detectados en compile-time
String wrong = String.format("Age: %s", age);  // ❌ Debería ser %d para int
// Falla en runtime con IllegalFormatConversionException

// ❌ Orden de parámetros fácil de equivocar
String report = String.format(
    "Name: %s, Age: %d, City: %s",
    age,    // ❌ Orden incorrecto - error en runtime
    name,
    city
);
```

#### 3. StringBuilder

```java
// ❌ Problema: Muy verbose para casos simples
StringBuilder sb = new StringBuilder();
sb.append("User ");
sb.append(name);
sb.append(" is ");
sb.append(age);
sb.append(" years old and lives in ");
sb.append(city);
String message = sb.toString();
```

#### 4. Text Blocks (Java 15+)

```java
// Mejora pero sin interpolación real
String report = """
    ==================== REPORT ====================
    Name: %s
    Age: %d
    City: %s
    ================================================
    """.formatted(name, age, city);

// O con String.format
String report = String.format("""
    ==================== REPORT ====================
    Name: %s
    Age: %d
    City: %s
    ================================================
    """, name, age, city);
```

### Con String Templates (Preview en Java 21)

```java
// ✅ Solución propuesta: Interpolación directa y type-safe
String message = STR."User \{name} is \{age} years old and lives in \{city}";

// ✅ Con text blocks
String report = STR."""
    ==================== REPORT ====================
    Name: \{name}
    Age: \{age}
    City: \{city}
    ================================================
    """;

// ✅ Con expresiones
String info = STR."User \{user.name()} has \{user.orders().size()} orders";

// ✅ Type-safe en compile-time
String calculation = STR."Result: \{2 + 2}";  // "Result: 4"
```

**Ventajas propuestas:**
- Interpolación directa
- Type-safe (verificado en compile-time)
- Expresiones complejas permitidas
- Más legible y mantenible

## Estado Actual en Java 21

**NOTA IMPORTANTE**: String Templates está en preview y no está completamente estable. El proyecto usa `String.format()` como alternativa práctica.

### Alternativa Actual: String.format() con Locale

```java
// Implementación actual usando String.format()
@Service
public class StringTemplateService {

    /**
     * Genera un reporte formateado
     */
    public String generateReport(ReportData data) {
        // Usando String.format con Locale.US para consistencia
        return String.format(Locale.US, """
            ==================== REPORT ====================
            Title: %s
            Author: %s
            Date: %s
            Total: $%.2f
            Items: %d
            ================================================
            """, data.title(), data.author(), data.date(), data.total(), data.items());
    }

    /**
     * Genera SQL query (en producción usar PreparedStatement)
     */
    public String buildQuery(String table, String column, String value) {
        return String.format(
            "SELECT * FROM %s WHERE %s = '%s'",
            table, column, value
        );
    }

    /**
     * Crea respuesta JSON formateada
     */
    public String createJsonResponse(String name, int age, String city) {
        return String.format("""
            {
              "name": "%s",
              "age": %d,
              "city": "%s",
              "timestamp": "%s"
            }
            """, name, age, city, LocalDateTime.now());
    }

    public record ReportData(
        String title,
        String author,
        String date,
        double total,
        int items
    ) {}
}
```

## Casos de Uso (Cuando String Templates se Estabilice)

### 1. Mensajes de Usuario

```java
// Con STR template processor (preview)
public String welcomeMessage(User user) {
    return STR."Welcome back, \{user.name()}! You have \{user.notifications()} new notifications.";
}

// Alternativa actual
public String welcomeMessage(User user) {
    return String.format("Welcome back, %s! You have %d new notifications.",
        user.name(), user.notifications());
}
```

### 2. Logging

```java
// Con STR template
log.info(STR."Processing order \{orderId} for customer \{customer.name()} (total: $\{order.total()})");

// Alternativa actual
log.info("Processing order {} for customer {} (total: ${})",
    orderId, customer.name(), order.total());
```

### 3. SQL Building (con FMT processor para escape)

```java
// Con FMT template processor (escaparía automáticamente)
String query = FMT."SELECT * FROM users WHERE name = \{userName} AND age > \{minAge}";

// Alternativa actual (PreparedStatement en producción)
String query = String.format("SELECT * FROM users WHERE name = '%s' AND age > %d",
    userName, minAge);
```

### 4. HTML/JSON Generation

```java
// Con STR template
String html = STR."""
    <div class="user-card">
        <h2>\{user.name()}</h2>
        <p>Email: \{user.email()}</p>
        <p>Member since: \{user.joinDate()}</p>
    </div>
    """;

// Alternativa actual
String html = String.format("""
    <div class="user-card">
        <h2>%s</h2>
        <p>Email: %s</p>
        <p>Member since: %s</p>
    </div>
    """, user.name(), user.email(), user.joinDate());
```

### 5. Reportes Complejos

```java
// Con STR template
String report = STR."""
    ==================== SALES REPORT ====================
    Period: \{startDate} to \{endDate}
    Total Sales: $\{String.format("%.2f", totalSales)}
    Number of Orders: \{orderCount}
    Average Order Value: $\{String.format("%.2f", totalSales / orderCount)}
    Top Product: \{topProduct.name()} (\{topProduct.quantity()} sold)
    ======================================================
    """;
```

## Template Processors (Preview)

### STR - Standard Interpolation

```java
// Interpolación directa
String result = STR."2 + 2 = \{2 + 2}";  // "2 + 2 = 4"

// Con métodos
String greeting = STR."Hello, \{user.name().toUpperCase()}!";

// Con operaciones
String info = STR."You have \{messages.size()} message\{messages.size() != 1 ? "s" : ""}";
```

### FMT - Formatted Interpolation

```java
// Con formato específico
String formatted = FMT."Value: %.2f\{value}";

// Combinando formatos
String complex = FMT."""
    Name: %-20s\{name}
    Age: %03d\{age}
    Balance: $%,.2f\{balance}
    """;
```

### Custom Template Processors

```java
// Procesador personalizado para SQL seguro
String query = SQL."SELECT * FROM users WHERE id = \{userId} AND status = \{status}";
// Procesador SQL automáticamente escapa valores y previene SQL injection
```

## Comparación: String.format() vs String Templates

| Aspecto | String.format() | String Templates (Preview) |
|---------|----------------|----------------------------|
| Sintaxis | Separada del texto | Inline interpolación |
| Type Safety | Runtime | Compile-time |
| Legibilidad | Buena | Excelente |
| Orden de params | Puede confundirse | Obvio por posición |
| Expresiones | No | Sí |
| Estabilidad | ✓ Estable | ⚠️ Preview |

## Ejemplo Comparativo Completo

```java
// Datos de ejemplo
String name = "Alice";
int age = 30;
double salary = 75000.50;
List<String> skills = List.of("Java", "Python", "SQL");

// ======= String.format() (ESTABLE) =======
String report1 = String.format("""
    Employee: %s
    Age: %d years
    Salary: $%.2f
    Skills: %s
    Total Skills: %d
    """, name, age, salary, String.join(", ", skills), skills.size());

// ======= String Templates (PREVIEW) =======
String report2 = STR."""
    Employee: \{name}
    Age: \{age} years
    Salary: $\{String.format("%.2f", salary)}
    Skills: \{String.join(", ", skills)}
    Total Skills: \{skills.size()}
    """;

// ======= Text Blocks + formatted() (ESTABLE) =======
String report3 = """
    Employee: %s
    Age: %d years
    Salary: $%.2f
    Skills: %s
    Total Skills: %d
    """.formatted(name, age, salary, String.join(", ", skills), skills.size());
```

## Buenas Prácticas (Cuando se Estabilice)

### ✅ DO: Usar para Composición Simple

```java
// ✅ CORRECTO: String templates para casos simples
String message = STR."Welcome, \{userName}!";

// ❌ INNECESARIO: Para strings sin interpolación
String message = STR."Welcome, stranger!";  // ← No necesita template
String message = "Welcome, stranger!";      // ✓ Mejor
```

### ✅ DO: Combinar con Text Blocks

```java
// ✅ CORRECTO: Text blocks + templates para multilinea
String email = STR."""
    Dear \{customer.name()},

    Your order #\{order.id()} has been shipped!

    Tracking number: \{order.trackingNumber()}

    Best regards,
    \{company.name()}
    """;
```

### ✅ DO: Usar Locale Apropiado

```java
// ✅ CORRECTO: Especificar locale para números
String price = STR."Price: \{String.format(Locale.US, "%.2f", amount)}";

// ❌ EVITAR: Depender de locale del sistema
String price = STR."Price: \{String.format("%.2f", amount)}";  // Puede dar "123,45" o "123.45"
```

### ⚠️ CUIDADO: Security con Entrada de Usuario

```java
// ⚠️ PELIGRO: SQL injection posible
String userInput = getUserInput();
String query = STR."SELECT * FROM users WHERE name = '\{userInput}'";
// ❌ Si userInput = "'; DROP TABLE users; --" → SQL injection!

// ✅ CORRECTO: Usar PreparedStatement
PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
stmt.setString(1, userInput);
```

### ⚠️ CUIDADO: No para HTML/JavaScript sin Escape

```java
// ⚠️ PELIGRO: XSS posible
String userInput = "<script>alert('XSS')</script>";
String html = STR."<div>\{userInput}</div>";
// ❌ Renderiza el script!

// ✅ CORRECTO: Escapar HTML
String html = STR."<div>\{escapeHtml(userInput)}</div>";
```

## Limitaciones Actuales

1. **Preview Feature**: No estable, puede cambiar

2. **Requiere --enable-preview**: Complejidad en build

3. **Performance**: Comparable a String.format(), no dramáticamente mejor

4. **No Lazy**: Las expresiones se evalúan inmediatamente

5. **Procesadores Custom**: API compleja para casos avanzados

## Alternativas Estables Actuales

Mientras String Templates madura, usa:

1. **String.format()** con Locale.US
2. **Text Blocks** (Java 15+) con `formatted()`
3. **StringBuilder** para construcción dinámica
4. **Libraries** como Apache Commons Text para casos complejos

## Configuración para Preview

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

## Conclusión

String Templates es una feature prometedora que:

- **Mejorará**: La legibilidad del código
- **Simplificará**: La composición de strings
- **Añadirá**: Type-safety en compile-time

Sin embargo, al estar en preview:

- **Espera** a que se estabilice para producción
- **Usa** String.format() o text blocks mientras tanto
- **Monitorea** las futuras versiones de Java para cambios

Es probable que se convierta en estándar en Java 22 o 23.

## Referencias

- **JEP 430**: String Templates (Preview)
- **JEP 459**: String Templates (Second Preview) - Java 22
- **Documentación oficial**: https://openjdk.org/jeps/430

## Ejemplos en el Proyecto

- Implementación: `src/main/java/com/monghit/java21test/features/stringtemplates/`
- Tests: `src/test/java/com/monghit/java21test/features/stringtemplates/`
- Endpoints: `/api/string-templates/**`

**Nota**: El proyecto usa `String.format()` como alternativa estable hasta que String Templates se gradúe de preview.
