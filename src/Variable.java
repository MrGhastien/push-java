public class Variable {

    String name;
    String value;
    boolean readOnly;

    public Variable(String name, String value, boolean readOnly) {
        this.name = name;
        this.value = value;
        this.readOnly = readOnly;
    }
}
