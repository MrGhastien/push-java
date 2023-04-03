package push.commands;

import java.util.LinkedList;
import java.util.List;

public class LogicListCommand implements Command, CommandList {

    private final LinkedList<Command> subCommands;
    private final LinkedList<Boolean> orOperators;
    private boolean async;

    public LogicListCommand(Command firstCommand, boolean or) {
        if (firstCommand == null)
            throw new IllegalArgumentException();
        this.subCommands = new LinkedList<>();
        this.subCommands.add(firstCommand);
        this.orOperators = new LinkedList<>();
        orOperators.add(or);
        this.async = false;
    }

    @Override
    public int execute() {
        int retCode = subCommands.get(0).execute();
        for (int i = 1; i < subCommands.size(); i++) {
            // Execute next command only if :
            // previousCode = 0 & operator = AND (false in 'orOperators' list)
            // previousCode != 0 & operator = OR (true in 'orOperators' list)
            if ((retCode == 0) ^ orOperators.get(i - 1))
                continue;

            retCode = subCommands.get(i).execute();
        }
        return retCode;
    }

    @Override
    public void addCommand(Command cmd) {
        addCommand(cmd, false);
    }

    /**
     * Adds a command to this logical command list, and specifies which operator to use
     * before executing the next command.
     * If the given command is the last one in the list, the operator will not be used when executing.
     *
     * @param cmd The command to add.
     * @param or  Indicates what operator links this command to the next one.
     *
     *            <code>true</code> if it is an 'or' operator, <code>false</code> if it is an 'and' operator.
     */
    public void addCommand(Command cmd, boolean or) {
        subCommands.add(cmd);
        orOperators.add(or);
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
        return async;
    }

    @Override
    public void setAsync(boolean async) {
        this.async = async;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(subCommands.size() + "L[ ");
        for (int i = 0; i < subCommands.size() - 1; i++) {
            Command c = subCommands.get(i);
            boolean or = orOperators.get(i);
            builder.append(c.toString());
            if (or) {
                builder.append(" || ");
            } else {
                builder.append(" && ");
            }
        }
        builder.append(subCommands.getLast());
        builder.append(" ]");
        if(async)
            builder.append(" &");
        return builder.toString();
    }

}
