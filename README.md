# Parser y Evaluador de Expresiones Trigonométricas en Java

## 📋 Descripción

Sistema completo de análisis léxico, sintáctico y evaluación de expresiones matemáticas con funciones trigonométricas, implementado en Java para el curso de Compiladores.

## ✨ Características Principales

### Funcionalidad Completa
- ✅ Operadores aritméticos: `+`, `-`, `*`, `/`, `^` (potencia)
- ✅ Funciones trigonométricas: `sin()`, `cos()`, `tan()`
- ✅ Paréntesis para agrupación `()`
- ✅ Números reales: `3.14`, `0.5`, `.5`
- ✅ Variables simbólicas: `x`, `y`, `z`, etc.
- ✅ Constantes: `pi` (π), `e` (número de Euler)
- ✅ Operador unario de negación: `-x`
- ✅ Detección automática de variables
- ✅ Evaluación con resultado numérico (double)
- ✅ Manejo robusto de errores con posición y mensajes claros

### Precedencia y Asociatividad (CRÍTICO)

**Precedencia (de mayor a menor):**
1. **Funciones trigonométricas**: `sin()`, `cos()`, `tan()`
2. **Potencia**: `^` (asociatividad a la DERECHA)
3. **Negación unaria**: `-` (mayor que `*` y `/`)
4. **Multiplicación y división**: `*`, `/` (asociatividad a la izquierda)
5. **Suma y resta**: `+`, `-` (asociatividad a la izquierda)

**Ejemplos críticos:**
- `2^3^2` → `2^(3^2) = 512` ✅ (NO `8^2 = 64` ❌)
- `-2^2` → `-(2^2) = -4` ✅ (NO `(-2)^2 = 4` ❌)
- `3+4*2` → `3+(4*2) = 11` ✅ (NO `(3+4)*2 = 14` ❌)

## 🏗️ Arquitectura del Sistema

### Gramática LL(1)

```
E  → T E'                         // Expresión
E' → + T E' | - T E' | ε          // Suma/resta (baja precedencia)

T  → U T'                         // Término
T' → * U T' | / U T' | ε          // Mult/div (media precedencia)

U  → - U | F                      // Unario (negación)

F  → P F'                         // Factor
F' → ^ U F' | ε                   // Potencia (derecha-asociativa, alta precedencia)

P  → NUM                          // Número
   | ID                           // Variable o constante (pi, e)
   | sin ( E )                    // Función seno
   | cos ( E )                    // Función coseno
   | tan ( E )                    // Función tangente
   | ( E )                        // Expresión entre paréntesis
```

### Estructura del Proyecto

```
src/main/java/com/unimag/
├── lexer/
│   ├── Token.java          # Definición de token (record)
│   ├── TokenType.java      # Tipos de tokens (enum)
│   └── Lexer.java          # Analizador léxico
├── parser/
│   ├── Parser.java         # Analizador sintáctico (parser recursivo descendente)
│   └── astNodes/
│       ├── Node.java       # Clase base abstracta
│       ├── NumberNode.java # Nodo para números
│       ├── VarNode.java    # Nodo para variables
│       ├── UnaryNode.java  # Nodo para negación unaria
│       ├── BinaryNode.java # Nodo para operadores binarios
│       └── FunctionNode.java # Nodo para funciones trigonométricas
├── eval/
│   └── Evaluator.java      # Evaluador de expresiones y manejo de variables
├── tests/
│   └── TestRunner.java     # Suite de 30 casos de prueba
└── main/
    └── Main.java           # Programa principal interactivo
```

## 🚀 Compilación y Ejecución

### Requisitos
- **Java 17** o superior (para text blocks y switch expressions)
- **Maven** (opcional) o **javac**

### Opción 1: Compilar con javac

```bash
# Compilar todo el proyecto
javac -d target/classes -sourcepath src/main/java \
    src/main/java/com/unimag/main/Main.java \
    src/main/java/com/unimag/tests/TestRunner.java

# Ejecutar el programa principal (modo interactivo)
java -cp target/classes com.unimag.main.Main

# Ejecutar la suite de pruebas
java -cp target/classes com.unimag.tests.TestRunner
```

### Opción 2: Compilar con Maven

```bash
# Compilar
mvn clean compile

# Ejecutar programa principal
mvn exec:java -Dexec.mainClass="com.unimag.main.Main"

# Ejecutar tests
mvn exec:java -Dexec.mainClass="com.unimag.tests.TestRunner"
```

## 📝 Uso del Programa Interactivo

```
╔════════════════════════════════════════════════════════════╗
║   PARSER Y EVALUADOR DE EXPRESIONES TRIGONOMÉTRICAS       ║
║                                                            ║
║   Operadores: +, -, *, /, ^                               ║
║   Funciones:  sin, cos, tan                               ║
║   Constantes: pi, e                                       ║
║   Variables:  x, y, z, etc.                               ║
║                                                            ║
║   Comandos:   'exit' para salir                           ║
║               'help' para ayuda                           ║
║               'test' para ejecutar pruebas                ║
╚════════════════════════════════════════════════════════════╝

Expresión> sin(pi/2)

--- FASE 1: TOKENIZACIÓN ---
Tokens generados: 7
  Token{ Type: SIN, Value: sin, Position: 0 }
  Token{ Type: L_PAREN, Value: (, Position: 3 }
  Token{ Type: PI, Value: pi, Position: 4 }
  Token{ Type: DIV, Value: /, Position: 6 }
  Token{ Type: NUMBER, Value: 2, Position: 7 }
  Token{ Type: R_PAREN, Value: ), Position: 8 }

--- FASE 2: ANÁLISIS SINTÁCTICO ---
✓ AST construido correctamente

--- FASE 3: EVALUACIÓN ---

╔══════════════════════════════════════════════╗
║  RESULTADO: 1.0000000000                     ║
╚══════════════════════════════════════════════╝

Expresión> x*2+y

Variables detectadas: x, y

Ingrese valor para 'x': 3
Ingrese valor para 'y': 4

╔══════════════════════════════════════════════╗
║  RESULTADO: 10.0000000000                    ║
╚══════════════════════════════════════════════╝

Expresión> exit

¡Hasta luego!
```

## 🧪 Casos de Prueba

El sistema incluye **30 casos de prueba** automatizados:

### Casos Correctos (15)
1. `3 + 4 * 2` → `11.0`
2. `(3 + 4) * 2` → `14.0`
3. `-2^2` → `-4.0` (precedencia crítica)
4. `2^3^2` → `512.0` (asociatividad derecha)
5. `sin(pi/2)` → `1.0`
6. `cos(0) + sin(pi)` → `1.0`
7. `x*2+y` (x=3, y=4) → `10.0`
8. `3.5 * 2.0 + .5` → `7.5`
9. `tan(pi/4)` → `1.0`
10. `2^(1/2)` → `1.41421...` (√2)
11. `cos(x)^2 + sin(x)^2` → `1.0` (identidad trigonométrica)
12. `sin(cos(x))` → `0.8414...`
13. `-(-5)` → `5.0`
14. `e^1` → `2.71828...`
15. `2*pi` → `6.28318...`

### Casos Erróneos (15)
16. `3 + * 4` → Error sintáctico
17. `5..3` → Error léxico (número mal formado)
18. `(3+4` → Error: paréntesis sin cerrar
19. `3+4)` → Error: paréntesis extra
20. `` (vacío) → Error: expresión vacía
21. `1/0` → Error: división por cero
22. `2^` → Error: operador sin operando
23. `3 @ 4` → Error: carácter inválido
24. `sin 1` → Error: función sin paréntesis
25. `3 ++ 4` → Error: múltiples operadores
26. `()` → Error: expresión vacía en paréntesis
27. `sin()` → Error: función sin argumento
28. `a + 3` → Error: variable no definida
29. `3 + .` → Error: punto sin dígitos
30. `* 3` → Error: operador al inicio

## 📊 Resultados de Pruebas

```
╔══════════════════════════════════════════════════════════╗
║  RESUMEN:  Total: 30  |  Pasadas: 30  |  Fallidas:  0  ║
╚══════════════════════════════════════════════════════════╝

✅ ¡TODAS LAS PRUEBAS PASARON!
```

## 🔍 Manejo de Errores

El sistema proporciona mensajes de error claros y específicos:

### Errores Léxicos
```
Error léxico en posición 2: carácter desconocido '@'
```

### Errores Sintácticos
```
Error sintáctico en posición 4: se esperaba ')' para cerrar paréntesis
  Token actual: Token{ Type: EOF, Value: , Position: 4 }
```

### Errores Semánticos
```
Error semántico: variable 'a' no está definida
```

### Errores de Ejecución
```
Error de ejecución: división por cero
```

## 🎓 Conceptos Implementados

### Análisis Léxico (Lexer)
- Reconocimiento de tokens
- Manejo de espacios en blanco
- Números decimales (incluyendo `.5`)
- Palabras reservadas
- Detección de errores léxicos

### Análisis Sintáctico (Parser)
- Parser recursivo descendente
- Gramática LL(1)
- Construcción de AST
- Precedencia de operadores
- Asociatividad (izquierda y derecha)
- Manejo de errores sintácticos

### Evaluación (Evaluator)
- Recorrido del AST
- Evaluación de expresiones
- Manejo de entorno de variables
- Funciones matemáticas (sin, cos, tan, pow)
- Constantes (π, e)
- Detección de errores en tiempo de ejecución

## 👨‍💻 Autor

Proyecto desarrollado para el curso de **Compiladores** - Universidad del Magdalena

## 📄 Licencia

Este proyecto es de código abierto para fines educativos.

---

**Nota**: Las funciones trigonométricas trabajan en **RADIANES**, no en grados.
- `sin(pi/2)` = 1.0 ✅
- `sin(90)` ≠ 1.0 ❌ (debe convertirse: 90° × π/180)
