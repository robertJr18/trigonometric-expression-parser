package com.unimag.parser.astNodes;

import java.util.Map;
import java.util.Set;

/**
 * Clase base abstracta para todos los nodos del AST
 */
public abstract class Node {
    /**
     * Evalúa el nodo y devuelve el resultado numérico
     * @param env Entorno con valores de variables
     * @return Resultado de la evaluación
     * @throws Exception Si hay errores durante la evaluación
     */
    public abstract double evaluate(Map<String, Double> env) throws Exception;

    /**
     * Recolecta todas las variables usadas en este nodo y sus descendientes
     * @param vars Conjunto donde se almacenan las variables encontradas
     */
    public abstract void collectVariables(Set<String> vars);
}
