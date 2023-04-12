package push.commands;

import push.commands.interpreter.Streams;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.ProcessBuilder.Redirect;


public class RedirectedCommand implements CommandList {

    private Command cmd;
    private File outputTarget;
    private File inputTarget;
    private boolean append;
    private boolean force;
    private boolean async;

    public RedirectedCommand() {
        cmd = null;
        this.outputTarget = null;
        this.inputTarget = null;
        this.append = false;
        this.force = false;
    }

    @Override
    public int execute(Streams streams) {
        Redirect outRedirect, inRedirect;
        OutputStream out = streams.out;
        InputStream in = streams.in;
        if(outputTarget != null) {
            if (append) {
                outRedirect = Redirect.appendTo(outputTarget);
            } else {
                outRedirect = Redirect.to(outputTarget);
            }
            try {
                out = new PrintStream(new FileOutputStream(outputTarget, append));
            } catch (IOException e) {
                System.err.println("push: Could not redirect output to file '" + outputTarget.getPath() + "'");
                e.printStackTrace();
            }
        } else {
            outRedirect = Redirect.INHERIT;
        }

        if (inputTarget == null) {
            inRedirect = Redirect.INHERIT;
        } else {
            inRedirect = Redirect.from(inputTarget);
            try {
                in = new FileInputStream(inputTarget);
            } catch (IOException e) {
                System.err.println("push: Could not redirect input from file '" + inputTarget.getPath() + "'");
                e.printStackTrace();
            }
        }

        streams = new Streams(out,
                              in,
                              outRedirect,
                              inRedirect);
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

    public void setAppend(boolean append) {
        this.append = append;
    }

    public void setForceOverride(boolean force) {
        this.force = force;
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
