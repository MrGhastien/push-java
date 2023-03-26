public class Platform {

    public static char pathSeparator() {
        String name = System.getProperty("os.name").toLowerCase();
        if(name.contains("windows")) {
            return '\\';
        }
        return '/';
    }

}
