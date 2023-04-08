package push.commands.interpreter;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class Streams {

    public Streams(InputStream out, OutputStream in, ProcessBuilder.Redirect inputRedirect, ProcessBuilder.Redirect outputRedirect) {
        this.out = out;
        this.in = in;
        this.inputRedirect = inputRedirect;
        this.outputRedirect = outputRedirect;
    }

    public Streams() {
        this.out = System.in;
        this.in = System.out;
        this.inputRedirect = ProcessBuilder.Redirect.INHERIT;
        this.outputRedirect = ProcessBuilder.Redirect.INHERIT;
    }

    public InputStream out;
    public OutputStream in;
    public ProcessBuilder.Redirect inputRedirect;
    public ProcessBuilder.Redirect outputRedirect;
}
