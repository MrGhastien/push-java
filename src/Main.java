import java.io.IOException;

public class Main {

    private static Context ctx;

    public static void main(String[] args) throws IOException {
        ctx = new Context(System.in);
        ctx.currPath = System.getProperty("user.home");

        ctx.registerCommand("exit", Builtins::exit);
        ctx.registerCommand("rm", Builtins::rm);
        ctx.registerCommand("mkdir", Builtins::mkdir);
        ctx.registerCommand("cd", Builtins::cd);
        ctx.registerCommand("ls", new ls());
        ctx.registerCommand("cat", Builtins::cat);
        ctx.registerCommand("pwd", Builtins::pwd);
        ctx.registerCommand("echo", Builtins::echo);
        ctx.registerCommand("cp", Builtins::cp);
        ctx.registerCommand("mv", Builtins::mv);
        ctx.registerCommand("clear", Builtins::clear);
        ctx.registerCommand("unset", Builtins::unset);



        String str;
        String[] splitStr;
        int retCode;
        while(!ctx.shouldExit) {
            str = ctx.getReader().readLine();

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

            retCode = ctx.runCommand(splitStr);
            if(retCode != Integer.MIN_VALUE)
                continue;

            retCode = ctx.runProgram(splitStr);

        }
    }

    public static Context context() {
        return ctx;
    }
}