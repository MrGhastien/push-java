public class Variable {

    String name;
    String value;
    boolean readOnly;

    public Variable(String name, String value, boolean readOnly) {
        this.name = name;
        this.value = value;
        this.readOnly = readOnly;
    }

    public static boolean isValidChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '-' || c == '_';
    }
}
