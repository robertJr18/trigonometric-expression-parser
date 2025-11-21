package com.unimag.parser.astNodes;

import java.util.Map;
import java.util.Set;

/**
 * Nodo del AST que representa una función trigonométrica
 * Funciones soportadas: sin, cos, tan
 */
public class FunctionNode extends Node {
    private final String name;
    private final Node argument;

    public FunctionNode(String name, Node argument) {
        this.name = name;
        this.argument = argument;
    }

    @Override
    public double evaluate(Map<String, Double> env) throws Exception {
        double arg = argument.evaluate(env);

        return switch (name) {
            case "sin", "sen" -> Math.sin(arg);
            case "cos" -> Math.cos(arg);
            case "tan" -> Math.tan(arg);
            default -> throw new RuntimeException(
                String.format("Función desconocida: '%s'", name)
            );
        };
    }

    @Override
    public void collectVariables(Set<String> vars) {
        // Recolectar variables del argumento
        argument.collectVariables(vars);
    }

    public String getName() {
        return name;
    }

    public Node getArgument() {
        return argument;
    }
}
