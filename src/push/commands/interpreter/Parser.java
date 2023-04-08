package push.commands.interpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import push.Main;
import push.Variable;
import push.commands.*;
import push.commands.interpreter.Token.SpecialToken;
import push.util.Lazy;

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
                        simple.addArg(Lazy.ofPresent(token.toString()));
                    } else {
                        throw new IllegalStateException("ALGO MAL FAIT 0/20");
                    }
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
        expression = expression.substring(1, expression.length() - 1);

        if (expression.startsWith("((")) {
            expression = expression.substring(2, expression.length() - 1);
            return "";
        }

        else if (expression.startsWith("(") || expression.startsWith("`")) {
            expression = expression.substring(1, expression.length() - 2);
            Command command = parse(Indexer.index(expression));
            Streams streams = new Streams();
            command.execute(streams);
            BufferedReader in = new BufferedReader(new InputStreamReader(streams.out));
            String text = "";
            try {
                int res = 0;
                while ((res = in.read()) != -1) {
                    text += (char) res;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return text;
        }

        else if (expression.startsWith("{")) {
            expression = expression.substring(1, expression.length() - 2);
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
