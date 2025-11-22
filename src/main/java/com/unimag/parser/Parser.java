package com.unimag.parser;

import com.unimag.lexer.Token;
import com.unimag.lexer.TokenType;
import com.unimag.parser.astNodes.*;

import java.util.List;

/*
  E  → T E'
  E' → + T E' | - T E' | ε
  T  → F T'
  T' → * F T' | / F T' | ε
  F  → U F'
  F' → ^ F | ε
  U  → - U | P
  P  → NUM | ID | sin(E) | cos(E) | tan(E) | (E)
 */
public class Parser {
    private final List<Token> tokens;
    private int currentIndex;
    private Token currentToken;


    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentIndex = 0;
        this.currentToken = tokens.get(0);
    }

    private void advance() {
        currentIndex++;
        if (currentIndex < tokens.size()) {
            currentToken = tokens.get(currentIndex);
        }
    }


    private boolean check(TokenType type) {

        return currentToken.type() == type;
    }


    private void expect(TokenType type, String errorMessage) {
        if (!check(type)) {
            throw new RuntimeException(
                String.format("Error sintáctico en posición %d: %s\n  Token actual: %s",
                    currentToken.position(), errorMessage, currentToken)
            );
        }
        advance();
    }


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
      E → T E'
      Parsea una expresión (suma y resta - menor precedencia)
     */
    private Node parseExpression() {
        Node left = parseTerm();
        return parseExpressionPrime(left);
    }

    /**
      E' → + T E' | - T E' | ε
      Parsea el resto de una expresión (asociatividad izquierda)
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
     T → F T'
     Parsea un término (multiplicación y división)
     */
    private Node parseTerm() {
        Node left = parseUnary();
        return parseTermPrime(left);
    }

    /**
      T' → * F T' | / F T' | ε
      Parsea el resto de un término (asociatividad izquierda)
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
      U → - U | P
      Parsea operador unario (negación)
     */
    private Node parseUnary() {
        if (check(TokenType.MINUS)) {
            advance();
            Node expr = parseUnary();
            return new UnaryNode(expr);
        }
        return parseFactor();
    }

    /**
      F → U F'
      Parsea un factor (base para potencia)
     */
    private Node parseFactor() {
        Node left = parsePrimary();
        return parseFactorPrime(left);
    }

    /**
      F' → ^ F | ε
      Parsea potencia (asociatividad DERECHA - CRÍTICO)
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
      P → NUM | ID | sin(E) | cos(E) | tan(E) | (E)
      Parsea expresiones primarias (números, variables, funciones, paréntesis)
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

            expect(TokenType.L_PAR,
                String.format("se esperaba '(' después de función '%s'", funcName));

            Node argument = parseExpression();

            expect(TokenType.R_PAR,
                String.format("se esperaba ')' para cerrar función '%s'", funcName));

            return new FunctionNode(funcName, argument);
        }


        if (check(TokenType.L_PAR)) {
            advance();
            Node expr = parseExpression();
            expect(TokenType.R_PAR, "se esperaba ')' para cerrar paréntesis");
            return expr;
        }


        throw new RuntimeException(
            String.format("Error sintáctico en posición %d: token inesperado '%s'\n  Se esperaba: número, variable, función (sin/cos/tan), o '('",
                currentToken.position(), currentToken.value())
        );
    }
}
