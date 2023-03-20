import java.io.File;
import java.util.Objects;

public class cd implements Command {
    Context ctx = Main.context();
    public int execute(String[] input) {
        if(Objects.equals(input[1], ".."))
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
