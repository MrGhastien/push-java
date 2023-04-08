package push;

import java.io.IOException;
import java.util.List;

import push.commands.Command;
import push.commands.interpreter.Indexer;
import push.commands.interpreter.Parser;
import push.commands.interpreter.Streams;
import push.commands.interpreter.Token;

public class Main {

    private static Context ctx;

    public static void main(String[] args) throws IOException {
        ctx = new Context(System.in, args);
        ctx.currPath = System.getProperty("user.home");

        registerBuiltins();

        String str;
        String[] splitStr;
        int retCode;
        while(!ctx.shouldExit) {
            System.err.flush();
            prompt();
            str = ctx.getReader().readLine();


            List<Token> tokens;
            tokens = Indexer.index(str);
            for (Token t : tokens) {
                System.out.print("'" + t.toString() + "' ");
            }
            System.out.println();
            for (Token t : tokens) {
                System.out.print("'" + t.getIdentifier().name() + "' ");
            }
            System.out.println();
            Command cmd = Parser.parse(tokens);
            Parser.substituteAll(cmd);
            System.out.println(cmd.toString());
            cmd.execute(new Streams(System.in, System.out, ProcessBuilder.Redirect.INHERIT, ProcessBuilder.Redirect.INHERIT));
            /*
            if (str.contains("="))
            {
                String[] split = str.split("=");

                Variable variable = new Variable(split[0], split[1],false);
                ctx.envVariables.put(split[0], variable);
                continue;
            }

            splitStr = str.split(" ");

            if(splitStr[0].equals("readonly")) {
                ctx.envVariables.get(splitStr[1]).readOnly = true;
                continue;
            }
            
            
            retCode = ctx.runBuiltin(splitStr);
            if(retCode != Integer.MIN_VALUE)
                continue;

            retCode = ctx.runProgram(splitStr);
            */
        }
    }

    public static Context context() {
        return ctx;
    }

    private static void registerBuiltins() {
        ctx.registerBuiltin("exit", Builtins::exit);
        ctx.registerBuiltin("rm", Builtins::rm);
        ctx.registerBuiltin("mkdir", Builtins::mkdir);
        ctx.registerBuiltin("cd", Builtins::cd);
        ctx.registerBuiltin("ls", new ls());
        ctx.registerBuiltin("cat", Builtins::cat);
        ctx.registerBuiltin("pwd", Builtins::pwd);
        ctx.registerBuiltin("echo", Builtins::echo);
        ctx.registerBuiltin("cp", Builtins::cp);
        ctx.registerBuiltin("mv", Builtins::mv);
        ctx.registerBuiltin("clear", Builtins::clear);
        ctx.registerBuiltin("unset", Builtins::unset);
    }

    private static void prompt() {
        System.out.print("[" + ctx.currPath + "] $> ");
    }
}
