import java.io.File;

public class cd implements command{
    public int execute(String[] input) {
        if(input[1] == "..")
        {
            File f = new File(Main.currPath);
            Main.currPath = f.getParent();
        }
        else
        {
            File f = new File(Main.currPath + "\\" + input[1]);
            if(f.isDirectory())
            {
                Main.currPath = Main.currPath + "\\" + input[1];
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
