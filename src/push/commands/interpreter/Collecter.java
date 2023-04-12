package push.commands.interpreter;

import push.Variable;

public final class Collecter {
    

    public static int collectSingleQuotes(String rawCmd, StringBuilder builder, int i) {
        builder.append(rawCmd.charAt(i));
        i++;
        char c;
        boolean quoteMatched = false;
        for(; i < rawCmd.length(); i++) {
            c = rawCmd.charAt(i);
            builder.append(c);
            
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

    public static int collectDoubleQuotes(String rawCmd, StringBuilder builder, int i) {
        return collectDoubleQuotes(rawCmd, builder, i, null);
    }

    public static int collectDoubleQuotes(String rawCmd, StringBuilder builder, int i, ExpansionHandler expansionHandler) {
        builder.append(rawCmd.charAt(i));
        i++;
        char c;
        boolean quoteMatched = false;
        StringBuilder expansionBuilder = expansionHandler == null ? builder : new StringBuilder();
        for(; i < rawCmd.length(); i++) {
            c = rawCmd.charAt(i);

            if (c == '\\') {
                i = collectEscape(rawCmd, builder, i);
                continue;
            }

            if (c == '$') {
                i = collectExpansion(rawCmd, expansionBuilder, i, expansionHandler);
                if(expansionHandler != null)
                    expansionBuilder.setLength(0);
                continue;
            }

            if (c == '`') {
                i = collectUpToDelimiter(rawCmd, builder, i);
                if (expansionHandler != null) {
                    expansionHandler.handle(expansionBuilder.substring(1, expansionBuilder.length() - 1), ExpansionType.CMD);
                    expansionBuilder.setLength(0);
                }
                continue;
            }

            builder.append(c);
            if(c == '"') {
                quoteMatched = true;
                break;
            }
        }

        if(!quoteMatched)
            throw new IllegalArgumentException("Mismatched single quote !");
        return i;
    }

    public static int collectExpansion(String rawCmd, StringBuilder builder, int i)  {
        return collectExpansion(rawCmd, builder, i, null);
    }

    public static int collectExpansion(String rawCmd, StringBuilder builder, int i, ExpansionHandler handler) {
        builder.append(rawCmd.charAt(i));
        char c = rawCmd.charAt(i + 1);
        ExpansionType type = ExpansionType.PARAM;
        int expansStart = 1, expansEnd = 0;
        if(c == '{') {
            i = collectUpToDelimiter(rawCmd, builder, i + 1);
            expansStart = 2;
            expansEnd = 1;
        } else if (c == '(') {
            expansEnd = 1;
            if (rawCmd.charAt(i + 2) == '(') {
                expansStart = 3;
                type = ExpansionType.ARITH;
                //handleArithExpans(rawCmd, argBuilder, i + 3);
                throw new UnsupportedOperationException("fuck");
            } else {
                type = ExpansionType.CMD;
                expansStart = 2;
                i = collectUpToDelimiter(rawCmd, builder, i + 1);
            }
        } else if(Character.isDigit(c)){
            builder.append(c);
            i++;
        } else {
            int start = i;
            while(i < rawCmd.length() && Variable.isValidChar(c = rawCmd.charAt(i))) {
                builder.append(c);
                i++;
            }
            if(i == start)
                throw new IllegalStateException("Invalid token for substitution '" + c + "'");
        }
        //Assume the given StringBuilder is empty if the handler is non-null
        //This would mean we are calling this function when performing substitutions.
        if(handler != null)
            handler.handle(builder.substring(expansStart, builder.length() - expansEnd), type);
        return i;
    }

    public static int collectUpToDelimiter(String input, StringBuilder builder, int i) {
        char delimiter = input.charAt(i);
        char opposite = getOpposite(delimiter);
        builder.append(delimiter);
        i++;
        char c;
        int delimiterLevel = 1;
        for(; i < input.length(); i++) {
            c = input.charAt(i);

            if(c == '$') {
                i = collectExpansion(input, builder, i);
                continue;
            }
            if (c == '`') {
                i = collectUpToDelimiter(input, builder, i);
                continue;
            }

            if (c == '\\') {
                i = collectEscape(input, builder, i);
                continue;
            }

            if(c == opposite) {
                delimiterLevel--;
                builder.append(c);
                if(delimiterLevel == 0)
                    break;
            }

            if(c == delimiter) {
                delimiterLevel++;
                builder.append(c);
                continue;
            }


            if(c == '\'') {
                i = collectSingleQuotes(input, builder, i);
                continue;
            }

            if(c == '"') {
                i = collectDoubleQuotes(input, builder, i);
                continue;
            }
            builder.append(c);
        }
        if(delimiterLevel != 0)
            throw new IllegalArgumentException("Unmatched delimiter '" + delimiter + "'");
        return i;
    }

    public static int collectEscape(String rawCmd, StringBuilder builder, int pos) {
        char c = rawCmd.charAt(pos);
        if (pos == rawCmd.length() - 1) {
            builder.append(c);
            return pos;
        }

        char next = rawCmd.charAt(pos + 1);
        if (next != '\n' && next != '\r') {
            builder.append(c);
            builder.append(next);
        } else if (next == '\r') {
            if (pos < rawCmd.length() - 2 && rawCmd.charAt(pos + 2) == '\n')
                pos++;
        }
        pos++;
        return pos;
    }

    public static char getOpposite(char delimiter) {
        return switch (delimiter) {
            case '{' -> '}';
            case '(' -> ')';
            default -> delimiter;
        };
    }

    public static enum ExpansionType {
        CMD,
        PARAM,
        ARITH;
    }

    @FunctionalInterface
    public static interface ExpansionHandler {

        void handle(String expansion, ExpansionType type);
        
    }

}
