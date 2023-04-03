package push.commands;

import java.util.Arrays;
import java.util.LinkedList;

public class PipelineCommand implements Command, CommandList {

    private final LinkedList<Command> subCommands;
    private boolean async;

    public PipelineCommand(Command... subCommands) {
        this.subCommands = new LinkedList<>();
        this.subCommands.addAll(Arrays.asList(subCommands));
        async = false;
    }

    @Override
    public int execute() {
        int retCode = 0;
        for(Command c : subCommands) {

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

    @Override
    public boolean isAsync() {
        return async;
    }

    @Override
    public void setAsync(boolean async) {
        this.async = async;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[ ");
        for(Command c : subCommands) {
            builder.append(c.toString());
            if(!c.isAsync()) {
                builder.append("; ");
            }
        }
        builder.append(']');
        return builder.toString();
    }
}
