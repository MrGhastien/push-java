package push.commands;

/**
 * Represents a command.
 */
public interface Command {

    /**
     * Runs the command.
     * @return The return code of the command
     */
    int execute();

    /**
     * Indicates if this command is run asynchronously (i.e. the shell does not wait for the
     * command to complete before continuing).
     * @return <code>true</code> if the command is asynchronous, <code>false</code> otherwise.
     */
    boolean isAsync();

    /**
     * Sets wether this command should be run asynchronously or not.
     * @param async The new (a)synchronized flag.
     */
    void setAsync(boolean async);


}
