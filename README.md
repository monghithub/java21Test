# Java 21 Features Testing

Proyecto completo de Spring Boot para testear y demostrar todas las funcionalidades nuevas de Java 21.

## Características Implementadas

### 1. Virtual Threads (Project Loom) ✅
Hilos virtuales que permiten escalabilidad masiva con overhead mínimo.

**Endpoints:**
- `POST /api/virtual-threads/execute-tasks` - Ejecuta múltiples tareas concurrentes
- `GET /api/virtual-threads/benchmark` - Compara rendimiento con platform threads
- `POST /api/virtual-threads/simulate-blocking-io` - Simula operaciones I/O bloqueantes
- `GET /api/virtual-threads/current-thread-info` - Información del thread actual

### 2. Pattern Matching for Switch ✅
Coincidencia de patrones mejorada en switch statements con guards.

**Endpoints:**
- `POST /api/pattern-matching/calculate-area` - Calcula área según tipo de forma
- `POST /api/pattern-matching/describe-shape` - Describe formas con pattern matching
- `POST /api/pattern-matching/validate-shape` - Valida formas con guards
- `POST /api/pattern-matching/categorize` - Categoriza formas por tamaño
- `POST /api/pattern-matching/compare` - Compara dos formas

### 3. Record Patterns ✅
Deconstrucción de records en pattern matching, incluyendo records anidados.

**Endpoints:**
- `POST /api/record-patterns/analyze-employee` - Analiza empleado con record patterns
- `POST /api/record-patterns/extract-info` - Extrae información anidada
- `POST /api/record-patterns/filter-by-city` - Filtra empleados por ciudad
- `POST /api/record-patterns/generate-report` - Genera reporte detallado
- `POST /api/record-patterns/compare` - Compara dos empleados

### 4. String Templates (Preview) ✅
Interpolación de strings mejorada (implementado con String.format).

**Endpoints:**
- `POST /api/string-templates/format-report` - Genera reportes formateados
- `POST /api/string-templates/sql-query` - Genera queries SQL
- `POST /api/string-templates/json-builder` - Construye JSON

### 5. Sequenced Collections ✅
Nuevas interfaces para colecciones ordenadas con métodos addFirst/addLast/getFirst/getLast/reversed.

**Endpoints:**
- `GET /api/sequenced/list` - Demuestra SequencedCollection con List
- `GET /api/sequenced/set` - Demuestra SequencedSet
- `GET /api/sequenced/map` - Demuestra SequencedMap

### 6. Unnamed Patterns and Variables ✅
Uso de `_` para valores ignorados en pattern matching y variables.

**Endpoints:**
- `GET /api/unnamed/exception` - Demuestra unnamed en excepciones
- `GET /api/unnamed/loop` - Demuestra unnamed en loops
- `POST /api/unnamed/switch` - Demuestra unnamed en switch
- `POST /api/unnamed/process-tuple` - Procesa tuplas ignorando valores

### 7. Structured Concurrency (Preview) ✅
Concurrencia estructurada para mejor manejo de tareas paralelas (implementado con ExecutorService).

**Endpoints:**
- `GET /api/structured-concurrency/fetch-parallel` - Obtiene datos de múltiples fuentes en paralelo

### 8. Scoped Values (Preview) ✅
Alternativa moderna a ThreadLocal para compartir datos (implementado con ThreadLocal).

**Endpoints:**
- `POST /api/scoped-values/process-request` - Procesa request con contexto
- `POST /api/scoped-values/nested-scopes` - Demuestra scopes anidados

## Requisitos

- **Java 21** (JDK 21 o superior)
- **Maven 3.9+**
- **IDE compatible**: IntelliJ IDEA 2023.2+, Eclipse 2023-09+, o VS Code con Extension Pack for Java

## Instalación y Ejecución

### 1. Clonar el repositorio

```bash
git clone <repository-url>
cd java21Test
```

### 2. Configurar Java 21 con SDKMAN (recomendado)

Este proyecto incluye un archivo `.sdkmanrc` que configura automáticamente Java 21.

```bash
# Si no tienes SDKMAN instalado, instálalo primero:
# curl -s "https://get.sdkman.io" | bash

# Instalar las versiones especificadas en .sdkmanrc
sdk env install

# Activar el entorno (se hace automáticamente al entrar al directorio)
sdk env

# Verificar la versión de Java
java -version
```

**Nota:** Si usas SDKMAN, el proyecto cambiará automáticamente a Java 21 cuando entres al directorio.

### 3. Compilar el proyecto

```bash
mvn clean compile
```

### 3. Ejecutar tests

```bash
mvn test
```

### 4. Ejecutar la aplicación

**Opción 1: Usar script de arranque (recomendado)**
```bash
./start.sh
```

**Opción 2: Manualmente con Maven**
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="--enable-preview"
```

La aplicación estará disponible en: `http://localhost:8080/api`

### 5. Detener la aplicación

**Opción 1: Usar script de parada**
```bash
./stop.sh
```

**Opción 2: Ctrl+C** en la terminal donde corre la aplicación

**Opción 3: Matar el proceso manualmente**
```bash
# Encontrar el PID
ps aux | grep java21test

# Matar el proceso
kill <PID>
```

## Scripts de Gestión

El proyecto incluye scripts para facilitar la gestión:

### Script Principal: `manage.sh`

```bash
# Iniciar aplicación
./manage.sh start

# Detener aplicación
./manage.sh stop

# Reiniciar aplicación
./manage.sh restart

# Ver estado
./manage.sh status

# Ver logs en tiempo real
./manage.sh logs

# Ejecutar tests
./manage.sh test

# Compilar proyecto
./manage.sh compile
```

### Scripts Individuales

- **`start.sh`** - Inicia la aplicación con Java 21 y preview features
- **`stop.sh`** - Detiene la aplicación gracefully
- **`manage.sh`** - Script principal de gestión con múltiples comandos

## Documentación y Testing

### Swagger UI

Una vez iniciada la aplicación, accede a la documentación interactiva de la API:

**URL:** http://localhost:8080/api/swagger-ui.html

Desde Swagger UI puedes:
- Ver todos los endpoints disponibles
- Probar cada funcionalidad interactivamente
- Ver ejemplos de requests y responses
- Explorar los modelos de datos

### Actuator

Monitoreo y métricas de la aplicación:

**URL:** http://localhost:8080/api/actuator/health

Endpoints disponibles:
- `/actuator/health` - Estado de salud de la aplicación
- `/actuator/info` - Información de la aplicación
- `/actuator/metrics` - Métricas de rendimiento
- `/actuator/env` - Variables de entorno

## Configuración de Preview Features

Algunas features de Java 21 están en preview y requieren habilitación especial:

### En el pom.xml (ya configurado):

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <compilerArgs>
            <arg>--enable-preview</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

### Para ejecutar con preview features:

```bash
java --enable-preview -jar target/java21test-1.0.0-SNAPSHOT.jar
```

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/monghit/java21test/
│   │   ├── Java21TestApplication.java          # Clase principal
│   │   ├── config/
│   │   │   └── VirtualThreadConfig.java        # Configuración de Virtual Threads
│   │   ├── common/
│   │   │   ├── dto/ApiResponse.java            # DTO de respuesta estándar
│   │   │   └── exception/GlobalExceptionHandler.java
│   │   └── features/                           # Cada feature en su paquete
│   │       ├── virtualthreads/
│   │       ├── patternmatching/
│   │       ├── recordpatterns/
│   │       ├── stringtemplates/
│   │       ├── sequencedcollections/
│   │       ├── unnamedpatterns/
│   │       ├── structuredconcurrency/
│   │       └── scopedvalues/
│   └── resources/
│       ├── application.properties              # Configuración
│       └── banner.txt                          # Banner personalizado
└── test/
    └── java/com/monghit/java21test/
        ├── Java21TestApplicationTests.java    # Test principal
        └── features/                          # Tests por feature
```

## Ejemplos de Uso

### Virtual Threads

```bash
curl -X POST http://localhost:8080/api/virtual-threads/execute-tasks?numberOfTasks=100
```

### Pattern Matching

```bash
curl -X POST http://localhost:8080/api/pattern-matching/calculate-area \
  -H "Content-Type: application/json" \
  -d '{"type": "circle", "radius": 5.0}'
```

### Record Patterns

```bash
curl -X POST http://localhost:8080/api/record-patterns/analyze-employee \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "age": 30,
    "street": "123 Main St",
    "city": "New York",
    "zipCode": "10001",
    "country": "USA",
    "departmentName": "Engineering",
    "departmentCode": "ENG",
    "manager": "Jane Manager",
    "salary": 85000
  }'
```

### Sequenced Collections

```bash
curl -X GET http://localhost:8080/api/sequenced/list
```

## Tecnologías Utilizadas

- **Java 21** - Última versión LTS con todas las nuevas features
- **Spring Boot 3.2.1** - Framework de aplicación
- **Maven** - Gestión de dependencias
- **SpringDoc OpenAPI** - Documentación automática de API
- **JUnit 5** - Testing
- **AssertJ** - Assertions fluidas para tests
- **SLF4J + Logback** - Logging

## Features de Java 21 Destacadas

### Virtual Threads
Permite crear millones de threads con overhead mínimo. Ideal para aplicaciones I/O bound.

### Pattern Matching
Código más limpio y expresivo al trabajar con jerarquías de tipos.

### Record Patterns
Deconstrucción directa de records en pattern matching, incluyendo records anidados.

### Sequenced Collections
API unificada para trabajar con colecciones ordenadas.

## Tests

Ejecutar todos los tests:
```bash
mvn test
```

Ejecutar tests de una feature específica:
```bash
mvn test -Dtest=VirtualThreadServiceTest
mvn test -Dtest=PatternMatchingServiceTest
mvn test -Dtest=RecordPatternServiceTest
```

## Troubleshooting

### Error: "release version 21 not supported"

Esto significa que Maven no está usando Java 21. Soluciones:

**Opción 1: Usar SDKMAN (recomendado)**
```bash
sdk env install  # Instala Java 21
sdk env          # Activa Java 21
java -version    # Verifica que sea Java 21
mvn clean compile
```

**Opción 2: Cambiar JAVA_HOME manualmente**
```bash
export JAVA_HOME=/path/to/java21
export PATH=$JAVA_HOME/bin:$PATH
```

**Opción 3: Ver versiones de Java disponibles en SDKMAN**
```bash
sdk list java | grep 21
sdk install java 21.0.5-tem
sdk use java 21.0.5-tem
```

### Error: "preview features are disabled"

Asegúrate de que Maven esté usando Java 21 y que el plugin de compilación tenga `--enable-preview`:

```bash
java -version  # Debe mostrar Java 21
mvn -version   # Debe usar Java 21
```

### Error: "cannot find symbol"

Verifica que estés usando JDK 21 (no JRE) y que todas las dependencias se hayan descargado:

```bash
mvn clean install
```

## Contribuciones

Este es un proyecto educativo para demostrar las features de Java 21. Las contribuciones son bienvenidas para:
- Agregar más ejemplos de uso
- Mejorar la documentación
- Agregar tests adicionales
- Optimizar implementaciones

## Licencia

MIT License - Ver archivo LICENSE para más detalles.

## Autor

Monghit - 2024

## Referencias

- [JEP 444: Virtual Threads](https://openjdk.org/jeps/444)
- [JEP 441: Pattern Matching for switch](https://openjdk.org/jeps/441)
- [JEP 440: Record Patterns](https://openjdk.org/jeps/440)
- [JEP 431: Sequenced Collections](https://openjdk.org/jeps/431)
- [JEP 443: Unnamed Patterns and Variables](https://openjdk.org/jeps/443)
- [Java 21 Documentation](https://docs.oracle.com/en/java/javase/21/)
