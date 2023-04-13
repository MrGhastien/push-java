package push.commands.interpreter;

import java.util.LinkedList;
import java.util.List;



public class Indexer {

    public static List<Token> index(String rawCmd) {
        LinkedList<Token> tokens = new LinkedList<>();
        StringBuilder tokenBuilder = new StringBuilder();
        char c;
        Marker marker = null;
        
        for (int i = 0; i < rawCmd.length(); i++) {
            c = rawCmd.charAt(i);

            if (marker == Marker.OPERATOR) {
                boolean operatorPart = TokenIdentifier.getOperator(tokenBuilder.toString() + c) != null;
                if(!operatorPart) {
                    delimitToken(tokenBuilder, tokens, marker);
                    marker = Marker.WORD;
                } else {
                    tokenBuilder.append(c);
                    continue;
                }
            }

            if(c == '\\') { // RULE 4.a
                char next = rawCmd.charAt(++i);
                if (next != '\n') {
                    tokenBuilder.append(c);
                    tokenBuilder.append(next);
                }
                continue;
            }

            if (c == '\'') { // RULE 4.b
                i = Collecter.collectSingleQuotes(rawCmd, tokenBuilder, i);
                continue;
            }

            if (c == '"') { // RULE 4.c
                i = Collecter.collectDoubleQuotes(rawCmd, tokenBuilder, i);
                continue;
            }
          

            if (c == '$') {
                marker = Marker.WORD;
                i = Collecter.collectExpansion(rawCmd, tokenBuilder, i);
                continue;
            }

            if (c == '`') {
                marker = Marker.WORD;
                i = Collecter.collectUpToDelimiter(rawCmd, tokenBuilder, i);
                continue;
            }

            if(TokenIdentifier.isOperatorPart(c, 0)) { // RULE 6
                delimitToken(tokenBuilder, tokens, marker);
                tokenBuilder.append(c);
                marker = Marker.OPERATOR;
                continue;
            }

            if (Character.isWhitespace(c)) { // RULE 7
                delimitToken(tokenBuilder, tokens, marker);
                marker = null;
                continue;
            }

            if (marker == Marker.WORD) { // RULE 8
                tokenBuilder.append(c);
                continue;
            }

            if(c == '#') // RULE 9
                break;

            tokenBuilder.append(c); // RULE 10
            marker = Marker.WORD;
        }
        delimitToken(tokenBuilder, tokens, marker); // RULE 1
        return tokens;
    }

    private static void delimitToken(StringBuilder argBuilder, LinkedList<Token> tokens, Marker marker) {
        if(argBuilder.length() == 0)
            return;

        recognize(tokens, argBuilder, marker);
        argBuilder.setLength(0);
    }

    public static void recognize(LinkedList<Token> tokens, StringBuilder tokenBuilder, Marker marker) {
        String symbol = tokenBuilder.toString();
        Token last = tokens.isEmpty() ? null : tokens.getLast();

        if (marker == null) {
            TokenIdentifier id = TokenIdentifier.getOperator(symbol);
            marker = id == null ? Marker.WORD : Marker.OPERATOR;
        }

        if (marker == Marker.WORD) {
            TokenIdentifier reservedId = TokenIdentifier.getReserved(symbol);
            if (reservedId != null)
                tokens.add(reservedId.create(symbol));
            else 
                tokens.add(TokenIdentifier.WORD.create(symbol));
            return;
        }

        if (marker == Marker.OPERATOR) {
            TokenIdentifier id = TokenIdentifier.getOperator(symbol);
            if (id == null)
                throw new IllegalArgumentException("Unknown operator '" + symbol + "'");

            if (last != null && id == TokenIdentifier.EQUAL) {
                String lastSymbol = last.toString();
                if (isValidName(lastSymbol))
                    ((Token.SpecialToken) last).setIdentifier(TokenIdentifier.ASSIGN_WORD);
                else
                    throw new IllegalArgumentException("Invalid variable name '" + lastSymbol + "'");
            }

            if (id.isRedirect() && last != null) {

                if (last != null && last.getIdentifier() != TokenIdentifier.WORD)
                    throw new IllegalArgumentException("Unexpected token near redirect operator '" + symbol + "'");

                boolean isNumber = true;
                for (int i = 0; i < last.toString().length(); i++) {
                    char c = last.toString().charAt(i);
                    if (!Character.isDigit(c)) {
                        isNumber = false;
                    }
                }
                if (isNumber) {
                    ((Token.SpecialToken) last).setIdentifier(TokenIdentifier.IO_NUMBER);
                }
            }
            tokens.add(id.create(symbol));
            return;
        }
    }

    private static boolean isValidName(String symbol) {
        char first = symbol.charAt(0);
        boolean ok = Character.isLetter(first) || first == '_' || first == '-';
        for(int i = 1; i < symbol.length(); i++) {
            char c = symbol.charAt(i);
            ok = Character.isLetter(first) || Character.isDigit(c) || first == '_' || first == '-';
            if(!ok)
                return false;
        }
        return true;
    }

}
