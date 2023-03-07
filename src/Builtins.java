import java.io.File;
import java.util.Objects;

public class Builtins {

    public static int exit(String[] args) {
        Context ctx = Main.context();
        ctx.shouldExit = true;
        System.out.println("Goodbye!");
        return 0;
    }

    public static int rm(String[] input) {
        File f = new File(Main.context().currPath + "\\" + input[1]);
        if (!Objects.equals(input[1], "-r") && f.isDirectory()) {
            System.out.println("This is a directory use -r to delete it");
            return -1;
        }
        else if (f.delete()) {
            System.out.println("File deleted");
            return 0;
        } else {
            System.out.println("File not deleted");
            return -1;
        }
    }

    public static int mkdir(String[] input) {
        File f = new File(Main.context().currPath + "\\" + input[1]);
        if (f.mkdir()) {
            System.out.println("Directory created");
            return 0;
        } else {
            System.out.println("Directory not created");
            return -1;
        }
    }

    public int cd(String[] input) {
        Context ctx = Main.context();;
        if(input[1] == "..")
        {
            File f = new File(ctx.currPath);
            ctx.currPath = f.getParent();
        }
        else
        {
            File f = new File(ctx.currPath + "\\" + input[1]);
            if(f.isDirectory())
            {
                ctx.currPath = ctx.currPath + "\\" + input[1];
                return 0;
            }
            else
            {
                System.out.println("This is not a directory");
                return -1;
            }
        }
        return 0;
    }

}
