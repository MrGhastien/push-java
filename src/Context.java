import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    public Context(InputStream in, String[] params) {
        shouldExit = false;
        reader = new BufferedReader(new InputStreamReader(in));
        this.builtinCommands = new HashMap<>();
        childProcesses = new HashMap<>();
        previousRetCode = 0;
        this.params = Collections.unmodifiableList(Arrays.asList(params));
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

    /**
     * Runs the given command with arguments.
     * @param args The command name and arguments. The first argument is the command name.
     * @return 0 if the command resulted in a success, Integer.MIN_VALUE if the command is unknown,
     * other values when the command failed.
     */
    public int runBuiltin(String[] args) {
        BuiltinCommand func = builtinCommands.get(args[0]);
        if(func == null)
            return Integer.MIN_VALUE;

        previousRetCode = func.execute(args);
        return previousRetCode;
    }



    public int runProgram(String programPath, String[] args) {
        String[] cmd = new String[args.length + 1];
        cmd[0] = programPath;
        System.arraycopy(args, 0, cmd, 1, cmd.length - 1);

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.inheritIO();
        pb.directory(new File(Main.context().currPath));

        Process process = null;
        int retCode = Integer.MIN_VALUE;
        try {
            process = pb.start();
            lastProcId = process.pid();
            childProcesses.put(process.pid(), process);
            process.waitFor();
            childProcesses.remove(process.pid());
            retCode = process.exitValue();
        } catch (IOException e) {
            IO.printlnErr("Could not run program \"" + programPath + "\" : Program not found.");
        } catch (InterruptedException e) {
            IO.printlnErr("Program \"" + programPath + "\" [" + process.pid() + "] was interrupted.");
        }
        previousRetCode = retCode;
        return retCode;
    }

    public String getName() {
        return "name";
    }

    public List<String> getParameters() {
        return params;
    }
}
