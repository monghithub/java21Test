# Sequenced Collections

## Descripción

Sequenced Collections es una nueva jerarquía de interfaces introducida en Java 21 que proporciona una forma uniforme y predecible de trabajar con colecciones que tienen un orden de encuentro definido. Introduce métodos consistentes para acceder al primer y último elemento, y para obtener vistas reversas de las colecciones.

Esta feature fue finalmente incorporada en Java 21 después de años de inconsistencias en las APIs de colecciones.

## Problema que Resuelve

### Antes de Java 21

Las colecciones de Java tenían APIs inconsistentes e incompletas para operaciones comunes:

#### 1. Acceso al Primer y Último Elemento

```java
// ❌ Problema: APIs inconsistentes para diferentes colecciones

// List - acceso por índice
List<String> list = new ArrayList<>();
String first = list.get(0);  // Puede lanzar IndexOutOfBoundsException
String last = list.get(list.size() - 1);

// Deque - métodos especializados
Deque<String> deque = new LinkedList<>();
String first = deque.getFirst();
String last = deque.getLast();

// SortedSet - métodos únicos
SortedSet<String> sortedSet = new TreeSet<>();
String first = sortedSet.first();
String last = sortedSet.last();

// LinkedHashSet - ¡No hay forma directa!
LinkedHashSet<String> linkedSet = new LinkedHashSet<>();
String first = linkedSet.iterator().next();  // ¿Feo, verdad?
String last = ???  // Imposible sin iterar todo
```

#### 2. Vista Reversa

```java
// ❌ Problema: No había forma estándar de obtener vista reversa

// List - necesita Collections.reverse() o streams
List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C"));
Collections.reverse(list);  // ¡Modifica la lista original!

// O con streams (crea nueva lista)
List<String> reversed = list.stream()
    .collect(Collectors.collectingAndThen(
        Collectors.toList(),
        l -> { Collections.reverse(l); return l; }
    ));

// NavigableSet - tiene descendingSet()
NavigableSet<String> navSet = new TreeSet<>();
NavigableSet<String> reversed = navSet.descendingSet();  // ✓ Esto funciona

// LinkedHashSet - ¡Imposible sin crear nueva colección!
```

#### 3. Agregar al Principio/Final

```java
// ❌ Problema: APIs diferentes para cada colección

// List
list.add(0, "first");  // Al principio (ineficiente en ArrayList)
list.add("last");      // Al final

// Deque
deque.addFirst("first");
deque.addLast("last");

// LinkedHashSet - ¡No se puede controlar el orden de inserción!
```

### Después de Java 21

```java
// ✅ Solución: API unificada para todas las colecciones secuenciadas

SequencedCollection<String> collection = new ArrayList<>();  // O LinkedHashSet, o Deque...

// Métodos consistentes en todas las implementaciones
collection.addFirst("first");
collection.addLast("last");

String first = collection.getFirst();
String last = collection.getLast();

SequencedCollection<String> reversed = collection.reversed();

// ¡La misma API para List, Set, Deque!
```

## Nueva Jerarquía de Interfaces

Java 21 introduce tres nuevas interfaces:

```
        Collection
            ↓
   SequencedCollection  ←━━━━━━━━┓
       ↙        ↘                ┃
      /          \               ┃
    List    SequencedSet         ┃
               ↓                 ┃
          SortedSet              ┃
               ↓                 ┃
          NavigableSet           ┃
                                 ┃
        Map                      ┃
         ↓                       ┃
    SequencedMap ━━━━━━━━━━━━━━━┛
         ↓
    SortedMap
         ↓
    NavigableMap
```

### SequencedCollection

```java
public interface SequencedCollection<E> extends Collection<E> {
    // Métodos para acceso a extremos
    E getFirst();
    E getLast();

    // Métodos para agregar en extremos
    void addFirst(E e);
    void addLast(E e);

    // Métodos para remover de extremos
    E removeFirst();
    E removeLast();

    // Vista reversa
    SequencedCollection<E> reversed();
}
```

### SequencedSet

```java
public interface SequencedSet<E> extends SequencedCollection<E>, Set<E> {
    SequencedSet<E> reversed();  // Retorna SequencedSet, no SequencedCollection
}
```

### SequencedMap

```java
public interface SequencedMap<K, V> extends Map<K, V> {
    // Entries
    Map.Entry<K, V> firstEntry();
    Map.Entry<K, V> lastEntry();

    // Remover entries
    Map.Entry<K, V> pollFirstEntry();
    Map.Entry<K, V> pollLastEntry();

    // Put en extremos
    V putFirst(K key, V value);
    V putLast(K key, V value);

    // Vistas de colecciones secuenciadas
    SequencedMap<K, V> reversed();
    SequencedSet<K> sequencedKeySet();
    SequencedCollection<V> sequencedValues();
    SequencedSet<Map.Entry<K, V>> sequencedEntrySet();
}
```

## Implementaciones que Ahora son Sequenced

### Listas (SequencedCollection)

```java
// Todas las listas son ahora SequencedCollection
List<String> arrayList = new ArrayList<>();
List<String> linkedList = new LinkedList<>();
List<String> vector = new Vector<>();

// Todos tienen estos métodos
arrayList.addFirst("first");
linkedList.getLast();
vector.reversed();
```

### Sets Ordenados (SequencedSet)

```java
// LinkedHashSet ahora tiene API completa
LinkedHashSet<String> linkedSet = new LinkedHashSet<>();
linkedSet.addFirst("first");  // ¡Nuevo en Java 21!
linkedSet.addLast("last");
linkedSet.getFirst();
linkedSet.getLast();
linkedSet.reversed();

// TreeSet también es SequencedSet
TreeSet<String> treeSet = new TreeSet<>();
treeSet.addFirst("a");  // Respeta el orden natural
```

### Deques

```java
// Deque siempre tuvo estos métodos, ahora implementa SequencedCollection
Deque<String> deque = new LinkedList<>();
ArrayDeque<String> arrayDeque = new ArrayDeque<>();
```

### Maps (SequencedMap)

```java
// LinkedHashMap es SequencedMap
LinkedHashMap<String, Integer> linkedMap = new LinkedHashMap<>();
linkedMap.putFirst("first", 1);  // ¡Nuevo!
linkedMap.putLast("last", 99);
linkedMap.firstEntry();
linkedMap.lastEntry();
linkedMap.reversed();

// TreeMap también
TreeMap<String, Integer> treeMap = new TreeMap<>();
```

## Casos de Uso

### 1. Cola de Procesamiento (Queue con acceso completo)

```java
/**
 * Cola de tareas con acceso eficiente a ambos extremos
 */
public class TaskQueue {
    private final SequencedCollection<Task> tasks = new LinkedList<>();

    // Agregar tarea urgente al principio
    public void addUrgent(Task task) {
        tasks.addFirst(task);
    }

    // Agregar tarea normal al final
    public void addNormal(Task task) {
        tasks.addLast(task);
    }

    // Obtener siguiente tarea (del principio)
    public Task getNext() {
        return tasks.isEmpty() ? null : tasks.removeFirst();
    }

    // Ver última tarea agregada
    public Task peekLast() {
        return tasks.isEmpty() ? null : tasks.getLast();
    }

    // Procesar en orden inverso
    public void processReverse() {
        for (Task task : tasks.reversed()) {
            task.process();
        }
    }
}
```

### 2. Cache LRU (Least Recently Used)

```java
/**
 * Cache con política LRU usando LinkedHashMap
 */
public class LRUCache<K, V> {
    private final SequencedMap<K, V> cache = new LinkedHashMap<>();
    private final int maxSize;

    public LRUCache(int maxSize) {
        this.maxSize = maxSize;
    }

    public void put(K key, V value) {
        // Si existe, remover para reinsertarlo al final (más reciente)
        if (cache.containsKey(key)) {
            cache.remove(key);
        }

        // Agregar al final (más reciente)
        cache.putLast(key, value);

        // Si excede el tamaño, remover el más antiguo (primero)
        if (cache.size() > maxSize) {
            cache.pollFirstEntry();  // Remueve el menos usado
        }
    }

    public V get(K key) {
        if (!cache.containsKey(key)) {
            return null;
        }

        // Mover al final (marcar como recién usado)
        V value = cache.remove(key);
        cache.putLast(key, value);
        return value;
    }

    public K getLeastRecentlyUsed() {
        Map.Entry<K, V> entry = cache.firstEntry();
        return entry != null ? entry.getKey() : null;
    }

    public K getMostRecentlyUsed() {
        Map.Entry<K, V> entry = cache.lastEntry();
        return entry != null ? entry.getKey() : null;
    }
}
```

### 3. Historia de Navegación (Browser-like History)

```java
/**
 * Sistema de navegación con historial
 */
public class NavigationHistory {
    private final SequencedCollection<Page> history = new ArrayList<>();
    private static final int MAX_HISTORY = 50;

    // Visitar nueva página
    public void visit(Page page) {
        history.addLast(page);

        // Mantener solo las últimas 50 páginas
        while (history.size() > MAX_HISTORY) {
            history.removeFirst();
        }
    }

    // Página actual (última visitada)
    public Page getCurrentPage() {
        return history.isEmpty() ? null : history.getLast();
    }

    // Primera página visitada (más antigua)
    public Page getOldestPage() {
        return history.isEmpty() ? null : history.getFirst();
    }

    // Navegar hacia atrás
    public Page goBack() {
        if (history.size() < 2) {
            return getCurrentPage();
        }
        history.removeLast();  // Remover página actual
        return history.getLast();  // Retornar la anterior
    }

    // Obtener historial en orden inverso (más reciente primero)
    public List<Page> getRecentFirst() {
        return new ArrayList<>(history.reversed());
    }
}
```

### 4. Ranking / Leaderboard

```java
/**
 * Tabla de clasificación con acceso rápido a top y bottom
 */
public class Leaderboard {
    private final SequencedSet<Player> ranking = new LinkedHashSet<>();

    // Actualizar ranking (mantiene orden de inserción)
    public void updateRanking(List<Player> players) {
        ranking.clear();
        for (Player player : players) {
            ranking.addLast(player);  // Orden: mejor a peor
        }
    }

    // Top 1 (mejor jugador)
    public Player getWinner() {
        return ranking.isEmpty() ? null : ranking.getFirst();
    }

    // Último lugar
    public Player getLastPlace() {
        return ranking.isEmpty() ? null : ranking.getLast();
    }

    // Top N jugadores
    public List<Player> getTopN(int n) {
        return ranking.stream()
            .limit(n)
            .toList();
    }

    // Bottom N jugadores (últimos)
    public List<Player> getBottomN(int n) {
        return ranking.reversed().stream()
            .limit(n)
            .toList();
    }

    // Ranking inverso (peor a mejor)
    public SequencedSet<Player> getReversedRanking() {
        return ranking.reversed();
    }
}
```

### 5. Undo/Redo Stack

```java
/**
 * Sistema de deshacer/rehacer acciones
 */
public class ActionHistory {
    private final SequencedCollection<Action> undoStack = new LinkedList<>();
    private final SequencedCollection<Action> redoStack = new LinkedList<>();
    private static final int MAX_UNDO = 100;

    // Ejecutar nueva acción
    public void execute(Action action) {
        action.execute();

        // Agregar al stack de undo
        undoStack.addLast(action);

        // Limpiar redo stack (no puedes rehacer después de nueva acción)
        redoStack.clear();

        // Mantener tamaño máximo
        if (undoStack.size() > MAX_UNDO) {
            undoStack.removeFirst();
        }
    }

    // Deshacer última acción
    public void undo() {
        if (undoStack.isEmpty()) {
            return;
        }

        Action action = undoStack.removeLast();
        action.undo();
        redoStack.addLast(action);
    }

    // Rehacer última acción deshecha
    public void redo() {
        if (redoStack.isEmpty()) {
            return;
        }

        Action action = redoStack.removeLast();
        action.execute();
        undoStack.addLast(action);
    }

    // Verificar si se puede deshacer
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    // Verificar si se puede rehacer
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    // Obtener última acción (sin remover)
    public Action getLastAction() {
        return undoStack.isEmpty() ? null : undoStack.getLast();
    }
}
```

## Ejemplo Completo del Proyecto

```java
@Service
public class SequencedCollectionService {

    private static final Logger log = LoggerFactory.getLogger(SequencedCollectionService.class);

    /**
     * Demuestra SequencedCollection con List
     */
    public SequencedListDemo demonstrateSequencedList() {
        log.info("Demonstrating SequencedCollection with List");

        List<String> list = new ArrayList<>();

        // Métodos de SequencedCollection
        list.addFirst("First");    // ← Nuevo en Java 21
        list.addLast("Last");
        list.addFirst("New First");

        String first = list.getFirst();  // ← Nuevo en Java 21
        String last = list.getLast();    // ← Nuevo en Java 21
        List<String> reversed = list.reversed();  // ← Nuevo en Java 21

        return new SequencedListDemo(list, first, last, reversed);
    }

    /**
     * Demuestra SequencedSet con LinkedHashSet
     */
    public SequencedSetDemo demonstrateSequencedSet() {
        log.info("Demonstrating SequencedSet");

        LinkedHashSet<Integer> set = new LinkedHashSet<>();

        // ¡Ahora LinkedHashSet tiene estos métodos!
        set.addFirst(10);  // ← ¡Nuevo en Java 21!
        set.addLast(20);   // ← ¡Nuevo en Java 21!
        set.addFirst(5);

        Integer first = set.getFirst();  // ← ¡Nuevo!
        Integer last = set.getLast();    // ← ¡Nuevo!
        SequencedSet<Integer> reversed = set.reversed();  // ← ¡Nuevo!

        return new SequencedSetDemo(
            new ArrayList<>(set),
            first,
            last,
            new ArrayList<>(reversed)
        );
    }

    /**
     * Demuestra SequencedMap con LinkedHashMap
     */
    public SequencedMapDemo demonstrateSequencedMap() {
        log.info("Demonstrating SequencedMap");

        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);

        // Métodos de SequencedMap
        Map.Entry<String, Integer> firstEntry = map.firstEntry();  // ← Nuevo
        Map.Entry<String, Integer> lastEntry = map.lastEntry();    // ← Nuevo
        SequencedMap<String, Integer> reversed = map.reversed();   // ← Nuevo

        return new SequencedMapDemo(map, firstEntry, lastEntry, reversed);
    }
}
```

## Comparación Detallada

| Operación | Antes de Java 21 | Java 21 con Sequenced Collections |
|-----------|------------------|-----------------------------------|
| Primer elemento | `list.get(0)` o `set.iterator().next()` | `collection.getFirst()` |
| Último elemento | `list.get(list.size()-1)` o iterar todo | `collection.getLast()` |
| Agregar al inicio | `list.add(0, elem)` (no disponible en Set) | `collection.addFirst(elem)` |
| Agregar al final | `list.add(elem)` | `collection.addLast(elem)` |
| Vista reversa | `Collections.reverse()` o streams | `collection.reversed()` |
| Consistencia API | Diferente para cada tipo | Uniforme para todos |

## Buenas Prácticas

### ✅ DO: Usar Interfaz SequencedCollection

```java
// ✅ CORRECTO: Programar contra la interfaz
public void processItems(SequencedCollection<Item> items) {
    Item first = items.getFirst();
    Item last = items.getLast();
    // Funciona con List, LinkedHashSet, Deque, etc.
}

// ❌ INCORRECTO: Depender de implementación específica
public void processItems(ArrayList<Item> items) {
    // Limitado solo a ArrayList
}
```

### ✅ DO: Usar reversed() para Iteración Inversa

```java
// ✅ CORRECTO: Vista reversa eficiente
SequencedCollection<String> collection = new ArrayList<>();
for (String item : collection.reversed()) {
    System.out.println(item);
}

// ❌ INCORRECTO: Crear nueva lista
List<String> list = new ArrayList<>(collection);
Collections.reverse(list);  // Modifica la lista!
for (String item : list) {
    System.out.println(item);
}
```

### ✅ DO: Aprovechar putFirst/putLast en Maps

```java
// ✅ CORRECTO: Controlar orden de inserción
SequencedMap<String, Integer> map = new LinkedHashMap<>();
map.putFirst("important", 1);  // Al principio
map.putLast("normal", 2);      // Al final

// ❌ ANTES: No había forma de controlar el orden
Map<String, Integer> map = new LinkedHashMap<>();
map.put("normal", 2);
// ¿Cómo poner "important" al principio? ¡Imposible!
```

### ⚠️ CUIDADO: reversed() es una Vista

```java
// ⚠️ La vista reversed() refleja cambios en la colección original
SequencedCollection<String> original = new ArrayList<>(List.of("A", "B", "C"));
SequencedCollection<String> reversed = original.reversed();

original.addLast("D");
System.out.println(reversed.getFirst());  // "D" - ¡refleja el cambio!

// Si necesitas una copia independiente:
List<String> reversedCopy = new ArrayList<>(original.reversed());
```

### ⚠️ CUIDADO: Performance en ArrayList.addFirst()

```java
// ⚠️ addFirst() en ArrayList es O(n) - ineficiente
ArrayList<String> arrayList = new ArrayList<>();
arrayList.addFirst("item");  // Requiere desplazar todos los elementos

// ✅ MEJOR: Usar LinkedList o ArrayDeque para operaciones frecuentes al inicio
LinkedList<String> linkedList = new LinkedList<>();
linkedList.addFirst("item");  // O(1) - eficiente
```

## Limitaciones y Consideraciones

1. **Performance**: `addFirst()` en `ArrayList` es O(n), usa `LinkedList` o `Deque` si necesitas esto frecuentemente

2. **Vistas vs Copias**: `reversed()` retorna una vista, no una copia. Cambios en una afectan a la otra

3. **Null Handling**: Algunos métodos pueden lanzar `NoSuchElementException` si la colección está vacía

4. **Inmutables**: Las colecciones inmutables (de `List.of()`, etc.) lanzan `UnsupportedOperationException` con `addFirst()`/`addLast()`

## Conclusión

Sequenced Collections resuelve inconsistencias históricas en las APIs de colecciones de Java:

- **Uniformidad**: API consistente para todas las colecciones ordenadas
- **Completitud**: Operaciones que faltaban ahora están disponibles
- **Eficiencia**: Métodos optimizados para cada implementación
- **Simplicidad**: Código más claro y mantenible

Es una mejora fundamental que hace el trabajo con colecciones más intuitivo y predecible.

## Referencias

- **JEP 431**: Sequenced Collections
- **Documentación oficial**: https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/SequencedCollection.html

## Ejemplos en el Proyecto

- Implementación: `src/main/java/com/monghit/java21test/features/sequencedcollections/`
- Tests: `src/test/java/com/monghit/java21test/features/sequencedcollections/`
- Endpoints: `/api/sequenced-collections/**`
