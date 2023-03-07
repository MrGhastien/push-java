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

    /**
     * Renturn the number of digit of a number
     * @param nb : the number that you want to know the number of digit
     * @return the number of digit
     */
    public int digitNb(long nb) {
        String sizeStr = String.valueOf(nb);
        int digitNb = sizeStr.length();
        return digitNb;
    }

    /**
     * Return a date in a dd-MM-yyyy HH:mm:ss format from a time in ms
     * @param time : the time in ms that you want to convert into the dd-MM-yyyy HH:mm:ss format
     *             (it is 01/01/1970 12:00:00 AM + time)
     * @return the date in a dd-MM-yyyy HH:mm:ss format
     */

    public String dateFormat(long time) {
        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String strDate = formatter.format(date);
        return strDate;
    }

    /**
     * Print spaces
     * @param nb : the number of space that you want to print
     */
    public void spacing(int nb)
    {
        while (nb != 0)
        {
            System.out.print(" ");
            nb--;
        }
    }

    /**
     * Print the content of a directory
     * @param input : the potential args of the command
     * @return 0 if the command is executed correctly -1 else
     */
    public int execute(String[] input) {

        File f = new File(Main.currPath);
        int max = -1;
        for (String s : f.list()) {

            File currF = new File(Main.currPath + "\\" + s);
            int digitNb = digitNb(currF.length());
            if (digitNb > max) {
                max = digitNb;
            }

        }

        System.out.print("Last Modified" + "        " + "Size" );
        spacing(max - 3);
        System.out.println("Execute" + " " + "Read" + "    " + "Write" + " " + "Name");

        String[] docList = f.list();
        if(docList == null) {
            System.out.println("No files in this directory");
            return -1;
        }
        for (String s : f.list()) {

            File currF = new File(Main.currPath + "\\" + s);
            String dateStr = dateFormat(currF.lastModified());
            int digitNb = digitNb(currF.length());

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
