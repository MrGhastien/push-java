package push.commands.interpreter;

import java.io.InputStream;
import java.io.OutputStream;

public class Streams {

    public Streams(InputStream out, OutputStream in) {
        this.out = out;
        this.in = in;
    }

    public Streams() {
        this.out = System.in;
        this.in = System.out;
    }

    public InputStream out;
    public OutputStream in;
}
