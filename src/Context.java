import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Class holding information about the current state of the program.
 */
public class Context {

    /**
     * Holds the path of the working directory of the shell.
     */
    public String currPath;
    public boolean shouldExit;

    /**
     * Holds the environment variables of the shell.
     */
    public Map<String, Variable> envVariables = new HashMap<>();


    /**
     * A list of commands. Commands are programs built into the shell.
     */
    public final Map<String, Command> builtinCommands;
    private final BufferedReader reader;

    private final Map<Long, Process> childProcesses;

    public Context(InputStream in) {
        shouldExit = false;
        reader = new BufferedReader(new InputStreamReader(in));
        this.builtinCommands = new HashMap<>();
        childProcesses = new HashMap<>();
    }

    public BufferedReader getReader() {
        return reader;
    }

    /**
     * Registers a function as a builtin command.
     * @param name The name of the command.
     * @param func The function to run when running the command.
     */
    public void registerCommand(String name, Command func) {
        builtinCommands.put(name, func);
    }

    /**
     * Runs the given command with arguments.
     * @param args The command name and arguments. The first argument is the command name.
     * @return 0 if the command resulted in a success, Integer.MIN_VALUE if the command is unknown,
     * other values when the command failed.
     */
    public int runCommand(String[] args) {
        Command func = builtinCommands.get(args[0]);
        if(func == null)
            return Integer.MIN_VALUE;

        return func.execute(args);
    }



    public int runProgram(String[] args) {
        String programPath = args[0];

        ProcessBuilder pb = new ProcessBuilder(args);
        pb.inheritIO();
        pb.directory(new File(Main.context().currPath));

        Process process = null;
        int retCode = Integer.MIN_VALUE;
        try {
            process = pb.start();
            childProcesses.put(process.pid(), process);
            process.waitFor();
            childProcesses.remove(process.pid());
            retCode = process.exitValue();
        } catch (IOException e) {
            IO.printlnErr("Could not run program \"" + programPath + "\" : Program not found.");
        } catch (InterruptedException e) {
            IO.printlnErr("Program \"" + programPath + "\" [" + process.pid() + "] was interrupted.");
        }

        return retCode;
    }

}
