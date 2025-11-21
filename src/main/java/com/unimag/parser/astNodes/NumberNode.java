package com.unimag.parser.astNodes;

import java.util.Map;
import java.util.Set;

/**
 * Nodo del AST que representa un número literal
 */
public class NumberNode extends Node {
    private final double value;

    public NumberNode(double value) {
        this.value = value;
    }

    @Override
    public double evaluate(Map<String, Double> env) throws Exception {
        return value;
    }

    @Override
    public void collectVariables(Set<String> vars) {
        // Los números no tienen variables
    }

    public double getValue() {
        return value;
    }
}
