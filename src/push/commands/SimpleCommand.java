package push.commands;

import java.util.ArrayList;
import java.util.List;

import push.Context;
import push.Main;
import push.commands.interpreter.Streams;

public class SimpleCommand implements Command {

    private final List<String> args;
    private boolean async;

    public SimpleCommand() {
        this.args = new ArrayList<>();
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
        StringBuilder str = new StringBuilder();
        if(argCount() == 0)
            str.append("");
        else {
            str.append(args.get(0));
            for (int i = 1; i < argCount(); i++) {
                str.append(' ');
                str.append(args.get(i));
            }
        }

        if(async)
            str.append(" &");
        return str.toString();
    }
}
