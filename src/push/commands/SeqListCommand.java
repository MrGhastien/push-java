package push.commands;

import java.util.List;

public class SeqListCommand implements Command {

    private final List<Command> subCommands;

    public SeqListCommand(List<Command> subCommands) {
        this.subCommands = subCommands;
    }

    @Override
    public int execute() {
        int retCode = 0;
        for(Command c : subCommands) {
            retCode = c.execute();
        }
        return retCode;
    }
}
