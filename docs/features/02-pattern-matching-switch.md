# Pattern Matching for Switch

## Descripción

Pattern Matching for Switch es una evolución significativa de la expresión `switch` en Java 21. Permite usar patrones complejos en las cláusulas case, incluyendo type patterns, guarded patterns, y deconstrucción de objetos, haciendo el código más expresivo y seguro.

Esta característica se graduó a feature estándar en Java 21 después de estar en preview en versiones anteriores (Java 17-20).

## Problema que Resuelve

### Antes de Java 21

En versiones anteriores de Java, trabajar con jerarquías de tipos requería código verboso y propenso a errores:

#### 1. Múltiples instanceof con casting
```java
// ❌ Problema: Código verboso, repetitivo y propenso a errores
public String describeShape(Shape shape) {
    if (shape instanceof Circle) {
        Circle c = (Circle) shape;  // Cast manual
        if (c.radius() < 5) {
            return "Small circle with radius " + c.radius();
        } else if (c.radius() < 10) {
            return "Medium circle with radius " + c.radius();
        } else {
            return "Large circle with radius " + c.radius();
        }
    } else if (shape instanceof Rectangle) {
        Rectangle r = (Rectangle) shape;  // Cast manual
        if (r.width() == r.height()) {
            return "Square with side " + r.width();
        } else {
            return "Rectangle " + r.width() + "x" + r.height();
        }
    } else if (shape instanceof Triangle) {
        Triangle t = (Triangle) shape;  // Cast manual
        return "Triangle with sides " + t.side1() + ", " + t.side2() + ", " + t.side3();
    }
    return "Unknown shape";
}
```

**Problemas:**
- Código repetitivo y verboso
- Casting manual propenso a errores
- Difícil de mantener y extender
- No exhaustivo (puede olvidar casos)

#### 2. Switch tradicional limitado
```java
// ❌ Problema: Solo constantes primitivas y Strings
public String processValue(int type) {
    switch (type) {
        case 1:
            return "Type One";
        case 2:
            return "Type Two";
        default:
            return "Unknown";
    }
    // No puede trabajar con tipos complejos
}
```

### Después de Java 21

```java
// ✅ Solución: Pattern matching elegante y seguro
public String describeShape(Shape shape) {
    return switch (shape) {
        case Circle c when c.radius() < 5 ->
            "Small circle with radius " + c.radius();

        case Circle c when c.radius() < 10 ->
            "Medium circle with radius " + c.radius();

        case Circle c ->
            "Large circle with radius " + c.radius();

        case Rectangle r when r.width() == r.height() ->
            "Square with side " + r.width();

        case Rectangle r ->
            "Rectangle " + r.width() + "x" + r.height();

        case Triangle t ->
            "Triangle with sides " + t.side1() + ", " + t.side2() + ", " + t.side3();
    };
}
```

**Ventajas:**
- Código conciso y expresivo
- Sin casting manual (automático)
- Type-safe y exhaustivo
- Fácil de mantener y extender
- Guards (when clauses) para condiciones adicionales

## Características Principales

### 1. Type Patterns

```java
// Pattern matching con tipos
public double calculateArea(Shape shape) {
    return switch (shape) {
        case Circle c -> Math.PI * c.radius() * c.radius();
        case Rectangle r -> r.width() * r.height();
        case Triangle t -> {
            // Block para lógica compleja
            double s = (t.side1() + t.side2() + t.side3()) / 2;
            yield Math.sqrt(s * (s - t.side1()) * (s - t.side2()) * (s - t.side3()));
        }
    };
}
```

### 2. Guarded Patterns (when clauses)

```java
// Pattern matching con guardas
public String categorizeEmployee(Employee emp) {
    return switch (emp) {
        case Employee e when e.salary() > 100_000 && e.experience() > 10 ->
            "Senior Executive";

        case Employee e when e.salary() > 50_000 && e.experience() > 5 ->
            "Senior Employee";

        case Employee e when e.experience() > 2 ->
            "Mid-level Employee";

        case Employee e ->
            "Junior Employee";
    };
}
```

### 3. Null Handling

```java
// Manejo explícito de null
public String describeObject(Object obj) {
    return switch (obj) {
        case null -> "null object";  // ✅ Case explícito para null
        case String s when s.isEmpty() -> "empty string";
        case String s -> "String: " + s;
        case Integer i -> "Integer: " + i;
        default -> "Unknown type";
    };
}
```

### 4. Switch Expressions

```java
// Switch como expresión (retorna valor)
public int getDaysInMonth(Month month, boolean isLeapYear) {
    return switch (month) {
        case JANUARY, MARCH, MAY, JULY, AUGUST, OCTOBER, DECEMBER -> 31;
        case APRIL, JUNE, SEPTEMBER, NOVEMBER -> 30;
        case FEBRUARY -> isLeapYear ? 29 : 28;
    };
}
```

## Sealed Classes + Pattern Matching

Una combinación poderosa con sealed interfaces/classes:

```java
// Sealed interface asegura que conocemos todos los tipos
public sealed interface Shape
    permits Circle, Rectangle, Triangle {}

public record Circle(double radius) implements Shape {}
public record Rectangle(double width, double height) implements Shape {}
public record Triangle(double side1, double side2, double side3) implements Shape {}

// Switch exhaustivo sin default (el compilador verifica todos los casos)
public double calculateArea(Shape shape) {
    return switch (shape) {
        case Circle c -> Math.PI * c.radius() * c.radius();
        case Rectangle r -> r.width() * r.height();
        case Triangle t -> calculateTriangleArea(t);
        // ✅ No necesita default - el compilador sabe que cubrimos todos los casos
    };
}
```

## Casos de Uso

### 1. Procesamiento de Eventos

```java
public void handleEvent(Event event) {
    switch (event) {
        case MouseClickEvent e when e.button() == Button.LEFT ->
            handleLeftClick(e.x(), e.y());

        case MouseClickEvent e when e.button() == Button.RIGHT ->
            showContextMenu(e.x(), e.y());

        case KeyPressEvent e when e.key() == Key.ENTER ->
            submitForm();

        case KeyPressEvent e when e.key() == Key.ESCAPE ->
            cancelOperation();

        case NetworkEvent e when e.status() == Status.CONNECTED ->
            onConnect(e);

        case NetworkEvent e when e.status() == Status.DISCONNECTED ->
            onDisconnect(e);

        default ->
            log.warn("Unhandled event: " + event);
    }
}
```

### 2. Validación de Datos

```java
public ValidationResult validate(Object data) {
    return switch (data) {
        case null ->
            ValidationResult.error("Data cannot be null");

        case String s when s.isBlank() ->
            ValidationResult.error("String cannot be blank");

        case String s when s.length() > 255 ->
            ValidationResult.error("String too long");

        case String s ->
            ValidationResult.success();

        case Integer i when i < 0 ->
            ValidationResult.error("Integer must be positive");

        case Integer i when i > 1000 ->
            ValidationResult.error("Integer too large");

        case Integer i ->
            ValidationResult.success();

        default ->
            ValidationResult.error("Unsupported type: " + data.getClass());
    };
}
```

### 3. Parsing y Transformación

```java
public JsonValue parseValue(Token token) {
    return switch (token) {
        case Token t when t.type() == TokenType.STRING ->
            new JsonString(t.value());

        case Token t when t.type() == TokenType.NUMBER ->
            new JsonNumber(Double.parseDouble(t.value()));

        case Token t when t.type() == TokenType.TRUE ->
            JsonBoolean.TRUE;

        case Token t when t.type() == TokenType.FALSE ->
            JsonBoolean.FALSE;

        case Token t when t.type() == TokenType.NULL ->
            JsonNull.INSTANCE;

        default ->
            throw new ParseException("Unexpected token: " + token);
    };
}
```

### 4. Estado de Aplicación

```java
public String getStatusMessage(ApplicationState state) {
    return switch (state) {
        case InitializingState s when s.progress() < 50 ->
            "Starting up... " + s.progress() + "%";

        case InitializingState s ->
            "Almost ready... " + s.progress() + "%";

        case RunningState s when s.activeUsers() > 1000 ->
            "High load: " + s.activeUsers() + " users";

        case RunningState s ->
            "Running normally: " + s.activeUsers() + " users";

        case MaintenanceState s ->
            "Under maintenance until " + s.estimatedEnd();

        case ErrorState s ->
            "Error occurred: " + s.errorMessage();

        case ShutdownState s ->
            "Shutting down...";
    };
}
```

## Ejemplo Completo del Proyecto

```java
@Service
public class PatternMatchingService {

    /**
     * Calcula el área de una forma usando pattern matching
     */
    public double calculateArea(Shape shape) {
        log.info("Calculating area for shape: {}", shape.getClass().getSimpleName());

        return switch (shape) {
            case Circle c -> {
                log.debug("Circle with radius: {}", c.radius());
                yield c.area();
            }
            case Rectangle r -> {
                log.debug("Rectangle {}x{}", r.width(), r.height());
                yield r.area();
            }
            case Triangle t -> {
                log.debug("Triangle with sides: {}, {}, {}", t.side1(), t.side2(), t.side3());
                yield t.area();
            }
        };
    }

    /**
     * Describe una forma con guards (when clauses)
     */
    public String describeShape(Shape shape) {
        return switch (shape) {
            case Circle c when c.radius() < 5 ->
                String.format(Locale.US, "Small circle with radius %.2f", c.radius());

            case Circle c when c.radius() >= 5 && c.radius() < 10 ->
                String.format(Locale.US, "Medium circle with radius %.2f", c.radius());

            case Circle c ->
                String.format(Locale.US, "Large circle with radius %.2f", c.radius());

            case Rectangle r when r.isSquare() ->
                String.format(Locale.US, "Square with side %.2f", r.width());

            case Rectangle r when r.width() > r.height() ->
                String.format(Locale.US, "Horizontal rectangle (%.2f x %.2f)", r.width(), r.height());

            case Rectangle r ->
                String.format(Locale.US, "Vertical rectangle (%.2f x %.2f)", r.width(), r.height());

            case Triangle t when t.isEquilateral() ->
                String.format(Locale.US, "Equilateral triangle with side %.2f", t.side1());

            case Triangle t when t.isIsosceles() ->
                String.format(Locale.US, "Isosceles triangle");

            case Triangle t ->
                String.format(Locale.US, "Scalene triangle");
        };
    }

    /**
     * Pattern matching con diferentes tipos de objetos
     */
    public String describeObject(Object obj) {
        return switch (obj) {
            case null -> "null object";
            case String s when s.isEmpty() -> "empty string";
            case String s -> String.format("String with length %d: %s", s.length(), s);
            case Integer i when i < 0 -> String.format("negative integer: %d", i);
            case Integer i when i == 0 -> "zero";
            case Integer i -> String.format("positive integer: %d", i);
            case Double d -> String.format(Locale.US, "double: %.2f", d);
            case Shape s -> String.format(Locale.US, "Shape: %s with area %.2f", s.type(), s.area());
            default -> String.format("unknown type: %s", obj.getClass().getSimpleName());
        };
    }

    /**
     * Validación usando pattern matching
     */
    public ValidationResult validateShape(Shape shape) {
        boolean isValid = switch (shape) {
            case Circle c -> c.radius() > 0 && c.radius() < 1000;
            case Rectangle r -> r.width() > 0 && r.height() > 0 &&
                               r.width() < 1000 && r.height() < 1000;
            case Triangle t -> t.side1() > 0 && t.side2() > 0 && t.side3() > 0 &&
                              t.side1() < 1000 && t.side2() < 1000 && t.side3() < 1000;
        };

        String message = isValid ? "Shape is valid" : "Shape dimensions are out of range";
        return new ValidationResult(isValid, message, shape.type());
    }
}
```

## Comparación Detallada

### Antes vs Después

| Aspecto | Java 16 y anteriores | Java 21 |
|---------|---------------------|---------|
| Type checking | instanceof manual | Pattern automático |
| Casting | Explícito y repetitivo | Automático |
| Null handling | if (x == null) separado | case null integrado |
| Condiciones | if-else anidados | Guards (when) |
| Exhaustividad | No verificada | Compilador verifica |
| Expresiones | No, solo statements | Sí, retorna valor |

### Ejemplo Comparativo

```java
// ❌ ANTES (Java 16)
public String processPayment(Payment payment) {
    if (payment == null) {
        return "No payment";
    }
    if (payment instanceof CreditCard) {
        CreditCard cc = (CreditCard) payment;
        if (cc.balance() > payment.amount()) {
            return "Process credit card: " + cc.number();
        } else {
            return "Insufficient balance";
        }
    } else if (payment instanceof DebitCard) {
        DebitCard dc = (DebitCard) payment;
        return "Process debit card: " + dc.number();
    } else if (payment instanceof Cash) {
        Cash cash = (Cash) payment;
        if (cash.amount() >= payment.amount()) {
            return "Accept cash: " + cash.amount();
        } else {
            return "Insufficient cash";
        }
    }
    return "Unknown payment method";
}

// ✅ DESPUÉS (Java 21)
public String processPayment(Payment payment) {
    return switch (payment) {
        case null ->
            "No payment";

        case CreditCard cc when cc.balance() > payment.amount() ->
            "Process credit card: " + cc.number();

        case CreditCard cc ->
            "Insufficient balance";

        case DebitCard dc ->
            "Process debit card: " + dc.number();

        case Cash cash when cash.amount() >= payment.amount() ->
            "Accept cash: " + cash.amount();

        case Cash cash ->
            "Insufficient cash";

        default ->
            "Unknown payment method";
    };
}
```

## Buenas Prácticas

### ✅ DO: Usar con Sealed Types

```java
// ✅ CORRECTO: Sealed interface + pattern matching = exhaustividad garantizada
public sealed interface Result permits Success, Error {}

public record Success(String data) implements Result {}
public record Error(String message) implements Result {}

public String processResult(Result result) {
    return switch (result) {
        case Success s -> "Success: " + s.data();
        case Error e -> "Error: " + e.message();
        // No necesita default - el compilador verifica exhaustividad
    };
}
```

### ✅ DO: Ordenar Cases de Específico a General

```java
// ✅ CORRECTO: Casos más específicos primero
return switch (shape) {
    case Circle c when c.radius() < 5 -> "Small circle";     // Específico
    case Circle c when c.radius() < 10 -> "Medium circle";   // Específico
    case Circle c -> "Large circle";                          // General
    // ...
};

// ❌ INCORRECTO: General antes que específico
return switch (shape) {
    case Circle c -> "Circle";                  // ❌ Este matchea todo
    case Circle c when c.radius() < 5 -> ...    // ❌ Nunca se alcanza
};
```

### ✅ DO: Usar Yield en Blocks

```java
// ✅ CORRECTO: yield para retornar valor desde block
return switch (command) {
    case "start" -> {
        initialize();
        startService();
        yield "Service started";  // ← yield para retornar
    }
    case "stop" -> {
        stopService();
        cleanup();
        yield "Service stopped";
    }
    default -> "Unknown command";
};
```

### ✅ DO: Manejar null Explícitamente

```java
// ✅ CORRECTO: Case explícito para null
return switch (value) {
    case null -> "No value";
    case String s -> "String: " + s;
    default -> "Other";
};

// ❌ INCORRECTO: Olvidar null puede causar NullPointerException
return switch (value) {
    case String s -> "String: " + s;  // ❌ Falla si value es null
    default -> "Other";
};
```

### ⚠️ CUIDADO: Guards son Evaluados en Orden

```java
// ⚠️ El orden importa con guards
return switch (number) {
    case Integer i when i > 100 -> "Large";     // Se evalúa primero
    case Integer i when i > 50 -> "Medium";     // Luego este
    case Integer i -> "Small";                   // Finalmente este
};
```

## Limitaciones y Consideraciones

1. **Performance**: Pattern matching tiene un pequeño overhead comparado con switch tradicional en primitivos

2. **Complejidad de Guards**: Guards muy complejos pueden hacer el código difícil de leer

3. **Exhaustividad**: Con tipos no-sealed, necesitas default case

4. **Backward compatibility**: Requiere Java 21+ (preview en 17-20)

## Conclusión

Pattern Matching for Switch transforma el código Java de verboso y propenso a errores a conciso y type-safe:

- **Expresividad**: Código más claro y fácil de leer
- **Seguridad**: Type-safe con verificación del compilador
- **Mantenibilidad**: Fácil agregar nuevos casos
- **Integración**: Funciona perfectamente con sealed types y records

Es una de las mejoras más significativas del lenguaje Java en los últimos años.

## Referencias

- **JEP 441**: Pattern Matching for switch
- **JEP 427**: Pattern Matching for switch (Third Preview)
- **JEP 406**: Pattern Matching for switch (Preview)

## Ejemplos en el Proyecto

- Implementación: `src/main/java/com/monghit/java21test/features/patternmatching/`
- Tests: `src/test/java/com/monghit/java21test/features/patternmatching/`
- Endpoints: `/api/pattern-matching/**`
