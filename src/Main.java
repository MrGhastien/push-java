import java.io.IOException;

public class Main {

    private static Context ctx;

    public static void main(String[] args) throws IOException {
        ctx = new Context(System.in);
        ctx.currPath = System.getProperty("user.home");

        ctx.registerCommand("exit", Builtins::exit);

        String str;
        String[] splitStr;
        int retCode;
        while(!ctx.shouldExit) {
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
}