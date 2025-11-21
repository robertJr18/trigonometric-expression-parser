package com.unimag.parser.astNodes;

import java.util.Map;
import java.util.Set;

/**
 * Nodo del AST que representa una operación binaria
 * Operadores soportados: +, -, *, /, ^
 */
public class BinaryNode extends Node {
    private final char operator;
    private final Node left;
    private final Node right;

    public BinaryNode(char operator, Node left, Node right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public double evaluate(Map<String, Double> env) throws Exception {
        double l = left.evaluate(env);
        double r = right.evaluate(env);

        return switch (operator) {
            case '+' -> l + r;
            case '-' -> l - r;
            case '*' -> l * r;
            case '/' -> {
                if (r == 0) {
                    throw new ArithmeticException(
                        "Error de ejecución: división por cero"
                    );
                }
                yield l / r;
            }
            case '^' -> Math.pow(l, r);
            default -> throw new RuntimeException(
                String.format("Operador desconocido: '%c'", operator)
            );
        };
    }

    @Override
    public void collectVariables(Set<String> vars) {
        // Recolectar variables de ambos lados
        left.collectVariables(vars);
        right.collectVariables(vars);
    }

    public char getOperator() {
        return operator;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }
}
