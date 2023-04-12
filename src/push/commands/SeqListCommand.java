package push.commands;

import push.commands.interpreter.Streams;

import java.util.LinkedList;
import java.util.List;

public class SeqListCommand implements Command, CommandList {

    private final LinkedList<Command> subCommands;

    public SeqListCommand(Command... subCommands) {
        this.subCommands = new LinkedList<>();
        this.subCommands.addAll(List.of(subCommands));
    }

    @Override
    public int execute(Streams streams) {
        int retCode = 0;
        for(int i = 0; i < subCommands.size(); i++) {
            Command c = subCommands.get(i);
            retCode = c.execute(streams);
        }
        return retCode;
    }

    @Override
    public void addCommand(Command cmd) {
        subCommands.add(cmd);
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
        builder.append("S[ ");
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

    @Override
    public Command[] subCommands() {
        return subCommands.toArray(new Command[0]);
    }
}
