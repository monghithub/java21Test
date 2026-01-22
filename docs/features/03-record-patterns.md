# Record Patterns

## Descripción

Record Patterns es una feature de Java 21 que permite deconstruir records directamente en pattern matching. Puedes extraer los componentes de un record en una sola operación, incluso con deconstrucción anidada de records dentro de otros records.

Esta característica se graduó a feature estándar en Java 21 después de estar en preview en Java 19-20.

## Problema que Resuelve

### Antes de Java 21

Trabajar con records anidados requería múltiples pasos de acceso manual:

```java
// Record models
public record Point(int x, int y) {}
public record Rectangle(Point topLeft, Point bottomRight) {}
public record Color(int red, int green, int blue) {}
public record ColoredRectangle(Rectangle rect, Color color) {}

// ❌ Problema: Acceso verbose y repetitivo
public String describe(ColoredRectangle cr) {
    Rectangle rect = cr.rect();
    Point topLeft = rect.topLeft();
    Point bottomRight = rect.bottomRight();
    int x1 = topLeft.x();
    int y1 = topLeft.y();
    int x2 = bottomRight.x();
    int y2 = bottomRight.y();

    Color color = cr.color();
    int r = color.red();
    int g = color.green();
    int b = color.blue();

    return String.format(
        "Rectangle from (%d,%d) to (%d,%d) with color RGB(%d,%d,%d)",
        x1, y1, x2, y2, r, g, b
    );
}
```

**Problemas:**
- Código muy verboso
- Muchas variables intermedias
- Difícil de leer y mantener
- Propenso a errores de orden

### Después de Java 21

```java
// ✅ Solución: Deconstrucción directa y concisa
public String describe(ColoredRectangle cr) {
    // Deconstrucción anidada en una sola línea
    if (cr instanceof ColoredRectangle(
            Rectangle(Point(int x1, int y1), Point(int x2, int y2)),
            Color(int r, int g, int b))) {

        return String.format(
            "Rectangle from (%d,%d) to (%d,%d) with color RGB(%d,%d,%d)",
            x1, y1, x2, y2, r, g, b
        );
    }
    return "Invalid rectangle";
}
```

**Ventajas:**
- Deconstrucción directa sin variables intermedias
- Código más conciso y legible
- Type-safe con verificación del compilador
- Soporta deconstrucción anidada profunda

## Características Principales

### 1. Deconstrucción Básica

```java
public record Employee(String name, int age, double salary) {}

public String analyzeEmployee(Object obj) {
    // Deconstruir el record directamente
    if (obj instanceof Employee(String name, int age, double salary)) {
        return String.format("%s is %d years old and earns $%.2f", name, age, salary);
    }
    return "Not an employee";
}
```

### 2. Deconstrucción Anidada

```java
public record Address(String street, String city, String zipCode) {}
public record Person(String name, Address address) {}

public String getCityInfo(Object obj) {
    // Deconstruir records anidados
    if (obj instanceof Person(String name, Address(String street, String city, String zip))) {
        return String.format("%s lives in %s (street: %s, zip: %s)",
            name, city, street, zip);
    }
    return "Not a person";
}
```

### 3. Con Switch Expression

```java
public record Circle(double radius) {}
public record Rectangle(double width, double height) {}

public double calculateArea(Object shape) {
    return switch (shape) {
        case Circle(double r) -> Math.PI * r * r;
        case Rectangle(double w, double h) -> w * h;
        default -> 0;
    };
}
```

### 4. Usando var para Inferencia

```java
public record Employee(String name, int age, Address address, Department dept) {}

// Usar 'var' para componentes que no necesitamos extraer
public String getEmployeeCity(Employee emp) {
    if (emp instanceof Employee(var name, var age, Address(var street, String city, var zip), var dept)) {
        return city;  // Solo nos interesa 'city'
    }
    return "Unknown";
}
```

## Casos de Uso

### 1. Filtrado de Colecciones

```java
public record Employee(String name, int age, Address address, Department department, double salary) {}
public record Address(String street, String city, String zipCode, String country) {}
public record Department(String name, String code, String manager) {}

/**
 * Filtrar empleados por ciudad usando record patterns
 */
public List<Employee> filterByCity(List<Employee> employees, String targetCity) {
    return employees.stream()
        .filter(emp -> {
            // Extraer solo el campo 'city' del Address anidado
            if (emp instanceof Employee(var name, var age,
                    Address(var street, String city, var zip, var country),
                    var dept, var salary)) {
                return city.equalsIgnoreCase(targetCity);
            }
            return false;
        })
        .toList();
}

/**
 * Filtrar por código de departamento
 */
public List<Employee> filterByDepartment(List<Employee> employees, String deptCode) {
    return employees.stream()
        .filter(emp -> {
            // Extraer solo el código del Department anidado
            if (emp instanceof Employee(var name, var age, var addr,
                    Department(var deptName, String code, var manager),
                    var salary)) {
                return code.equalsIgnoreCase(deptCode);
            }
            return false;
        })
        .toList();
}
```

### 2. Generación de Reportes

```java
/**
 * Genera un reporte detallado deconstruyendo completamente el empleado
 */
public EmployeeReport generateReport(Employee employee) {
    // Deconstrucción completa de todos los niveles
    if (employee instanceof Employee(
            String name,
            int age,
            Address(String street, String city, String zip, String country),
            Department(String deptName, String deptCode, String manager),
            double salary)) {

        String ageCategory = age < 30 ? "Young professional" :
                            age < 50 ? "Experienced professional" :
                            "Senior professional";

        String salaryBracket = salary < 50000 ? "Entry level" :
                              salary < 100000 ? "Mid level" :
                              "Senior level";

        return new EmployeeReport(
            name,
            age,
            ageCategory,
            String.format("%s, %s %s", street, city, zip),
            country,
            deptName,
            deptCode,
            manager,
            salary,
            salaryBracket
        );
    }

    throw new IllegalStateException("Unable to generate report");
}
```

### 3. Validación de Datos

```java
/**
 * Valida un empleado usando record patterns
 */
public ValidationResult validateEmployee(Employee employee) {
    // Deconstruir y validar en un solo paso
    if (employee instanceof Employee(
            String name,
            int age,
            Address(String street, String city, String zip, String country),
            Department(String deptName, String deptCode, String manager),
            double salary)) {

        boolean validAge = age >= 18 && age <= 100;
        boolean validSalary = salary > 0 && salary < 1000000;
        boolean validZip = zip != null && !zip.isBlank();
        boolean validDeptCode = deptCode != null && deptCode.matches("[A-Z]{2,5}");

        boolean isValid = validAge && validSalary && validZip && validDeptCode;

        String message = isValid ? "Employee data is valid" :
            String.format("Validation issues: age=%s, salary=%s, zip=%s, deptCode=%s",
                validAge, validSalary, validZip, validDeptCode);

        return new ValidationResult(isValid, message, name);
    }

    return new ValidationResult(false, "Unable to validate employee", "Unknown");
}
```

### 4. Comparación de Objetos

```java
/**
 * Compara dos empleados deconstruyendo ambos
 */
public EmployeeComparison compareEmployees(Employee emp1, Employee emp2) {
    // Deconstruir ambos empleados simultáneamente
    if (emp1 instanceof Employee(String name1, int age1, Address addr1, Department dept1, double salary1) &&
        emp2 instanceof Employee(String name2, int age2, Address addr2, Department dept2, double salary2)) {

        boolean sameDepartment = dept1.code().equals(dept2.code());
        boolean sameCity = addr1.city().equals(addr2.city());
        double salaryDiff = Math.abs(salary1 - salary2);
        int ageDiff = Math.abs(age1 - age2);

        String comparison = sameDepartment && sameCity ?
            String.format("%s and %s work in the same department and city", name1, name2) :
            sameDepartment ?
            String.format("%s and %s work in the same department but different cities", name1, name2) :
            sameCity ?
            String.format("%s and %s live in the same city but work in different departments", name1, name2) :
            String.format("%s and %s work in different departments and cities", name1, name2);

        return new EmployeeComparison(
            name1, name2,
            sameDepartment, sameCity,
            salaryDiff, ageDiff,
            comparison
        );
    }

    throw new IllegalStateException("Unable to compare employees");
}
```

### 5. Transformación de Datos

```java
public record Point3D(double x, double y, double z) {}
public record Sphere(Point3D center, double radius) {}

/**
 * Transforma objetos geométricos
 */
public Sphere translateSphere(Sphere sphere, double dx, double dy, double dz) {
    // Deconstruir, transformar y reconstruir
    if (sphere instanceof Sphere(Point3D(double x, double y, double z), double radius)) {
        return new Sphere(
            new Point3D(x + dx, y + dy, z + dz),
            radius
        );
    }
    throw new IllegalArgumentException("Invalid sphere");
}

/**
 * Escala una esfera
 */
public Sphere scaleSphere(Sphere sphere, double factor) {
    return switch (sphere) {
        case Sphere(Point3D center, double radius) ->
            new Sphere(center, radius * factor);
    };
}
```

## Ejemplo Completo del Proyecto

```java
@Service
public class RecordPatternService {

    private static final Logger log = LoggerFactory.getLogger(RecordPatternService.class);

    /**
     * Analiza un empleado usando record patterns con guardas
     */
    public String analyzeEmployee(Employee employee) {
        log.info("Analyzing employee: {}", employee.name());

        // Record pattern con deconstrucción y guards
        return switch (employee) {
            case Employee(String name, int age, Address addr, Department dept, double salary)
                when salary > 100000 ->
                String.format("Senior employee %s, age %d, earns $%.2f in %s department",
                    name, age, salary, dept.name());

            case Employee(String name, int age, Address addr, Department dept, double salary)
                when salary > 50000 ->
                String.format("Mid-level employee %s, age %d, earns $%.2f in %s department",
                    name, age, salary, dept.name());

            case Employee(String name, int age, Address addr, Department dept, double salary) ->
                String.format("Junior employee %s, age %d, earns $%.2f in %s department",
                    name, age, salary, dept.name());
        };
    }

    /**
     * Extrae información con deconstrucción anidada profunda
     */
    public String extractCityFromEmployee(Employee employee) {
        log.info("Extracting city from employee: {}", employee.name());

        // Deconstrucción anidada de Employee -> Address
        if (employee instanceof Employee(
                String name,
                int age,
                Address(String street, String city, String zip, String country),
                Department dept,
                double salary)) {

            return String.format("%s lives in %s, %s (ZIP: %s)", name, city, country, zip);
        }

        return "Unable to extract city information";
    }

    /**
     * Filtra empleados usando record patterns en streams
     */
    public List<Employee> filterByCity(List<Employee> employees, String targetCity) {
        log.info("Filtering employees by city: {}", targetCity);

        return employees.stream()
            .filter(emp -> {
                // Pattern matching dentro de lambda
                if (emp instanceof Employee(
                        var name, var age,
                        Address(var street, String city, var zip, var country),
                        var dept, var salary)) {
                    return city.equalsIgnoreCase(targetCity);
                }
                return false;
            })
            .collect(Collectors.toList());
    }
}
```

## Comparación Detallada

### Antes vs Después

```java
public record Order(
    String id,
    Customer customer,
    List<Item> items,
    Payment payment
) {}

public record Customer(String name, Address address) {}
public record Address(String street, String city) {}
public record Payment(String type, double amount) {}

// ❌ ANTES: Acceso manual verbose
public String getOrderInfo(Order order) {
    Customer customer = order.customer();
    Address address = customer.address();
    String city = address.city();
    Payment payment = order.payment();
    double amount = payment.amount();

    return String.format("Order for customer in %s, total: $%.2f", city, amount);
}

// ✅ DESPUÉS: Deconstrucción directa
public String getOrderInfo(Order order) {
    if (order instanceof Order(
            var id,
            Customer(var name, Address(var street, String city)),
            var items,
            Payment(var type, double amount))) {

        return String.format("Order for customer in %s, total: $%.2f", city, amount);
    }
    return "Invalid order";
}
```

## Combinación con Otras Features

### 1. Record Patterns + Sealed Types

```java
public sealed interface Payment permits CreditCard, DebitCard, Cash {}

public record CreditCard(String number, String cvv, double limit) implements Payment {}
public record DebitCard(String number, String pin) implements Payment {}
public record Cash(double amount) implements Payment {}

// Exhaustividad garantizada con sealed + record patterns
public String processPayment(Payment payment) {
    return switch (payment) {
        case CreditCard(String number, String cvv, double limit) ->
            "Processing credit card ending in " + number.substring(number.length() - 4);

        case DebitCard(String number, String pin) ->
            "Processing debit card ending in " + number.substring(number.length() - 4);

        case Cash(double amount) ->
            "Processing cash payment of $" + amount;
        // No necesita default - sealed interface garantiza exhaustividad
    };
}
```

### 2. Record Patterns + Guards

```java
public record Transaction(String id, double amount, String type, LocalDateTime timestamp) {}

public String categorizeTransaction(Transaction tx) {
    return switch (tx) {
        case Transaction(var id, double amount, String type, var time)
            when amount > 10000 && type.equals("DEBIT") ->
            "Large debit transaction - requires approval";

        case Transaction(var id, double amount, String type, var time)
            when amount > 10000 && type.equals("CREDIT") ->
            "Large credit transaction - automatic";

        case Transaction(var id, double amount, var type, var time)
            when amount < 0 ->
            "Invalid transaction - negative amount";

        case Transaction(var id, double amount, var type, var time) ->
            "Regular transaction";
    };
}
```

### 3. Record Patterns + Streams

```java
public record Sale(String productId, int quantity, double price, LocalDate date) {}

public Map<String, Double> getTotalSalesByProduct(List<Sale> sales) {
    return sales.stream()
        .collect(Collectors.groupingBy(
            sale -> switch (sale) {
                case Sale(String productId, var q, var p, var d) -> productId;
            },
            Collectors.summingDouble(
                sale -> switch (sale) {
                    case Sale(var id, int quantity, double price, var d) -> quantity * price;
                }
            )
        ));
}
```

## Buenas Prácticas

### ✅ DO: Usar var para Componentes No Necesarios

```java
// ✅ CORRECTO: var para componentes que no usamos
if (employee instanceof Employee(
        var name,
        var age,
        Address(var street, String city, var zip, var country),  // Solo 'city'
        var dept,
        var salary)) {
    return city;
}
```

### ✅ DO: Deconstruir Solo lo Necesario

```java
// ✅ CORRECTO: Solo deconstruir Address, no Department
if (employee instanceof Employee(
        String name,
        int age,
        Address(String street, String city, String zip, String country),
        Department dept,  // No deconstruido - usamos el record completo
        double salary)) {

    return String.format("%s works in %s department, lives in %s",
        name, dept.name(), city);
}
```

### ✅ DO: Usar en Switch para Múltiples Casos

```java
// ✅ CORRECTO: Record patterns en switch
public String processShape(Object shape) {
    return switch (shape) {
        case Circle(double radius) -> "Circle with radius " + radius;
        case Rectangle(double width, double height) -> "Rectangle " + width + "x" + height;
        case Triangle(double a, double b, double c) -> "Triangle with sides " + a + "," + b + "," + c;
        default -> "Unknown shape";
    };
}
```

### ⚠️ CUIDADO: Nombres de Variables Claros

```java
// ⚠️ EVITAR: Nombres confusos
if (employee instanceof Employee(String s, int n, Address a, Department d, double x)) {
    // ¿Qué es 's', 'n', 'x'?
}

// ✅ MEJOR: Nombres descriptivos
if (employee instanceof Employee(String name, int age, Address addr, Department dept, double salary)) {
    // Claro y legible
}
```

### ⚠️ CUIDADO: No Abusar de la Profundidad

```java
// ⚠️ EVITAR: Deconstrucción demasiado profunda
if (obj instanceof Company(
        String name,
        Department(
            String deptName,
            Manager(
                String managerName,
                Employee(
                    String empName,
                    Address(
                        String street,
                        City(String cityName, Country(String countryName))
                    )
                )
            )
        )
    )) {
    // Difícil de leer y mantener
}

// ✅ MEJOR: Dividir en pasos
if (obj instanceof Company(String name, Department dept)) {
    if (dept.manager() instanceof Manager(String managerName, Employee emp)) {
        // Más legible
    }
}
```

## Limitaciones

1. **Solo Records**: No funciona con clases regulares, solo con records

2. **Performance**: Pequeño overhead comparado con acceso directo de getters

3. **Complejidad**: Deconstrucciones muy anidadas pueden ser difíciles de leer

4. **No Modificable**: Los componentes extraídos son final (inmutables en el scope)

## Conclusión

Record Patterns simplifica dramáticamente el trabajo con records anidados:

- **Concisión**: Menos código boilerplate
- **Claridad**: Intención clara y directa
- **Type-Safety**: Verificación del compilador
- **Composición**: Funciona bien con otras features de Java 21

Es especialmente poderoso cuando se combina con sealed types y pattern matching.

## Referencias

- **JEP 440**: Record Patterns
- **JEP 432**: Record Patterns (Second Preview)
- **JEP 405**: Record Patterns & Array Patterns (Preview)

## Ejemplos en el Proyecto

- Implementación: `src/main/java/com/monghit/java21test/features/recordpatterns/`
- Tests: `src/test/java/com/monghit/java21test/features/recordpatterns/`
- Endpoints: `/api/record-patterns/**`
