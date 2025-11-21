package com.unimag.tests;

import com.unimag.eval.Evaluator;
import com.unimag.lexer.Lexer;
import com.unimag.parser.Parser;
import com.unimag.parser.astNodes.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * Suite de pruebas para el Parser y Evaluador de Expresiones Trigonométricas
 * Incluye 25+ casos de prueba (correctos y erróneos)
 */
public class TestRunner {
    private static int testsPassed = 0;
    private static int testsFailed = 0;
    private static int totalTests = 0;

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║   SUITE DE PRUEBAS - PARSER DE EXPRESIONES TRIG.        ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝\n");

        // CASOS CORRECTOS
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("CASOS CORRECTOS (15 pruebas)");
        System.out.println("═══════════════════════════════════════════════════════════\n");

        // 1. Precedencia básica
        testExpression("3 + 4 * 2", null, 11.0, "Precedencia: multiplicación antes que suma");

        // 2. Paréntesis
        testExpression("(3 + 4) * 2", null, 14.0, "Paréntesis cambian precedencia");

        // 3. Negación unaria con potencia
        testExpression("-2^2", null, -4.0, "Negación unaria: -(2^2) = -4");

        // 4. Potencia asociativa derecha
        testExpression("2^3^2", null, 512.0, "Potencia derecha: 2^(3^2) = 512");

        // 5. Función sin con constante pi
        testExpression("sin(pi/2)", null, 1.0, "sin(π/2) = 1.0", 0.0001);

        // 6. Suma de funciones trigonométricas
        testExpression("cos(0) + sin(pi)", null, 1.0, "cos(0) + sin(π) ≈ 1.0", 0.0001);

        // 7. Variable con operación
        Map<String, Double> vars1 = new HashMap<>();
        vars1.put("x", 3.0);
        vars1.put("y", 4.0);
        testExpression("x*2+y", vars1, 10.0, "Variables: x*2+y con x=3, y=4");

        // 8. Números decimales
        testExpression("3.5 * 2.0 + .5", null, 7.5, "Números decimales: 3.5 * 2.0 + 0.5");

        // 9. Función tan
        testExpression("tan(pi/4)", null, 1.0, "tan(π/4) = 1.0", 0.0001);

        // 10. Raíz cuadrada con potencia
        testExpression("2^(1/2)", null, 1.41421356, "Raíz cuadrada: 2^(1/2)", 0.00001);

        // 11. Identidad trigonométrica
        Map<String, Double> vars2 = new HashMap<>();
        vars2.put("x", 0.5);
        testExpression("cos(x)^2 + sin(x)^2", vars2, 1.0, "Identidad: cos²(x) + sin²(x) = 1", 0.0001);

        // 12. Composición de funciones
        Map<String, Double> vars3 = new HashMap<>();
        vars3.put("x", 0.0);
        testExpression("sin(cos(x))", vars3, 0.8414709848, "sin(cos(0)) ≈ 0.8414", 0.0001);

        // 13. Doble negación
        testExpression("-(-5)", null, 5.0, "Doble negación: -(-5) = 5");

        // 14. Constante e
        testExpression("e^1", null, Math.E, "Constante e: e^1 ≈ 2.71828", 0.00001);

        // 15. Múltiplo de pi
        testExpression("2*pi", null, 2*Math.PI, "2*π ≈ 6.28318", 0.00001);

        // CASOS ERRÓNEOS
        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("CASOS ERRÓNEOS (15 pruebas)");
        System.out.println("═══════════════════════════════════════════════════════════\n");

        // 16. Error sintáctico: operador sin operando
        testExpressionError("3 + * 4", "Error sintáctico: operador sin operando");

        // 17. Error léxico: múltiples puntos
        testExpressionError("5..3", "Error léxico: número mal formado");

        // 18. Error: paréntesis sin cerrar
        testExpressionError("(3+4", "Error sintáctico: paréntesis sin cerrar");

        // 19. Error: paréntesis extra
        testExpressionError("3+4)", "Error sintáctico: paréntesis extra");

        // 20. Error: expresión vacía
        testExpressionError("", "Error: expresión vacía");

        // 21. Error: división por cero
        testDivisionByZero("1/0", "Error de ejecución: división por cero");

        // 22. Error: operador sin operando derecho
        testExpressionError("2^", "Error sintáctico: operador sin operando derecho");

        // 23. Error: carácter inválido
        testExpressionError("3 @ 4", "Error léxico: carácter inválido");

        // 24. Error: función sin paréntesis
        testExpressionError("sin 1", "Error sintáctico: función sin paréntesis");

        // 25. Error: múltiples operadores
        testExpressionError("3 ++ 4", "Error sintáctico: múltiples operadores");

        // 26. Error: paréntesis vacío
        testExpressionError("()", "Error sintáctico: expresión vacía en paréntesis");

        // 27. Error: función sin argumento
        testExpressionError("sin()", "Error sintáctico: función sin argumento");

        // 28. Error: variable no definida
        testVariableNotDefined("a + 3", "Error semántico: variable no definida");

        // 29. Error: número mal formado (punto sin dígitos)
        testExpressionError("3 + .", "Error léxico: punto sin dígitos");

        // 30. Error: operador al inicio
        testExpressionError("* 3", "Error sintáctico: operador al inicio");

        // RESUMEN
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.printf("║  RESUMEN:  Total: %2d  |  Pasadas: %2d  |  Fallidas: %2d  ║%n",
                totalTests, testsPassed, testsFailed);
        System.out.println("╚══════════════════════════════════════════════════════════╝\n");

        if (testsFailed == 0) {
            System.out.println("✅ ¡TODAS LAS PRUEBAS PASARON!");
        } else {
            System.out.println("❌ Algunas pruebas fallaron. Revise los errores arriba.");
        }
    }

    /**
     * Prueba una expresión que debe evaluarse correctamente
     */
    private static void testExpression(String expression, Map<String, Double> variables,
                                        double expected, String description) {
        testExpression(expression, variables, expected, description, 0.000001);
    }

    /**
     * Prueba una expresión que debe evaluarse correctamente (con tolerancia personalizada)
     */
    private static void testExpression(String expression, Map<String, Double> variables,
                                        double expected, String description, double tolerance) {
        totalTests++;
        System.out.printf("Test %d: %s%n", totalTests, description);
        System.out.printf("  Expresión: %s%n", expression);

        try {
            Lexer lexer = new Lexer(expression);
            Parser parser = new Parser(lexer.tokenize());
            Node ast = parser.parse();

            Evaluator evaluator = new Evaluator(ast);
            if (variables != null) {
                evaluator.setVariables(variables);
            }

            double result = evaluator.evaluate();

            if (Math.abs(result - expected) < tolerance) {
                System.out.printf("  ✅ PASÓ - Resultado: %.10f (esperado: %.10f)%n%n", result, expected);
                testsPassed++;
            } else {
                System.out.printf("  ❌ FALLÓ - Resultado: %.10f (esperado: %.10f)%n%n", result, expected);
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.printf("  ❌ FALLÓ - Error inesperado: %s%n%n", e.getMessage());
            testsFailed++;
        }
    }

    /**
     * Prueba una expresión que debe producir un error
     */
    private static void testExpressionError(String expression, String description) {
        totalTests++;
        System.out.printf("Test %d: %s%n", totalTests, description);
        System.out.printf("  Expresión: %s%n", expression);

        try {
            Lexer lexer = new Lexer(expression);
            Parser parser = new Parser(lexer.tokenize());
            Node ast = parser.parse();

            Evaluator evaluator = new Evaluator(ast);
            double result = evaluator.evaluate();

            System.out.printf("  ❌ FALLÓ - Se esperaba error pero se obtuvo: %.10f%n%n", result);
            testsFailed++;
        } catch (Exception e) {
            System.out.printf("  ✅ PASÓ - Error capturado correctamente: %s%n%n", e.getMessage());
            testsPassed++;
        }
    }

    /**
     * Prueba específica para división por cero
     */
    private static void testDivisionByZero(String expression, String description) {
        totalTests++;
        System.out.printf("Test %d: %s%n", totalTests, description);
        System.out.printf("  Expresión: %s%n", expression);

        try {
            Lexer lexer = new Lexer(expression);
            Parser parser = new Parser(lexer.tokenize());
            Node ast = parser.parse();

            Evaluator evaluator = new Evaluator(ast);
            double result = evaluator.evaluate();

            System.out.printf("  ❌ FALLÓ - Se esperaba ArithmeticException pero se obtuvo: %.10f%n%n", result);
            testsFailed++;
        } catch (ArithmeticException e) {
            System.out.printf("  ✅ PASÓ - Error capturado: %s%n%n", e.getMessage());
            testsPassed++;
        } catch (Exception e) {
            System.out.printf("  ❌ FALLÓ - Error incorrecto: %s%n%n", e.getMessage());
            testsFailed++;
        }
    }

    /**
     * Prueba específica para variable no definida
     */
    private static void testVariableNotDefined(String expression, String description) {
        totalTests++;
        System.out.printf("Test %d: %s%n", totalTests, description);
        System.out.printf("  Expresión: %s%n", expression);

        try {
            Lexer lexer = new Lexer(expression);
            Parser parser = new Parser(lexer.tokenize());
            Node ast = parser.parse();

            Evaluator evaluator = new Evaluator(ast);
            // No establecer variables
            double result = evaluator.evaluate();

            System.out.printf("  ❌ FALLÓ - Se esperaba error de variable no definida pero se obtuvo: %.10f%n%n", result);
            testsFailed++;
        } catch (RuntimeException e) {
            if (e.getMessage().contains("variable") && e.getMessage().contains("no está definida")) {
                System.out.printf("  ✅ PASÓ - Error capturado: %s%n%n", e.getMessage());
                testsPassed++;
            } else {
                System.out.printf("  ❌ FALLÓ - Error incorrecto: %s%n%n", e.getMessage());
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.printf("  ❌ FALLÓ - Error incorrecto: %s%n%n", e.getMessage());
            testsFailed++;
        }
    }
}
