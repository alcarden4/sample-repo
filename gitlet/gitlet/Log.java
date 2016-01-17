package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Starting at the current head commit, display information about each commit
 * backwards along the commit tree until the initial commit. This set of commit
 * nodes is called the commit's history. For every node in this history, the
 * information it should display is the commit id, the time the commit was
 * made, and the commit message.
 * @author Anna Cardenas
 *  */
public class Log implements Command {

    @Override
    public void invoke() {
        File gitlet = new File(".gitlet");
        if (!gitlet.exists()) {
            System.out.println("Not in an initialized gitlet directory.");
            return;
        }
        RoadMap roadMap;
        File inFile = new File(".gitlet/roadMap.ser");
        try {
            ObjectInputStream inp =
                new ObjectInputStream(new FileInputStream(inFile));
            roadMap = (RoadMap) inp.readObject();
            inp.close();
        } catch (IOException | ClassNotFoundException excp) {
            roadMap = null;
        }
        if (roadMap == null) {
            System.out.println("road map was null somehow?");
            return;
        }
        String headHash = roadMap.getHeadPointer();

        Commit headCommit = deserialize(headHash);
        helper(headCommit);
    }
    /** RETURNS a deserialized commit with the hashcode, HASH. */
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
    /** Recursively gets the commit, CURR, to string message. */
    private void helper(Commit curr) {
        if (curr == null) {
            return;
        } else {
            System.out.println(curr.toString());
            String parentHash = curr.getParent();
            helper(deserialize(parentHash));
        }
    }
}
