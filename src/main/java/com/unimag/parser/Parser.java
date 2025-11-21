package com.unimag.parser;

import com.unimag.lexer.Token;
import com.unimag.lexer.TokenType;
import com.unimag.parser.astNodes.*;

import java.util.List;

/**
 * Parser recursivo descendente para expresiones trigonométricas
 * Implementa la siguiente gramática LL(1) con precedencia correcta:
 *
 * E  → T E'                         // Expresión
 * E' → + T E' | - T E' | ε          // Suma/resta (baja precedencia)
 * T  → F T'                         // Término
 * T' → * F T' | / F T' | ε          // Mult/div (media precedencia)
 * F  → U F'                         // Factor
 * F' → ^ F | ε                      // Potencia (derecha-asociativa, alta precedencia)
 * U  → - U | P                      // Unario (negación)
 * P  → NUM | ID | sin(E) | cos(E) | tan(E) | (E)  // Primario
 *
 * PRECEDENCIA (de mayor a menor):
 * 1. Funciones trigonométricas: sin(), cos(), tan()
 * 2. Potencia: ^ (asociatividad a la DERECHA)
 * 3. Negación unaria: -
 * 4. Multiplicación y división: *, /
 * 5. Suma y resta: +, -
 */
public class Parser {
    private final List<Token> tokens;
    private int currentIndex;
    private Token currentToken;

    /**
     * Constructor del Parser
     * @param tokens Lista de tokens del lexer
     */
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentIndex = 0;
        this.currentToken = tokens.get(0);
    }

    /**
     * Avanza al siguiente token
     */
    private void advance() {
        currentIndex++;
        if (currentIndex < tokens.size()) {
            currentToken = tokens.get(currentIndex);
        }
    }

    /**
     * Verifica si el token actual es del tipo esperado
     * @param type Tipo esperado
     * @return true si coincide
     */
    private boolean check(TokenType type) {
        return currentToken.type() == type;
    }

    /**
     * Consume un token si es del tipo esperado, sino lanza error
     * @param type Tipo esperado
     * @param errorMessage Mensaje de error
     */
    private void expect(TokenType type, String errorMessage) {
        if (!check(type)) {
            throw new RuntimeException(
                String.format("Error sintáctico en posición %d: %s\n  Token actual: %s",
                    currentToken.position(), errorMessage, currentToken)
            );
        }
        advance();
    }

    /**
     * Punto de entrada: parsea la expresión completa
     * @return Raíz del AST
     */
    public Node parse() {
        if (check(TokenType.EOF)) {
            throw new RuntimeException("Error: expresión vacía");
        }
        Node result = parseExpression();

        // Verificar que se haya consumido toda la entrada
        if (!check(TokenType.EOF)) {
            throw new RuntimeException(
                String.format("Error sintáctico en posición %d: tokens inesperados después de la expresión\n  Token: %s",
                    currentToken.position(), currentToken)
            );
        }

        return result;
    }

    /**
     * E → T E'
     * Parsea una expresión (suma y resta - menor precedencia)
     */
    private Node parseExpression() {
        Node left = parseTerm();
        return parseExpressionPrime(left);
    }

    /**
     * E' → + T E' | - T E' | ε
     * Parsea el resto de una expresión (asociatividad izquierda)
     */
    private Node parseExpressionPrime(Node left) {
        while (check(TokenType.PLUS) || check(TokenType.MINUS)) {
            char operator = currentToken.value().charAt(0);
            advance();
            Node right = parseTerm();
            left = new BinaryNode(operator, left, right);
        }
        return left;
    }

    /**
     * T → U T'
     * Parsea un término (multiplicación y división)
     */
    private Node parseTerm() {
        Node left = parseUnary();
        return parseTermPrime(left);
    }

    /**
     * T' → * U T' | / U T' | ε
     * Parsea el resto de un término (asociatividad izquierda)
     */
    private Node parseTermPrime(Node left) {
        while (check(TokenType.MULTI) || check(TokenType.DIV)) {
            char operator = currentToken.value().charAt(0);
            advance();
            Node right = parseUnary();
            left = new BinaryNode(operator, left, right);
        }
        return left;
    }

    /**
     * U → - U | F
     * Parsea operador unario (negación)
     * Precedencia: menor que potencia, mayor que mult/div
     * Ejemplo: -2^2 = -(2^2) = -4 (NO (-2)^2 = 4)
     */
    private Node parseUnary() {
        if (check(TokenType.MINUS)) {
            advance();
            // Recursión para manejar múltiples negaciones: --5
            Node expr = parseUnary();
            return new UnaryNode(expr);
        }
        return parseFactor();
    }

    /**
     * F → P F'
     * Parsea un factor (base para potencia)
     */
    private Node parseFactor() {
        Node left = parsePrimary();
        return parseFactorPrime(left);
    }

    /**
     * F' → ^ U | ε
     * Parsea potencia (asociatividad DERECHA - CRÍTICO)
     * Ejemplo: 2^3^2 = 2^(3^2) = 512 (NO 8^2 = 64)
     * La potencia tiene MAYOR precedencia que unario: -2^2 = -(2^2)
     */
    private Node parseFactorPrime(Node left) {
        if (check(TokenType.POW)) {
            advance();
            // Llamar a parseUnary para procesar el lado derecho
            // Esto permite: 2^-3 y garantiza que -2^2 = -(2^2)
            Node right = parseUnary();
            // Recursión para asociatividad derecha
            right = parseFactorPrime(right);
            return new BinaryNode('^', left, right);
        }
        return left;
    }

    /**
     * P → NUM | ID | sin(E) | cos(E) | tan(E) | (E)
     * Parsea expresiones primarias (números, variables, funciones, paréntesis)
     */
    private Node parsePrimary() {
        // Número
        if (check(TokenType.NUMBER)) {
            double value = Double.parseDouble(currentToken.value());
            advance();
            return new NumberNode(value);
        }

        // Constante PI
        if (check(TokenType.PI)) {
            advance();
            return new NumberNode(Math.PI);
        }

        // Constante E
        if (check(TokenType.E)) {
            advance();
            return new NumberNode(Math.E);
        }

        // Variable
        if (check(TokenType.VAR)) {
            String varName = currentToken.value();
            advance();
            return new VarNode(varName);
        }

        // Funciones trigonométricas
        if (check(TokenType.SIN) || check(TokenType.COS) || check(TokenType.TAN)) {
            String funcName = currentToken.value();
            advance();

            expect(TokenType.L_PAREN,
                String.format("se esperaba '(' después de función '%s'", funcName));

            Node argument = parseExpression();

            expect(TokenType.R_PAREN,
                String.format("se esperaba ')' para cerrar función '%s'", funcName));

            return new FunctionNode(funcName, argument);
        }

        // Expresión entre paréntesis
        if (check(TokenType.L_PAREN)) {
            advance();
            Node expr = parseExpression();
            expect(TokenType.R_PAREN, "se esperaba ')' para cerrar paréntesis");
            return expr;
        }

        // Error: token inesperado
        throw new RuntimeException(
            String.format("Error sintáctico en posición %d: token inesperado '%s'\n  Se esperaba: número, variable, función (sin/cos/tan), o '('",
                currentToken.position(), currentToken.value())
        );
    }
}
