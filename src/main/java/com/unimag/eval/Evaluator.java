package com.unimag.eval;

import com.unimag.parser.astNodes.Node;

import java.util.*;

/**
 * Evaluador de expresiones
 * Recolecta variables, solicita valores al usuario y evalúa el AST
 */
public class Evaluator {
    private final Node ast;
    private final Map<String, Double> environment;

    /**
     * Constructor del Evaluador
     * @param ast Árbol de sintaxis abstracta a evaluar
     */
    public Evaluator(Node ast) {
        this.ast = ast;
        this.environment = new HashMap<>();
    }

    /**
     * Recolecta todas las variables usadas en la expresión
     * @return Conjunto de nombres de variables
     */
    public Set<String> collectVariables() {
        Set<String> variables = new HashSet<>();
        ast.collectVariables(variables);
        return variables;
    }

    /**
     * Solicita valores de variables al usuario
     * @param scanner Scanner para leer entrada del usuario
     */
    public void requestVariableValues(Scanner scanner) {
        Set<String> variables = collectVariables();

        if (variables.isEmpty()) {
            return;
        }

        System.out.println("\nVariables detectadas: " + String.join(", ", variables));
        System.out.println();

        for (String var : variables) {
            System.out.print("Ingrese valor para '" + var + "': ");
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                environment.put(var, value);
            } catch (NumberFormatException e) {
                throw new RuntimeException(
                    String.format("Error: valor inválido para variable '%s'. Se esperaba un número.", var)
                );
            }
        }
    }

    /**
     * Establece valores de variables programáticamente (útil para pruebas)
     * @param varName Nombre de la variable
     * @param value Valor de la variable
     */
    public void setVariable(String varName, double value) {
        environment.put(varName, value);
    }

    /**
     * Establece múltiples variables a la vez
     * @param variables Mapa de variables y sus valores
     */
    public void setVariables(Map<String, Double> variables) {
        environment.putAll(variables);
    }

    /**
     * Evalúa la expresión con las variables actuales
     * @return Resultado de la evaluación
     * @throws Exception Si hay errores durante la evaluación
     */
    public double evaluate() throws Exception {
        return ast.evaluate(environment);
    }

    /**
     * Obtiene el entorno actual de variables
     * @return Mapa de variables
     */
    public Map<String, Double> getEnvironment() {
        return new HashMap<>(environment);
    }
}
