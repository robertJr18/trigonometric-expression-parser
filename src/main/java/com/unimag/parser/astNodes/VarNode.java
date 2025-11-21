package com.unimag.parser.astNodes;

import java.util.Map;
import java.util.Set;

/**
 * Nodo del AST que representa una variable
 */
public class VarNode extends Node {
    private final String identifier;

    public VarNode(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public double evaluate(Map<String, Double> env) throws Exception {
        if (!env.containsKey(identifier)) {
            throw new RuntimeException(
                String.format("Error semántico: variable '%s' no está definida", identifier)
            );
        }
        return env.get(identifier);
    }

    @Override
    public void collectVariables(Set<String> vars) {
        // Agregar esta variable al conjunto
        vars.add(identifier);
    }

    public String getIdentifier() {
        return identifier;
    }
}
