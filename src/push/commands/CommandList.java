package push.commands;

public interface CommandList extends Command {

    void addCommand(Command cmd);

    int getCommandCount();

    Command[] subCommands();
}
