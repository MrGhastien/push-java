import java.lang.StringBuilder;

public class CommandEvaluator {

    private static boolean canPerformFieldSplitting = true;

    public static String parse(String rawCommand) {
        StringBuilder builder = new StringBuilder();
        State state = State.NORMAL;
        char prev;
        for (int i = 0; i < rawCommand.length(); i++) {
            char c = rawCommand.charAt(i);
            if (c == '#')
                break;

            if (c == '`') {
                i = expandCommand(rawCommand, builder, i + 1);
                continue;
            }

            if (c == '$') {
                state = State.PARAM_EXPANS;
                i = handleExpansion(rawCommand, builder, i + 1);
                continue;
            }
        }

        return builder.toString();
    }

    private static int handleExpansion(String rawCommand, StringBuilder output, int i) {
        char delimiter = rawCommand.charAt(i);

        if (delimiter == '{') {
            return expandExpression(rawCommand, output, i + 1);
        }

        if (delimiter == '(') {
            if (rawCommand.charAt(i + 1) == '(')
                return expandArithmetic(rawCommand, output, i + 2);
            return expandCommand(rawCommand, output, i + 1);
        }

        expandParam(rawCommand, output, i + 1);

        return i;
    }

    private static int expandParam(String rawCommand, StringBuilder output, int i) {
        Context ctx = Main.context();
        switch (rawCommand.charAt(i)) {
            case '0' -> output.append(ctx.getName());
            case '!' -> output.append(ctx.getLastPid());
            case '$' -> output.append(ProcessHandle.current().pid());
            case '?' -> output.append(ctx.getPreviousRetCode());
            case '*' -> {
                output.append(ctx.getParameters());
            }
            case '@' -> output.append(ctx.getParameters());
            default -> {
                StringBuilder b = new StringBuilder();
                char c;
                while(Variable.isValidChar(c = rawCommand.charAt(i))) {
                    b.append(c);
                    i++;
                }
                String varName = b.toString();
                Variable var; // TODO: Get variable !
            }
        }
        return i + 1;
    }

    private static int expandExpression(String rawCommand, StringBuilder output, int i) {
        return i;
    }

    private static int expandArithmetic(String rawCommand, StringBuilder output, int i) {
        return i;
    }

    private static int expandCommand(String rawCommand, StringBuilder output, int i) {
        return i;
    }

    private static char getOpposite(char delimiter) {
        return switch (delimiter) {
            case '(' -> ')';
            case '{' -> '}';
            case '`' -> '`';
            default -> throw new IllegalArgumentException("test");
        };
    }

    public enum State {
        NORMAL,
        SINGLE_QUOTE,
        DOUBLE_QUOTE,
        PARAM_EXPANS,
        CMD_EXPANS,
        ARITH_EXPANS,
        ;
    }

}
