package gitlet;
/** Extra credit file for pull remote command. */

/**Fetches branch [remote name]/[remote branch name] as for the fetch command,
 * and then merges that fetch into the current branch.
 * @author Anna Cardenas
 *  */
public class Pull implements Command {
    /** Remote name for where we are pulling from. */
    private String remoteName;
    /** Name of remote branch that remoteName is connected to. Will be the
     *  directory path that we will pull from. */
    private String remoteBranch;
    /** Constructor for pull command that accesses the remote name RNAME and
     *  the remote branch RBRANCH. */
    Pull(String rName, String rBranch) {
        remoteName = rName;
        remoteBranch = rBranch;
    }
    @Override
    public void invoke() {
        Command fetch = new Fetch(remoteName, remoteBranch);
        fetch.invoke();
        Command merge = new Merge(remoteName + "/" + remoteBranch);
        merge.invoke();
    }
}
