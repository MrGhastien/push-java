import java.io.File;
import java.util.Objects;

public class rm implements Command {
    public int execute(String[] input) {
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
}
