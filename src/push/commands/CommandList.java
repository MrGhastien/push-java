package push.commands;

public interface CommandList extends Command {

    void addCommand(Command cmd);

    Command getLastCommand();

    int getCommandCount();
}
