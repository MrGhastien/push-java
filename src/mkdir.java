import java.io.File;

public class mkdir implements command {
    public int execute(String[] input) {
        File f = new File(Main.currPath + "\\" + input[1]);
        if (f.mkdir()) {
            System.out.println("Directory created");
            return 0;
        } else {
            System.out.println("Directory not created");
            return -1;
        }
    }
}
