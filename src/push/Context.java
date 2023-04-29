package push;

import push.commands.interpreter.Streams;

import java.io.*;
import java.util.*;

/**
 * Class holding information about the current state of the program.
 */
public class Context {

    /**
     * Holds the path of the working directory of the shell.
     */
    public String currPath;
    public boolean shouldExit;
    private int previousRetCode;
    private long lastProcId;

    /**
     * Holds the environment variables of the shell.
     */
    public Map<String, Variable> envVariables = new HashMap<>();


    /**
     * A list of commands. Commands are programs built into the shell.
     */
    public final Map<String, BuiltinCommand> builtinCommands;
    public final List<String> params;
    private final BufferedReader reader;

    private final Map<Long, Process> childProcesses;

    private final Map<Integer,Thread> threads = new HashMap<>();

    public Context(InputStream in, String[] params) {
        shouldExit = false;
        reader = new BufferedReader(new InputStreamReader(in));
        this.builtinCommands = new HashMap<>();
        childProcesses = new HashMap<>();
        previousRetCode = 0;
        currPath = System.getProperty("user.dir");
        this.params = Collections.unmodifiableList(Arrays.asList(params));
        Map<String, String> env = System.getenv();
        for(String key : env.keySet()) {
            envVariables.put(key, new Variable(key, env.get(key), false));
        }
    }

    public int getPreviousRetCode() {
        return this.previousRetCode;
    }

    public long getLastPid() {
        return lastProcId;
    }

    public BufferedReader getReader() {
        return reader;
    }

    /**
     * Registers a function as a builtin command.
     * @param name The name of the command.
     * @param func The function to run when running the command.
     */
    public void registerBuiltin(String name, BuiltinCommand func) {
        builtinCommands.put(name, func);
    }

    public int run(String[] args, boolean async, Streams streams) {
        int retCode = runBuiltin(args, async, streams);
        if(retCode == Integer.MIN_VALUE)
            retCode = runProgram(args, async, streams);
        previousRetCode = retCode;
        return retCode;
    }

    /**
     * Runs the given command with arguments.
     * @param args The command name and arguments. The first argument is the command name.
     * @return 0 if the command resulted in a success, Integer.MIN_VALUE if the command is unknown,
     * other values when the command failed.
     */
    public int runBuiltin(String[] args, boolean async, Streams streams) {
        BuiltinCommand func = builtinCommands.get(args[0]);
        PrintStream systemOut = System.out;
        InputStream systemIn = System.in;
        int retCode;
        System.setOut((PrintStream) streams.out);
        System.setIn(streams.in);
        if (func == null) {
            resetStreams(systemOut, systemIn, streams);
            retCode = Integer.MIN_VALUE;
        } else if(async) {
            Thread t = new Thread(() -> func.execute(args));
            threads.put(threads.size()+1,t);
            t.start();
            retCode = 0;
        } else {
            retCode = func.execute(args);
        }
        resetStreams(systemOut, systemIn, streams);
        return retCode;
    }

    private void resetStreams(PrintStream systemOut, InputStream systemIn, Streams streams) {
        System.setOut(systemOut);
        System.setIn(systemIn);
        if (systemOut != streams.out) {
            try {
                streams.out.close();
            } catch (IOException e) {
                System.err.println("push: Error when closing redirected output stream :");
                e.printStackTrace();
            }
        }
        if (systemIn != streams.in) {
            try {
                streams.out.close();
            } catch (IOException e) {
                System.err.println("push: Error when closing redirected input stream :");
                e.printStackTrace();
            }
        }
    }



    public int runProgram(String[] args, boolean async, Streams streams) {

        ProcessBuilder pb = new ProcessBuilder(args);
        pb.redirectOutput(streams.outputRedirect);
        pb.redirectInput(streams.inputRedirect);
        pb.redirectErrorStream(true);
        pb.directory(new File(Main.context().currPath));

        Process process = null;
        int retCode = Integer.MIN_VALUE;
        try {
            process = pb.start();
            streams.in = process.getInputStream();
            streams.out = process.getOutputStream();
            lastProcId = process.pid();
            childProcesses.put(process.pid(), process);
            if(!async) {
                process.waitFor();
                childProcesses.remove(process.pid());
                retCode = process.exitValue();
            } else {
                retCode = 0;
            }

        } catch (IOException e) {
            IO.printlnErr("Could not run program \"" + args[0] + "\" : Program not found.");
        } catch (InterruptedException e) {
            IO.printlnErr("Program \"" + args[0] + "\" [" + process.pid() + "] was interrupted.");
        }
        streams.in = System.in;
        streams.out = System.out;
        return retCode;
    }

    public String getName() {
        return "name";
    }

    public List<String> getParameters() {
        return params;
    }
}


