package push.commands;

import push.commands.interpreter.Streams;

public class SubshellCommand implements Command {

    private final Command cmd;

    public SubshellCommand(Command cmd) {
        this.cmd = cmd;
    }

    @Override
    public int execute(Streams streams) {
        //TODO: Launch push again and make it run the command
        return 0;
    }

    @Override
    public boolean isAsync() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setAsync(boolean async) {
        // TODO Auto-generated method stub
        
    }
}
