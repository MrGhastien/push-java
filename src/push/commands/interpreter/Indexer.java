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

                continue;
            }

            if (c == '\'') { // RULE 4.b
                i = handleSingleQuotes(rawCmd, tokenBuilder, i);
                continue;
            }

            if (c == '"') { // RULE 4.c
                i = handleDoubleQuotes(rawCmd, tokenBuilder, i);
                continue;
            }
          

            if (c == '$') {
                marker = Marker.WORD;
                i = handleExpansion(rawCmd, tokenBuilder, i);
                continue;
            }

            if (c == '`') {
                marker = Marker.WORD;
                i = collectUpToDelimiter(rawCmd, tokenBuilder, i);
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

        if(marker == null) {
            System.err.println("Warning : symbol with no marker encountered : '" + argBuilder.toString() + "'");
        }

        recognize(tokens, argBuilder, marker);
        argBuilder.setLength(0);
    }

    private static int handleExpansion(String rawCmd, StringBuilder argBuilder, int i) {
        argBuilder.append(rawCmd.charAt(i));
        char c = rawCmd.charAt(i + 1);
        if(c == '{') {
            i = collectUpToDelimiter(rawCmd, argBuilder, i + 1);
        } else if(c == '(') {
            if(rawCmd.charAt(i + 2) == '(') {
                //handleArithExpans(rawCmd, argBuilder, i + 3);
                throw new UnsupportedOperationException("fuck");
            } else
                i = collectUpToDelimiter(rawCmd, argBuilder, i + 1);
        }
        return i;
    }

    private static int collectUpToDelimiter(String rawCmd, StringBuilder tokenBuilder, int i) {
        char delimiter = rawCmd.charAt(i);
        tokenBuilder.append(delimiter);
        i++;
        char c;
        int delimiterLevel = 1;
        for(; i < rawCmd.length(); i++) {
            c = rawCmd.charAt(i);

            if(c == '$') {
                i = handleExpansion(rawCmd, tokenBuilder, i);
                tokenBuilder.append(c);
                continue;
            }
            if (c == '`') {
                i = collectUpToDelimiter(rawCmd, tokenBuilder, i);
                tokenBuilder.append(c);
                continue;
            }

            if(c == '\\') {
                tokenBuilder.append(c);
                tokenBuilder.append(rawCmd.charAt(i + 1));
                i++;
                continue;
            }

            if(c == delimiter) {
                delimiterLevel++;
                tokenBuilder.append(c);
                continue;
            }

            if(c == getOpposite(delimiter)) {
                delimiterLevel--;
                tokenBuilder.append(c);
                if(delimiterLevel == 0)
                    break;
            }

            if(c == '\'') {
                i = handleSingleQuotes(rawCmd, tokenBuilder, i);
                tokenBuilder.append(c);
                continue;
            }

            if(c == '"') {
                i = handleDoubleQuotes(rawCmd, tokenBuilder, i);
                tokenBuilder.append(c);
                continue;
            }
            tokenBuilder.append(c);
        }
        if(delimiterLevel != 0)
            throw new IllegalArgumentException("Unmatched delimiter '" + delimiter + "'");
        return i;
    }

    public static int handleSingleQuotes(String rawCmd, StringBuilder tokenBuilder, int i) {
        i++;
        char c;
        boolean quoteMatched = false;
        for(; i < rawCmd.length(); i++) {
            c = rawCmd.charAt(i);
            tokenBuilder.append(c);
            if (c == '\'') {
                quoteMatched = true;
                break;
            }
            continue;
        }
        if(!quoteMatched)
            throw new IllegalArgumentException("Mismatched single quote !");
        return i;
    }

    public static int handleDoubleQuotes(String rawCmd, StringBuilder tokenBuilder, int i) {
        i++;
        char c;
        boolean quoteMatched = false;
        boolean escapeNext = false;
        for(; i < rawCmd.length(); i++) {
            c = rawCmd.charAt(i);
            if(escapeNext) {
                if(c != '$' && c != '`' && c != '"' && c != '\\')
                    tokenBuilder.append('\\');
                tokenBuilder.append(c);
                escapeNext = false;
            }
            if (c == '\\') {
                escapeNext = true;
                continue;
            }

            if (c == '$') {
                i = handleExpansion(rawCmd, tokenBuilder, i);
                continue;
            }

            if (c == '`') {
                i = collectUpToDelimiter(rawCmd, tokenBuilder, i);
                continue;
            }

            tokenBuilder.append(c);
            if(c == '"') {
                quoteMatched = true;
                break;
            }
        }

        if(!quoteMatched)
            throw new IllegalArgumentException("Mismatched single quote !");
        return i;

    }

    public static char getOpposite(char delimiter) {
        return switch(delimiter) {
        case '{' -> '}';
        case '(' -> ')';
        default -> delimiter;
        };
    }

    public static void recognize(LinkedList<Token> tokens, StringBuilder tokenBuilder, Marker marker) {
        String symbol = tokenBuilder.toString();
        Token last = tokens.isEmpty() ? null : tokens.getLast();

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

                ((Token.SpecialToken) last).setIdentifier(TokenIdentifier.IO_NUMBER);
            }
            tokens.add(id.create(symbol));
            return;
        }

        System.err.println("Warning : symbol with no marker encountered.");
        TokenIdentifier id = TokenIdentifier.getOperator(symbol);
        recognize(tokens, tokenBuilder, id == null ? Marker.WORD : Marker.OPERATOR);
            
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
