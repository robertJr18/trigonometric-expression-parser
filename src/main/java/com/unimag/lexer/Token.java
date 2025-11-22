package com.unimag.lexer;

public record Token(TokenType type, String value, int position) {
    @Override
    public String toString() {
        return "Token: %s, Value: %s, Position: %d ".formatted(type, value, position);
    }
}
