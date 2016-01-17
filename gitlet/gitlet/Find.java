package gitlet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Prints out the ids of all commits that have the given commit message,
 * one per line. If there are multiple such commits, it prints the ids out
 * on separate lines.
 *@author Anna Cardenas
 *  */
public class Find implements Command {
    /** Log Message for finding commit. */
    private String logMessage;
    /** Constructs find command with the log message, MESSAGE. */
    Find(String message) {
        logMessage = message;
    }

    @Override
    public void invoke() {
        File gitlet = new File(".gitlet");
        if (!gitlet.exists()) {
            System.out.println("Not in an initialized gitlet directory.");
            return;
        }
        ArrayList<String> results = new ArrayList<String>();
        File commits = new File(".gitlet/commits");
        File[] commitHashes = commits.listFiles();
        for (File f : commitHashes) {
            Commit commit;
            File inFile = new File(".gitlet/commits/" + f.getName());
            try {
                ObjectInputStream inp =
                    new ObjectInputStream(new FileInputStream(inFile));
                commit = (Commit) inp.readObject();
                inp.close();
            } catch (IOException | ClassNotFoundException excp) {
                commit = null;
            }
            if (commit == null) {
                System.out.println("commit was null somehow?");
                return;
            }

            if (commit.getLogMessage().equals(logMessage)) {
                results.add(commit.getHash());
            }
        }
        if (results.size() == 0) {
            System.out.println("Found no commit with that message.");
            return;
        }
        for (String hash : results) {
            System.out.println(hash);
        }
        results.clear();
    }
}
