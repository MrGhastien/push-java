import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ls implements Command {

    public int execute(String[] input) {
        File f = new File(Main.context().currPath);



        for (String s : f.list()) {
            File currF = new File(Main.context().currPath + "\\" + s);
            Date date = new Date(currF.lastModified());
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String dateStr = format.format(date);
            System.out.println(dateStr + "  " + currF.length()
                    + "  " + currF.canExecute() + "  " + currF.canRead() + "  " + currF.canWrite() + s);

        }
        return 0;
    }
}
