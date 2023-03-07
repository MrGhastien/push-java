import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static String currPath;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Map<String,command> commands = new HashMap<>();
        currPath = System.getProperty("user.home");

        //commands.put("ls", new ls());
        command cmd = new ls();

        cmd.execute(new String[]{"ls"});


    }

    public static int test(String[] arg) {
        System.out.println("first arg is : " + arg[0]);
        return 0;
    }
}