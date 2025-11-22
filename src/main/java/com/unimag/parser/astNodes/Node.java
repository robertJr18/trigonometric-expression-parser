package com.unimag.parser.astNodes;

import java.util.Map;
import java.util.Set;

public abstract class Node {

    public abstract double evaluate(Map<String, Double> env) throws Exception;

    public abstract void collectVariables(Set<String> vars);
}
