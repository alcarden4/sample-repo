package gitlet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
/**
 * Creates a new branch with the given name, and points it at the current head
 * node. A branch is nothing more than a name for a reference (a SHA-1
 * identifier) to a commit node. This command does NOT immediately switch to the
 * newly created branch (just as in real git). Before you ever call branch, your
 * code should be running with a default branch called "master".
 * @author Anna Cardenas
 */
public class Branch implements Command {
    /** The branch we are going to switch to. */
    private String branchName;
    /** Create a new branch object from BNAME passed in. */
    Branch(String bName) {
        branchName = bName;
    }

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
            ObjectInputStream inp = new ObjectInputStream(
                    new FileInputStream(inFile));
            roadMap = (RoadMap) inp.readObject();
            inp.close();
        } catch (IOException | ClassNotFoundException excp) {

            roadMap = null;
        }
        if (roadMap == null) {
            System.out.println("road map was null somehow?");
            return;
        }

        HashMap<String, String> branches = roadMap.getBranches();
        if (branches.containsKey(branchName)) {
            System.out.println("A branch with that name already exists.");
        } else {
            String headCommitHash = roadMap.getHeadPointer();
            branches.put(branchName, headCommitHash);
        }
        File outFile2 = new File(".gitlet/roadMap.ser");
        try {
            ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream(outFile2));
            out.writeObject(roadMap);
            out.close();
        } catch (IOException excp) {
            excp.getMessage();
        }

    }
}
