package commands;

public class SimpleCommand implements Command {

    private final String programName;
    private final String[] args;

    public SimpleCommand(String programName, String[] args) {
        this.programName = programName;
        this.args = args;
    }

    @Override
    public int execute() {
        //Context ctx = Main.context();
        return 0;
    }
}
