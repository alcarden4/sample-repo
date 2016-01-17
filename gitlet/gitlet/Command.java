package gitlet;
/**
 * Command Interface to be used for all the possible commands in gitlet.
 *@author Anna Cardenas
 *  */

public interface Command {
    /** Each command will be invoked in some way specific to that command. */
    void invoke();
}
