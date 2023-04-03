package push.commands;

import java.util.LinkedList;
import java.util.List;

public class SeqListCommand implements Command, CommandList {

    private final LinkedList<Command> subCommands;

    public SeqListCommand(Command... subCommands) {
        this.subCommands = new LinkedList<>();
        this.subCommands.addAll(List.of(subCommands));
    }

    @Override
    public int execute() {
        int retCode = 0;
        for(int i = 0; i < subCommands.size(); i++) {
            Command c = subCommands.get(i);
            retCode = c.execute();
        }
        return retCode;
    }

    @Override
    public void addCommand(Command cmd) {
        subCommands.add(cmd);
    }

    @Override
    public Command getLastCommand() {
        return subCommands.getLast();
    }

    @Override
    public int getCommandCount() {
        return subCommands.size();
    }

    public List<Command> commands() {
        return subCommands;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void setAsync(boolean async) { }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(subCommands.size());
        builder.append("[ ");
        for(Command c : subCommands) {
            builder.append(c.toString());
            if(!c.isAsync()) {
                builder.append(";");
            }
            builder.append(' ');
        }
        builder.append(']');
        return builder.toString();
    }
}
