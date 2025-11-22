package com.unimag.parser.astNodes;

import java.util.Map;
import java.util.Set;

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
