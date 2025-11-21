package com.unimag.main;

import com.unimag.eval.Evaluator;
import com.unimag.lexer.Lexer;
import com.unimag.lexer.Token;
import com.unimag.parser.Parser;
import com.unimag.parser.astNodes.Node;

import java.util.List;
import java.util.Scanner;

/**
 * Programa principal - Parser y Evaluador de Expresiones Trigonométricas
 *
 * CARACTERÍSTICAS:
 * - Tokenización de expresiones matemáticas
 * - Parser recursivo descendente con gramática LL(1)
 * - Evaluación de expresiones con funciones trigonométricas
 * - Soporte para variables y constantes (pi, e)
 * - Manejo robusto de errores
 *
 * PRECEDENCIA (de mayor a menor):
 * 1. Funciones: sin(), cos(), tan()
 * 2. Potencia: ^ (asociatividad DERECHA)
 * 3. Negación unaria: -
 * 4. Multiplicación/División: *, /
 * 5. Suma/Resta: +, -
 *
 * EJEMPLOS:
 * - 3 + 4 * 2        → 11
 * - -2^2             → -4
 * - 2^3^2            → 512
 * - sin(pi/2)        → 1.0
 * - x*2+y (x=3,y=4)  → 10
 */
public class Main {
    private static final String BANNER = """
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
            """;

    private static final String HELP = """

            AYUDA - Parser de Expresiones Trigonométricas
            ===============================================

            OPERADORES SOPORTADOS:
              +, -        Suma y resta
              *, /        Multiplicación y división
              ^           Potencia (asociatividad derecha: 2^3^2 = 2^(3^2))
              -           Negación unaria (-2^2 = -4)

            FUNCIONES TRIGONOMÉTRICAS:
              sin(x)      Seno (en radianes)
              cos(x)      Coseno (en radianes)
              tan(x)      Tangente (en radianes)

            CONSTANTES:
              pi          π ≈ 3.14159265...
              e           e ≈ 2.71828182...

            VARIABLES:
              Cualquier identificador (x, y, z, abc, etc.)
              El sistema pedirá sus valores automáticamente

            EJEMPLOS:
              3 + 4 * 2              → 11
              (3 + 4) * 2            → 14
              -2^2                   → -4
              2^3^2                  → 512
              sin(pi/2)              → 1.0
              cos(0) + sin(pi)       → 1.0
              x*2+y  (x=3, y=4)      → 10
              2*pi                   → 6.28318...
              e^1                    → 2.71828...

            PRECEDENCIA (mayor a menor):
              1. Funciones trigonométricas
              2. Potencia ^
              3. Negación unaria -
              4. Multiplicación/División *, /
              5. Suma/Resta +, -
            """;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Mostrar banner
        System.out.println(BANNER);

        // Modo interactivo
        while (true) {
            try {
                System.out.print("Expresión> ");
                String input = scanner.nextLine().trim();

                // Comandos especiales
                if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("salir")) {
                    System.out.println("\n¡Hasta luego!");
                    break;
                }

                if (input.equalsIgnoreCase("help") || input.equalsIgnoreCase("ayuda")) {
                    System.out.println(HELP);
                    continue;
                }

                if (input.equalsIgnoreCase("test") || input.equalsIgnoreCase("pruebas")) {
                    System.out.println("\nEjecutando casos de prueba...");
                    System.out.println("Usa: java -cp target/classes com.unimag.tests.TestRunner");
                    continue;
                }

                if (input.isEmpty()) {
                    continue;
                }

                // Procesar expresión
                processExpression(input, scanner);

            } catch (Exception e) {
                System.err.println("\n❌ " + e.getMessage() + "\n");
            }
        }

        scanner.close();
    }

    /**
     * Procesa y evalúa una expresión
     */
    private static void processExpression(String input, Scanner scanner) throws Exception {
        // 1. TOKENIZACIÓN
        System.out.println("\n--- FASE 1: TOKENIZACIÓN ---");
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenize();

        System.out.println("Tokens generados: " + tokens.size());
        for (Token token : tokens) {
            if (token.type().name().equals("EOF")) continue;
            System.out.println("  " + token);
        }

        // 2. PARSING
        System.out.println("\n--- FASE 2: ANÁLISIS SINTÁCTICO ---");
        Parser parser = new Parser(tokens);
        Node ast = parser.parse();
        System.out.println("✓ AST construido correctamente");

        // 3. EVALUACIÓN
        System.out.println("\n--- FASE 3: EVALUACIÓN ---");
        Evaluator evaluator = new Evaluator(ast);

        // Recolectar y pedir variables
        evaluator.requestVariableValues(scanner);

        // Evaluar
        double result = evaluator.evaluate();

        // Mostrar resultado
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.printf("║  RESULTADO: %-31.10f ║%n", result);
        System.out.println("╚══════════════════════════════════════════════╝\n");
    }
}