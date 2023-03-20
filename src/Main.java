import java.io.IOException;

public class Main {

    private static Context ctx;

    public static void main(String[] args) throws IOException {
        ctx = new Context(System.in);
        ctx.currPath = System.getProperty("user.home");

        registerCommands();

        String str;
        String[] splitStr;
        int retCode;
        while(!ctx.shouldExit) {
            System.err.flush();
            prompt();
            str = ctx.getReader().readLine();
            splitStr = str.split(" ");

            retCode = ctx.runCommand(splitStr);
            if(retCode != Integer.MIN_VALUE)
                continue;

            retCode = ctx.runProgram(splitStr);
        }
    }

    public static Context context() {
        return ctx;
    }

    private static void registerCommands() {
        ctx.registerCommand("exit", Builtins::exit);
    }

    private static void prompt() {
        System.out.print("[" + ctx.currPath + "] $> ");
    }
}