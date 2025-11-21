package com.unimag.eval;

import com.unimag.parser.astNodes.Node;

import java.util.*;

public class Evaluator {
    private final Node ast;
    private final Map<String, Double> environment;

    public Evaluator(Node ast) {
        this.ast = ast;
        this.environment = new HashMap<>();
    }

    public Set<String> collectVariables() {
        Set<String> variables = new HashSet<>();
        ast.collectVariables(variables);
        return variables;
    }

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


    public void setVariable(String varName, double value) {
        environment.put(varName, value);
    }

    public void setVariables(Map<String, Double> variables) {
        environment.putAll(variables);
    }

    public double evaluate() throws Exception {
        return ast.evaluate(environment);
    }

    public Map<String, Double> getEnvironment() {
        return new HashMap<>(environment);
    }
}
