package push;

public class Variable {

    String name;
    String value;
    boolean readOnly;

    public Variable(String name, String value, boolean readOnly) {
        this.name = name;
        this.value = value;
        this.readOnly = readOnly;
    }

    /**
     * Checks is a given character is valid for a variable name.
     * Valid characters are lower and uppercase letters (a-zA-Z), digits (0-9), dashes (-) and underscores (_).
     * @param c The character to check
     * @return <code>true</code> if the character is any of the ones described above, <code>false</code> otherwise.
     */
    public static boolean isValidChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '-' || c == '_';
    }

    public String get() {
        return value;
    }
}
