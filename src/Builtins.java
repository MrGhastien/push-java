import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

    public static int cd(String[] input) {
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

    public int touch(String[] input) {
        File f = new File(Main.context().currPath + "\\" + input[1]);
        if (f.exists())
            f.setLastModified(System.currentTimeMillis());
        try {
            if (f.createNewFile()) {
                System.out.println("File created");
                return 0;
            } else {
                System.out.println("File not created");
                return -1;
            }
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return -1;
        }
    }

    public static int echo(String[] input) {
        System.out.println(input[1]);
        return 0;
    }

    public static int pwd(String[] input) {
        System.out.println(Main.context().currPath);
        return 0;
    }

    public static int mv(String[] input) {
        File f = new File(Main.context().currPath + "\\" + input[1]);
        File f2 = new File(Main.context().currPath + "\\" + input[2]);
        try {
            if ((Files.copy(f.toPath(), f2.toPath()) != null)) {
                rm(new String[]{"rm", input[1]});
                System.out.println("File moved");
                return 0;
            } else {
                System.out.println("File not moved");
                return -1;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int cp(String[] input) {
        File f = new File(Main.context().currPath + "\\" + input[1]);
        File f2 = new File(Main.context().currPath + "\\" + input[2]);
        try {
            if (Files.copy(f.toPath(), f2.toPath()) != null) {
                System.out.println("File copied");
                return 0;
            } else {
                System.out.println("File not copied");
                return -1;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int sleep(String[] input) {
        try {
            Thread.sleep(Integer.parseInt(input[1]));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int ls(String[] input) {
        File f = new File(Main.context().currPath);
        String[] files = f.list();
        for (String file : files) {
            System.out.println(file);
        }
        return 0;
    }

    public static int cat(String[] input) {
        File f = new File(Main.context().currPath + "\\" + input[1]);
        try {
            System.out.println(Files.readString(f.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

public static int clear(String[] input) {
        System.out.print("ergerghersheqjtsjqsrtnqsetjexdwtnwerhjnrdgj,rsy,ryjjrtjstjjtrj");
        System.out.print("\b".repeat(100));
        System.out.flush();
        return 0;
    }




}
