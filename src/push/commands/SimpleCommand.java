package push.commands;

import push.Context;
import push.Main;
import push.util.Lazy;

import java.util.LinkedList;
import java.util.List;

public class SimpleCommand implements Command {

    private final List<Lazy<String>> args;
    private boolean async;

    public SimpleCommand() {
        this.args = new LinkedList<>();
        async = false;
    }

    @Override
    public int execute() {
        String[] finalArgs = getArgs();
        Context ctx = Main.context();
        int retCode = ctx.runBuiltin(finalArgs, async);
        if(retCode != Integer.MIN_VALUE)
            return retCode;

        return ctx.runProgram(finalArgs, async);
    }

    public void addArg(Lazy<String> arg) {
        args.add(arg);
    }

    public int argCount() {
        return args.size();
    }

    private String[] getArgs() {
        LinkedList<String> collection = new LinkedList<>();

        for(Lazy<String> maybeLazy : args) {
            collection.add(maybeLazy.get());
        }

        return collection.toArray(new String[0]);
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
            str = args.get(0).get();

        if(async)
            str += " &";
        return str;
    }
}
