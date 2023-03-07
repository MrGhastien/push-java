public class Builtins {

    public static int exit(String[] args) {
        Context ctx = Main.context();
        ctx.shouldExit = true;
        System.out.println("Goodbye!");
        return 0;
    }

}
