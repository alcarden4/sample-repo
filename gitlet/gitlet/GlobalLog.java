package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Like log, except displays information about all commits ever made.
 * The order of the commits does not matter.
 *@author Anna Cardenas
 *  */

public class GlobalLog implements Command {

    @Override
    public void invoke() {
        File gitlet = new File(".gitlet");
        if (!gitlet.exists()) {
            System.out.println("Not in an initialized gitlet directory.");
            return;
        }
        File commitDirectory = new File(".gitlet/commits");
        File[] allCommits = commitDirectory.listFiles();
        for (int i = 0; i < allCommits.length; i += 1) {
            Commit c = deserialize(allCommits[i].getName());
            System.out.println(c.toString());
        }
    }
    /** Deserializes a commit by the hashcode, HASH given.
     *  RETURN this commit. */
    private Commit deserialize(String hash) {
        Commit commit;
        File inFile2 = new File(".gitlet/commits/" + hash);
        try {
            ObjectInputStream inp =
                new ObjectInputStream(new FileInputStream(inFile2));
            commit = (Commit) inp.readObject();
            inp.close();
        } catch (IOException | ClassNotFoundException excp) {
            commit = null;
        }
        return commit;
    }
}
