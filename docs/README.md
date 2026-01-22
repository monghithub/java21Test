# Documentación de Features de Java 21

Este directorio contiene documentación exhaustiva de todas las nuevas características de Java 21 implementadas en este proyecto.

## Características Documentadas

### 1. 🚀 [Virtual Threads (Project Loom)](features/01-virtual-threads.md)

**Estado**: ✅ Estable (Feature estándar)

Threads ligeros que permiten crear millones de hilos con overhead mínimo, revolucionando la programación concurrente en Java.

**Problemas que resuelve:**
- Alto consumo de memoria con platform threads tradicionales
- Limitación de threads disponibles en aplicaciones de alto tráfico
- Complejidad de programación asíncrona con CompletableFuture/Reactive

**Casos de uso ideales:**
- Operaciones I/O bound (APIs REST, DB, servicios externos)
- Microservicios con alto volumen de requests
- Web scraping y crawling
- Procesamiento paralelo de datos

---

### 2. 🔀 [Pattern Matching for Switch](features/02-pattern-matching-switch.md)

**Estado**: ✅ Estable (Feature estándar)

Permite usar patrones complejos en expresiones switch, incluyendo type patterns, guarded patterns y deconstrucción.

**Problemas que resuelve:**
- Código verboso con múltiples instanceof y casting manual
- Switch tradicional limitado a primitivos y Strings
- Falta de exhaustividad en chequeos de tipos

**Casos de uso ideales:**
- Procesamiento de jerarquías de tipos (con sealed classes)
- Event handling y command routing
- Validación de datos con múltiples condiciones
- State machines

---

### 3. 📦 [Record Patterns](features/03-record-patterns.md)

**Estado**: ✅ Estable (Feature estándar)

Deconstrucción de records directamente en pattern matching, permitiendo extraer componentes incluso con anidación profunda.

**Problemas que resuelve:**
- Acceso verboso a campos de records anidados
- Múltiples variables intermedias innecesarias
- Código difícil de leer cuando se trabaja con DTOs complejos

**Casos de uso ideales:**
- Filtrado de colecciones por campos anidados
- Generación de reportes desde DTOs
- Validación de estructuras de datos complejas
- Comparación y transformación de objetos

---

### 4. 📚 [Sequenced Collections](features/04-sequenced-collections.md)

**Estado**: ✅ Estable (Feature estándar)

Nueva jerarquía de interfaces que proporciona API uniforme para colecciones con orden definido.

**Problemas que resuelve:**
- APIs inconsistentes para acceder al primer/último elemento
- Falta de métodos para agregar elementos al principio/final en Sets ordenados
- No había forma estándar de obtener vista reversa

**Casos de uso ideales:**
- Implementación de colas con acceso a ambos extremos
- Cache LRU (Least Recently Used)
- Sistemas de navegación (browser history)
- Leaderboards y rankings
- Undo/Redo stacks

---

### 5. ⚡ [Unnamed Patterns and Variables](features/05-unnamed-patterns-variables.md)

**Estado**: ⚠️ Preview Feature

Permite usar `_` para indicar explícitamente que una variable o patrón no se utilizará.

**Problemas que resuelve:**
- Variables declaradas pero nunca usadas (warnings del compilador)
- Nombres de variables confusos como "ignored", "unused"
- Código menos legible cuando ciertos valores no importan

**Casos de uso ideales:**
- Exception handling cuando no necesitas los detalles
- Loops que solo cuentan iteraciones
- Pattern matching cuando solo necesitas algunos campos
- Lambdas con parámetros no utilizados

---

### 6. 📝 [String Templates](features/06-string-templates.md)

**Estado**: ⚠️ Preview Feature

Interpolación de expresiones directamente en strings de manera segura y eficiente (actualmente usa String.format() como alternativa).

**Problemas que resuelve:**
- Concatenación manual verbose y propensa a errores
- String.format() con separación entre formato y valores
- Falta de type-safety en compile-time

**Casos de uso ideales:**
- Mensajes de usuario dinámicos
- Logging con contexto
- Generación de HTML/JSON/SQL
- Reportes complejos

**Nota**: El proyecto usa `String.format()` con `Locale.US` como alternativa estable.

---

### 7. 🌲 [Structured Concurrency](features/07-structured-concurrency.md)

**Estado**: ⚠️ Preview Feature

Manejo de tareas concurrentes como unidad estructurada, garantizando que todas completen o fallen juntas.

**Problemas que resuelve:**
- Thread leaks cuando tareas fallan
- Manejo de errores complejo en código concurrente
- Falta de propagación de cancelación
- Debugging difícil con tareas desconectadas

**Casos de uso ideales:**
- Agregación de datos de múltiples servicios
- Validación paralela de datos
- Fan-out/fan-in patterns
- Timeout handling coordinado

**Nota**: El proyecto usa `ExecutorService` con Virtual Threads como alternativa estable.

---

### 8. 🔐 [Scoped Values](features/08-scoped-values.md)

**Estado**: ⚠️ Preview Feature

Alternativa moderna a ThreadLocal con mejor performance e inmutabilidad garantizada.

**Problemas que resuelve:**
- Memory leaks con ThreadLocal
- Alto overhead de memoria con virtual threads
- Alcance no claro de datos compartidos
- Falta de inmutabilidad garantizada

**Casos de uso ideales:**
- Request context en aplicaciones web
- Transaction context en bases de datos
- Security context con permisos
- Logging context (trace/span IDs)
- Feature flags context

**Nota**: El proyecto usa `ThreadLocal` como alternativa estable.

---

## Estructura de la Documentación

Cada documento sigue la misma estructura:

1. **Descripción**: Qué es la feature y su propósito
2. **Problema que Resuelve**: Comparación "antes vs después" con ejemplos de código
3. **Características Principales**: Aspectos clave de la feature
4. **Casos de Uso**: Ejemplos prácticos y reales
5. **Ejemplo Completo del Proyecto**: Código del proyecto con explicaciones
6. **Comparación Detallada**: Tablas y ejemplos comparativos
7. **Buenas Prácticas**: DOs y DON'Ts con ejemplos
8. **Limitaciones**: Consideraciones y restricciones
9. **Conclusión**: Resumen de beneficios y recomendaciones
10. **Referencias**: Enlaces a JEPs y documentación oficial
11. **Ejemplos en el Proyecto**: Ubicación del código de implementación

## Features por Estado

### Estables (✅)
Listas para usar en producción sin configuración adicional:

- Virtual Threads
- Pattern Matching for Switch
- Record Patterns
- Sequenced Collections

### Preview (⚠️)
Requieren `--enable-preview` y pueden cambiar en futuras versiones:

- Unnamed Patterns and Variables
- String Templates
- Structured Concurrency (también requiere `jdk.incubator.concurrent`)
- Scoped Values

## Configuración de Preview Features

Para usar las preview features, añade a tu `pom.xml`:

```xml
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

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <argLine>--enable-preview</argLine>
    </configuration>
</plugin>
```

Y al ejecutar:

```bash
java --enable-preview YourApplication
```

## Navegación Rápida por Caso de Uso

### Para Aplicaciones Web/APIs
- [Virtual Threads](features/01-virtual-threads.md) - Manejar alto volumen de requests
- [Scoped Values](features/08-scoped-values.md) - Request/security context
- [Structured Concurrency](features/07-structured-concurrency.md) - Llamadas paralelas a servicios

### Para Procesamiento de Datos
- [Pattern Matching](features/02-pattern-matching-switch.md) - Routing y transformación
- [Record Patterns](features/03-record-patterns.md) - Filtrado y validación
- [Sequenced Collections](features/04-sequenced-collections.md) - Ordenamiento y ranking

### Para Mejorar Legibilidad
- [Unnamed Patterns](features/05-unnamed-patterns-variables.md) - Código más claro
- [String Templates](features/06-string-templates.md) - Mensajes y logging
- [Record Patterns](features/03-record-patterns.md) - Deconstrucción simple

### Para Concurrencia
- [Virtual Threads](features/01-virtual-threads.md) - Escalabilidad sin complejidad
- [Structured Concurrency](features/07-structured-concurrency.md) - Tareas coordinadas
- [Scoped Values](features/08-scoped-values.md) - Compartir contexto

## Código de Ejemplo

Todos los ejemplos están implementados y testeados en:

```
src/
├── main/java/com/monghit/java21test/features/
│   ├── virtualthreads/       # Virtual Threads
│   ├── patternmatching/      # Pattern Matching
│   ├── recordpatterns/       # Record Patterns
│   ├── sequencedcollections/ # Sequenced Collections
│   ├── unnamedpatterns/      # Unnamed Patterns
│   ├── stringtemplates/      # String Templates
│   ├── structuredconcurrency/# Structured Concurrency
│   └── scopedvalues/         # Scoped Values
└── test/java/...             # Tests completos para todas las features
```

## Endpoints REST

El proyecto expone endpoints REST para probar cada feature:

- `/api/virtual-threads/**` - Demos de Virtual Threads
- `/api/pattern-matching/**` - Demos de Pattern Matching
- `/api/record-patterns/**` - Demos de Record Patterns
- `/api/sequenced-collections/**` - Demos de Sequenced Collections
- `/api/unnamed-patterns/**` - Demos de Unnamed Patterns
- `/api/string-templates/**` - Demos de String Templates
- `/api/structured-concurrency/**` - Demos de Structured Concurrency
- `/api/scoped-values/**` - Demos de Scoped Values

Accede a Swagger UI en: `http://localhost:8080/swagger-ui.html`

## Recursos Adicionales

### Documentación Oficial
- [Java 21 Release Notes](https://www.oracle.com/java/technologies/javase/21-relnotes.html)
- [JDK Enhancement Proposals (JEPs)](https://openjdk.org/jeps/0)
- [Java SE 21 Javadoc](https://docs.oracle.com/en/java/javase/21/docs/api/)

### JEPs Implementados
- JEP 444: Virtual Threads
- JEP 441: Pattern Matching for switch
- JEP 440: Record Patterns
- JEP 431: Sequenced Collections
- JEP 443: Unnamed Patterns and Variables (Preview)
- JEP 430: String Templates (Preview)
- JEP 453: Structured Concurrency (Preview)
- JEP 446: Scoped Values (Preview)

## Contribuir

Para añadir o mejorar la documentación:

1. Sigue la estructura establecida en los documentos existentes
2. Incluye ejemplos de código funcionales
3. Añade comparaciones "antes vs después"
4. Documenta casos de uso prácticos
5. Incluye referencias a JEPs relevantes

## Licencia

Este proyecto y su documentación están bajo la misma licencia que el proyecto principal.

---

**Última actualización**: 2026-01-22

**Versión de Java**: 21.0.5

**Spring Boot**: 3.2.1
