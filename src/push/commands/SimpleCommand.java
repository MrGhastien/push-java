package push.commands;

import push.Context;
import push.Main;
import push.commands.interpreter.Streams;
import push.util.Lazy;

import java.util.LinkedList;
import java.util.List;

public class SimpleCommand implements Command {

    private final List<String> args;
    private boolean async;

    public SimpleCommand() {
        this.args = new LinkedList<>();
        async = false;
    }

    @Override
    public int execute(Streams streams) {
        String[] finalArgs = getArgs().toArray(new String[0]);
        Context ctx = Main.context();
        return ctx.run(finalArgs, isAsync(),streams);
    }

    public void addArg(String arg) {
        args.add(arg);
    }

    public int argCount() {
        return args.size();
    }

    public List<String> getArgs() {
        return args;
    }

    @Override
    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    @Override
    public String toString() {
        String str;
        if(argCount() == 0)
            str = "";
        else
            str = args.get(0);

        if(async)
            str += " &";
        return str;
    }
}
