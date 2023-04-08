package push.commands.interpreter;

import push.Main;
import push.Variable;
import push.commands.*;

import java.io.*;
import java.nio.Buffer;
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

    //ls a b || echo test | cat abc ; echo gaming | shutdown ftg &
    public static Command parse(List<Token> tokens) {
        if (tokens.isEmpty()) {
            return null;
        }
        Token previousToken = null;
        LinkedList<Command> commandStack = new LinkedList<>();
        for (int i = 0; i < tokens.size(); i++) {
            if (commandStack.size() == 0 || !(commandStack.getLast() instanceof SimpleCommand))
                commandStack.addLast(new SimpleCommand());
            Token token = tokens.get(i);
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
                case GREAT -> {
                    Command cmd = commandStack.getLast();
                    Token nextToken = tokens.get(i + 1);
                    if(nextToken.getIdentifier() != WORD)
                        throw new IllegalStateException("ALGO MAL FAIT -1/20");
                    File file = new File(nextToken.toString());
                    if (commandStack.get(commandStack.size() - 2) instanceof RedirectedCommand redCmd) {
                        redCmd.setOutputTarget(file);
                    } else {
                        RedirectedCommand redCmd = new RedirectedCommand(file, null);
                        redCmd.setOutputTarget(file);
                        commandStack.add(commandStack.size() - 2, redCmd);
                    }
                    i++;
                }
            }
        }
        if (commandStack.size() > 1) {
            while (commandStack.size() > 1 && !(commandStack.getLast() instanceof SeqListCommand)) {
                Command cmd = commandStack.removeLast();
                ((CommandList) commandStack.getLast()).addCommand(cmd);
            }
        }
        return commandStack.removeFirst();
    }

    public static String substitute(String expression) {
        //TODO : First case not done
        if (!expression.startsWith("$"))
            return expression;
        expression = expression.substring(1);

        if (expression.startsWith("((")) {
            expression = expression.substring(2, expression.length() - 1);
            return "";
        }

        else {
            if (expression.startsWith("(") || expression.startsWith("`")) {
                expression = expression.substring(1, expression.length() - 1);
                Command command = parse(Indexer.index(expression));
                Streams streams = new Streams();
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                streams.in = new PrintStream(byteOut);
                streams.out = new ByteArrayInputStream(new byte[0]);
                command.execute(streams);

                BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(byteOut.toByteArray())));
                StringBuilder text = new StringBuilder();
                try {
                    int res;
                    while ((res = in.read()) != -1) {
                        char c = (char) res;
                        if (c != '\n' && c != '\r')
                            text.append(c);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return text.toString();
            }

            else if (expression.startsWith("{")) {
                expression = expression.substring(1, expression.length() - 1);
                Variable variable = Main.context().envVariables.get(expression);
                if (variable == null)
                    return "";
                return variable.get();
            } else {
                expression = expression.substring(1);
                Variable variable = Main.context().envVariables.get(expression);
                if (variable == null)
                    return "";
                return variable.get();
            }
        }
    }

    public static String[] fieldSplit(String input) {
        Variable ifs = Main.context().envVariables.get("IFS");
        return input.split(ifs != null ? ifs.get() : " ");
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
