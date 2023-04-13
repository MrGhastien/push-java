package push.commands.interpreter;

import push.Main;
import push.Variable;
import push.commands.*;
import push.commands.interpreter.Collecter.ExpansionHandler;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static push.commands.interpreter.TokenIdentifier.WORD;

public final class Parser {

    public static void combineToSeq(LinkedList<Command> commandStack, boolean async) {
        while (commandStack.size() > 1 && !(commandStack.get(commandStack.size() - 2) instanceof SeqListCommand)) {
            Command cmd = commandStack.removeLast();
            ((CommandList) commandStack.getLast()).addCommand(cmd);
        }
        Command cmd = commandStack.removeLast();
        if (commandStack.size() == 0) {
            SeqListCommand seqList = new SeqListCommand();
            commandStack.addLast(seqList);
        }
        ((CommandList) commandStack.getLast()).addCommand(cmd);
        cmd.setAsync(async);
    }

    public static void combineToLogic(LinkedList<Command> commandStack, boolean or) {
        Command previous = null;
        while (commandStack.size() > 1
                && !((previous = commandStack.get(commandStack.size() - 2)) instanceof LogicListCommand
                && !(previous instanceof SeqListCommand))) {
            Command cmd = commandStack.removeLast();
            ((CommandList) commandStack.getLast()).addCommand(cmd);
        }
        Command cmd = commandStack.removeLast();
        if (commandStack.size() == 0 || previous instanceof SeqListCommand) {

            previous = new LogicListCommand();
            commandStack.addLast(previous);
        }
        ((LogicListCommand) previous).addCommand(cmd, or);
    }

    private static void handleRedirection(LinkedList<Command> commandStack, Token currentToken, Token nextToken) {
        Command cmd = commandStack.getLast();
        if (nextToken.getIdentifier() != WORD)
            throw new IllegalStateException("ALGO MAL FAIT -1/20");
        RedirectedCommand redCmd;
        if (commandStack.size() > 1 && commandStack.get(commandStack.size() - 2) instanceof RedirectedCommand r) {
            redCmd = r;
        } else {
            redCmd = new RedirectedCommand();
            commandStack.add(commandStack.size() - 1, redCmd);
            redCmd.addCommand(cmd);
        }
        Path pwd = Paths.get(Main.context().currPath);
        File file = pwd.resolve(nextToken.toString()).toFile();
        switch (currentToken.getIdentifier()) {
            case GREAT -> {
                redCmd.setOutputTarget(file);
            }
            case LESS -> {
                redCmd.setInputTarget(file);
            }
            case LESSGREAT -> {
                redCmd.setInputTarget(file);
                redCmd.setOutputTarget(file);
            }
            case DGREAT -> {
                redCmd.setOutputTarget(file);
                redCmd.setAppend(true);
            }
            case CLOBBER -> {
                redCmd.setOutputTarget(file);
                redCmd.setForceOverride(true);
            }
            default -> {
                throw new IllegalArgumentException("Improper use of the 'handleRedirection' function !");
            }
        }
    }
    
    //ls a b || echo test | cat abc ; echo gaming | shutdown ftg &
    public static Command parse(List<Token> tokens) {
        if (tokens.isEmpty()) {
            return null;
        }
        LinkedList<Command> commandStack = new LinkedList<>();
        for (int i = 0; i < tokens.size(); i++) {
            if (commandStack.size() == 0 || !(commandStack.getLast() instanceof SimpleCommand))
                commandStack.addLast(new SimpleCommand());
            Token token = tokens.get(i);
            if (token.getIdentifier().isRedirect()) {
                handleRedirection(commandStack, token, tokens.get(i + 1));
                i++;
                continue;
            }
            switch (token.getIdentifier()) {
                case SEMI -> {
                    combineToSeq(commandStack, false);
                }
                case AND -> {
                    combineToSeq(commandStack, true);
                }
                case AND_IF -> {
                    combineToLogic(commandStack, false);
                }
                case OR_IF -> {
                    combineToLogic(commandStack, true);
                }
                case PIPE -> {
                    Command cmd = commandStack.removeLast();

                    if (commandStack.size() > 0 && commandStack.getLast() instanceof PipelineCommand) {
                        ((PipelineCommand) commandStack.getLast()).addCommand(cmd);
                    } else {
                        PipelineCommand pipeline = new PipelineCommand();
                        pipeline.addCommand(cmd);
                        commandStack.addLast(pipeline);
                    }
                }
                case WORD -> {
                    Command cmd = commandStack.getLast();
                    if (cmd instanceof SimpleCommand simple) {
                        simple.addArg(token.toString());
                    } else {
                        throw new IllegalStateException("ALGO MAL FAIT 0/20");
                    }
                }
                default -> {
                    throw new IllegalArgumentException();
                }
            }
        }
        if (commandStack.size() > 1) {
            while (commandStack.size() > 1 && !(commandStack.getLast() instanceof SeqListCommand)) {
                Command cmd = commandStack.removeLast();
                if(commandStack.getLast() instanceof RedirectedCommand)
                    continue;
                ((CommandList) commandStack.getLast()).addCommand(cmd);
            }
        }
        return commandStack.removeFirst();
    }

    public static String substitute(String expression) {

        StringBuilder resultBuilder = new StringBuilder();
        
        char c;
        ExpansionHandler handler = (str, type) -> {
            handleExpansion(str, type, resultBuilder);
        };
        for(int i = 0; i < expression.length(); i++) {
            c = expression.charAt(i);

            switch (c) {
                case '\'' -> {
                    i = Collecter.collectSingleQuotes(expression, resultBuilder, i);
                }
                case '"' -> {
                    i = Collecter.collectDoubleQuotes(expression, resultBuilder, i, handler);
                }
                case '$' -> {
                    StringBuilder builder = new StringBuilder();
                    i = Collecter.collectExpansion(expression, builder, i, handler);
                }
                default -> {
                    resultBuilder.append(c);
                }
            }
        }
        return resultBuilder.toString();
    }

    private static void handleExpansion(String str, Collecter.ExpansionType type, StringBuilder builder) {
        switch (type) {
            case CMD -> {
                builder.append(substituteCommand(str, false));
            }
            case PARAM -> {
                Variable v = Main.context().envVariables.get(str);
                if(v != null)
                    builder.append(v.get());
            }
            case ARITH -> {
                throw new UnsupportedOperationException("Not implemented");
            }
        }
    }

    private static String substituteCommand(String cmd, boolean backquote) {
        Command command = parse(Indexer.index(cmd));
        Streams streams = new Streams();
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        streams.out = new PrintStream(byteOut);
        streams.in = new ByteArrayInputStream(new byte[0]);
        command.execute(streams);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(byteOut.toByteArray())));
        StringBuilder builder = new StringBuilder();
        try {
            int res;
            while ((res = in.read()) != -1) {
                char c = (char) res;
                if (c != '\n' && c != '\r')
                    builder.append(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static String[] fieldSplit(String input) {
        Variable ifs = Main.context().envVariables.get("IFS");
        String delimiter = ifs == null ? " " : ifs.get();
        LinkedList<String> strings = new LinkedList<>();
        StringBuilder current = new StringBuilder();
        StringBuilder buffer = new StringBuilder();
        char c;
        for(int i = 0; i < input.length(); i++) {
            c = input.charAt(i);
            buffer.append(c);
            if(c == '\\') {
                i = Collecter.collectEscape(input, buffer, i);
                i++;
                continue;
            }
            if(c == '\'' || c == '"') {
                i = Collecter.collectUpToDelimiter(input, buffer, i);
                continue;

            }

            while(buffer.length() > delimiter.length()) {
                current.append(buffer.charAt(0));
                buffer.deleteCharAt(0);
            }
            if(buffer.toString().equals(delimiter)) {
                strings.add(current.toString());
                current.setLength(0);
                buffer.setLength(0);
            }
        }
        current.append(buffer);
        strings.add(current.toString());
        return strings.toArray(new String[0]);
    }

    public static String removeQuotes(String input) {
        input = input.replaceAll("\"", "");
        input = input.replaceAll("'", "");
        return input;
    }

    public static void substituteAll(Command command) {
        if (command instanceof SimpleCommand simple) {
            List<String> args = simple.getArgs();
            for (int i = 0; i < args.size(); i++) {
                String arg = args.get(i);
                String res = substitute(arg);
                String[] fieldSplitRes = fieldSplit(res);
                for (int j = 0; j < fieldSplitRes.length; j++) {
                    fieldSplitRes[j] = removeQuotes(fieldSplitRes[j]);
                }
                args.remove(i);
                for (int j = 0; j < fieldSplitRes.length; j++) {
                    args.add(i + j, fieldSplitRes[j]);
                }
                i += fieldSplitRes.length - 1;
            }
        }
        else {
            for (Command cmd : ((CommandList) command).subCommands()) {
                substituteAll(cmd);
            }
        }
    }
}
