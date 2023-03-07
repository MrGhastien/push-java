import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ls implements command {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public int digitNb(File currF) {
        long size = currF.length();
        String sizeStr = String.valueOf(size);
        int digitNb = sizeStr.length();
        return digitNb;
    }

    public String dateFormat(File currF) {
        Date date = new Date(currF.lastModified());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String strDate = formatter.format(date);
        return strDate;
    }

    public void spacing(int nb)
    {
        while (nb != 0)
        {
            System.out.print(" ");
            nb--;
        }
    }

    public int execute(String[] input) {
        File f = new File(Main.currPath);
        int max = -1;
        for (String s : f.list()) {

            File currF = new File(Main.currPath + "\\" + s);
            int digitNb = digitNb(currF);
            if (digitNb > max) {
                max = digitNb;
            }

        }

        System.out.print("Last Modified" + "        " + "Size" );
        spacing(max - 3);
        System.out.println("Execute" + " " + "Read" + "    " + "Write" + " " + "Name");
        for (String s : f.list()) {

            File currF = new File(Main.currPath + "\\" + s);
            String dateStr = dateFormat(currF);
            int digitNb = digitNb(currF);

            boolean isDir = currF.isDirectory();
            boolean isHHide = currF.isHidden();

            System.out.print(dateStr + "  " + currF.length());
            spacing(max - digitNb);
            if (isHHide){
                System.out.println(" " + currF.canExecute() + "    " + currF.canRead() + "    " + currF.canWrite() + "  " + ANSI_YELLOW + s + ANSI_RESET);
            }
            else if (isDir) {
                System.out.println(" " + currF.canExecute() + "    " + currF.canRead() + "    " + currF.canWrite() + "  " + ANSI_BLUE + s + ANSI_RESET);
            } else {
                System.out.println(" " + currF.canExecute() + "    " + currF.canRead() + "    " + currF.canWrite() + "  " + s);
            }

        }
        return 0;
    }
}
