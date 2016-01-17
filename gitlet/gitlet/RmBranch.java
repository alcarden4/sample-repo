package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * Deletes the branch with the given name. This only means to delete the pointer
 * associated with the branch; it does not mean to delete all commits that were
 * created under the branch, or anything like that.
 * @author Anna Cardenas
 */
public class RmBranch implements Command {
    /** Branch name we are going to remove. */
    private String branchName;

    /** Constructor for removing the branch, BNAME. */
    RmBranch(String bName) {
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
        if (!branches.containsKey(branchName)) {
            System.out.println(" A branch with that name does not exist.");
            return;
        } else if (branchName.equals(roadMap.getCurrentBranch())) {
            System.out.println("Cannot remove the current branch.");
            return;
        } else {
            branches.remove(branchName);
        }
        File outFile = new File(".gitlet/roadMap.ser");
        try {
            ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream(outFile));
            out.writeObject(roadMap);
            out.close();
        } catch (IOException excp) {
            excp.getMessage();
        }
    }
}
