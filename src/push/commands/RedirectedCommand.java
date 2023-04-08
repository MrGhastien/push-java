package push.commands;

import push.commands.interpreter.Streams;

import java.io.File;

public class RedirectedCommand implements CommandList {

    private Command cmd;
    private File outputTarget;
    private File inputTarget;
    private boolean async;

    public RedirectedCommand(File outputTarget, File inputTarget) {
        cmd = null;
        this.outputTarget = outputTarget;
        this.inputTarget = inputTarget;
    }

    @Override
    public int execute(Streams streams) {
        streams = new Streams(streams.out,
                streams.in,
                ProcessBuilder.Redirect.from(inputTarget),
                ProcessBuilder.Redirect.to(outputTarget));
        return cmd.execute(streams);
    }

    @Override
    public boolean isAsync() {
        return async;
    }

    @Override
    public void setAsync(boolean async) {
        this.async = async;
    }

    public File getOutputTarget() {
        return outputTarget;
    }

    public void setOutputTarget(File outputTarget) {
        this.outputTarget = outputTarget;
    }

    public File getInputTarget() {
        return inputTarget;
    }

    public void setInputTarget(File inputTarget) {
        this.inputTarget = inputTarget;
    }

    @Override
    public void addCommand(Command cmd) {
        if(this.cmd != null)
            throw new IllegalStateException("RedirectedCommand can only have one command");
        this.cmd = cmd;
    }

    @Override
    public int getCommandCount() {
        return cmd == null ? 0 : 1;
    }

    @Override
    public Command[] subCommands() {
        return cmd == null ? new Command[0] : new Command[]{this.cmd};
    }
}
