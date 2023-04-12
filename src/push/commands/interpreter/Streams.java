package push.commands.interpreter;

import java.io.InputStream;
import java.io.OutputStream;

public class Streams {

    public Streams(OutputStream out, InputStream in, ProcessBuilder.Redirect inputRedirect, ProcessBuilder.Redirect outputRedirect) {
        this.in = in;
        this.out = out;
        this.inputRedirect = inputRedirect;
        this.outputRedirect = outputRedirect;
    }

    public Streams() {
        this.in = System.in;
        this.out = System.out;
        this.inputRedirect = ProcessBuilder.Redirect.INHERIT;
        this.outputRedirect = ProcessBuilder.Redirect.INHERIT;
    }

    public InputStream in;
    public OutputStream out;
    public ProcessBuilder.Redirect inputRedirect;
    public ProcessBuilder.Redirect outputRedirect;
}
