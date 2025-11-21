package com.unimag.lexer;

import java.util.*;

public class Lexer {
    private final String input;
    private int position;
    private char currentChar;

    // Palabras reservadas: funciones y constantes
    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
            "sin", "cos", "tan", "pi", "e"
    ));


    public Lexer(String input) {
        this.input = input;
        this.position = 0;
        this.currentChar = input.length() > 0 ? input.charAt(0) : '\0';
    }

    private void advance() {
        position++;
        if (position < input.length()) {
            currentChar = input.charAt(position);
        } else {
            currentChar = '\0'; // Fin de entrada
        }
    }

    private char peek() {
        int peekPos = position + 1;
        if (peekPos < input.length()) {
            return input.charAt(peekPos);
        }
        return '\0';
    }

    private void skipWhitespace() {
        while (currentChar != '\0' && Character.isWhitespace(currentChar)) {
            advance();
        }
    }

    private Token readNumber() {
        int startPos = position;
        StringBuilder number = new StringBuilder();
        boolean hasDecimalPoint = false;

        // Caso especial: número que empieza con punto (.5)
        if (currentChar == '.') {
            hasDecimalPoint = true;
            number.append(currentChar);
            advance();

            // Debe haber al menos un dígito después del punto
            if (!Character.isDigit(currentChar)) {
                throw new RuntimeException(
                        String.format("Error léxico en posición %d: número mal formado '%s'",
                                startPos, number)
                );
            }
        }

        // Leer dígitos antes del punto decimal
        while (Character.isDigit(currentChar)) {
            number.append(currentChar);
            advance();
        }

        // Leer parte decimal si existe
        if (currentChar == '.' && !hasDecimalPoint) {
            hasDecimalPoint = true;
            number.append(currentChar);
            advance();

            // Leer dígitos después del punto
            while (Character.isDigit(currentChar)) {
                number.append(currentChar);
                advance();
            }
        }

        // Verificar si hay dos puntos consecutivos (error)
        if (currentChar == '.') {
            throw new RuntimeException(
                    String.format("Error léxico en posición %d: número mal formado con múltiples puntos decimales",
                            position)
            );
        }

        return new Token(TokenType.NUMBER, number.toString(), startPos);
    }

    private Token readIdentifier() {
        int startPos = position;
        StringBuilder identifier = new StringBuilder();

        // Primer carácter: letra o underscore
        if (Character.isLetter(currentChar) || currentChar == '_') {
            identifier.append(currentChar);
            advance();
        }

        // Siguientes caracteres: letra, dígito o underscore
        while (Character.isLetterOrDigit(currentChar) || currentChar == '_') {
            identifier.append(currentChar);
            advance();
        }

        String id = identifier.toString();

        // Verificar si es palabra reservada
        if (KEYWORDS.contains(id)) {
            return switch (id) {
                case "sin" -> new Token(TokenType.SIN, id, startPos);
                case "cos" -> new Token(TokenType.COS, id, startPos);
                case "tan" -> new Token(TokenType.TAN, id, startPos);
                case "pi" -> new Token(TokenType.PI, id, startPos);
                case "e" -> new Token(TokenType.E, id, startPos);
                default -> new Token(TokenType.VAR, id, startPos);
            };
        }

        // Es una variable
        return new Token(TokenType.VAR, id, startPos);
    }

    public Token getNextToken() {
        while (currentChar != '\0') {
            // Ignorar espacios en blanco
            if (Character.isWhitespace(currentChar)) {
                skipWhitespace();
                continue;
            }

            // Números (incluyendo .5)
            if (Character.isDigit(currentChar) ||
                (currentChar == '.' && Character.isDigit(peek()))) {
                return readNumber();
            }

            // Identificadores y palabras reservadas
            if (Character.isLetter(currentChar) || currentChar == '_') {
                return readIdentifier();
            }

            // Operadores y símbolos
            int currentPos = position;
            switch (currentChar) {
                case '+':
                    advance();
                    return new Token(TokenType.PLUS, "+", currentPos);
                case '-':
                    advance();
                    return new Token(TokenType.MINUS, "-", currentPos);
                case '*':
                    advance();
                    return new Token(TokenType.MULTI, "*", currentPos);
                case '/':
                    advance();
                    return new Token(TokenType.DIV, "/", currentPos);
                case '^':
                    advance();
                    return new Token(TokenType.POW, "^", currentPos);
                case '(':
                    advance();
                    return new Token(TokenType.L_PAR, "(", currentPos);
                case ')':
                    advance();
                    return new Token(TokenType.R_PAR, ")", currentPos);
                default:
                    throw new RuntimeException(
                            String.format("Error léxico en posición %d: carácter desconocido '%c'",
                                    position, currentChar)
                    );
            }
        }

        return new Token(TokenType.EOF, "", position);
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        Token token;

        do {
            token = getNextToken();
            tokens.add(token);
        } while (token.type() != TokenType.EOF);

        return tokens;
    }

    public void printTokens() {
        List<Token> tokens = tokenize();
        System.out.println("=== TOKENS ===");
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
