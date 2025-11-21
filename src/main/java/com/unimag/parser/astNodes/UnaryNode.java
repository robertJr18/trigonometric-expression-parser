package com.unimag.parser.astNodes;

import java.util.Map;
import java.util.Set;

/**
 * Nodo del AST que representa una operación unaria (negación)
 */
public class UnaryNode extends Node {
    private final char operator;
    private final Node expression;

    public UnaryNode(Node expr) {
        this.operator = '-';
        this.expression = expr;
    }

    @Override
    public double evaluate(Map<String, Double> env) throws Exception {
        return -expression.evaluate(env);
    }

    @Override
    public void collectVariables(Set<String> vars) {
        // Recolectar variables de la subexpresión
        expression.collectVariables(vars);
    }

    public Node getExpression() {
        return expression;
    }

    public char getOperator() {
        return operator;
    }
}
