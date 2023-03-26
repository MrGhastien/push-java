package commands;

import java.util.List;

public class LogicListCommand implements Command {

    private final List<Command> subCommands;
    private final List<Boolean> orOperators;

    public LogicListCommand(List<Command> subCommands, List<Boolean> orOperators) {
        this.subCommands = subCommands;
        this.orOperators = orOperators;
    }

    @Override
    public int execute() {
        int retCode = subCommands.get(0).execute();
        for(int i = 1; i < subCommands.size(); i++) {
            if((retCode == 0) == orOperators.get(i - 1))
                continue;

            retCode = subCommands.get(i).execute();
        }
        return retCode;
    }
}
