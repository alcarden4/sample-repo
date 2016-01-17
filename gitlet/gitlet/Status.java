package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * Displays what branches currently exist, and marks the current branch with
 * a *. Also displays what files have been staged or marked for untracking.
 *@author Anna Cardenas
 *  */

public class Status implements Command {

    @Override
    public void invoke() {
        File gitlet = new File(".gitlet");
        if (!gitlet.exists()) {
            System.out.println("Not in an initialized gitlet directory.");
            return;
        }
        RoadMap roadMap = deserializeRoadMap();
        TreeSet<String> branchNames =
                new TreeSet<String>(roadMap.getBranches().keySet());
        Iterator<String> branchIter = branchNames.iterator();
        String currentBranch = roadMap.getCurrentBranch();
        System.out.println("=== Branches ===");
        while (branchIter.hasNext()) {
            String branch = branchIter.next();
            if (branch.equals(currentBranch)) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println('\n' + "=== Staged Files ===");
        File stagedAreaDirectory = new File(".gitlet/staging_area");
        printFiles(Utils.plainFilenamesIn(stagedAreaDirectory));

        System.out.println('\n' + "=== Removed Files ===");
        File removedDirectory = new File(".gitlet/to_be_removed");
        printFiles(Utils.plainFilenamesIn(removedDirectory));

        Commit currentCommit = deserialize(roadMap.getHeadPointer());

        System.out.println('\n'
                + "=== Modifications Not Staged For Commit ===");
        printModified(currentCommit.getBlobs());

        System.out.println('\n' + "=== Untracked Files ===");
        printUntracked(currentCommit.getBlobs());
        System.out.println('\n');
    }

    /**
     * Prints any file that has been modified, also needs TRACKED files.
     * */
    private void printModified(HashMap<String, String> tracked) {
        TreeSet<String> modified = new TreeSet<String>();
        File curDirectory = new File(".");

        File stagedDir = new File(".gitlet/staging_area");
        for (String fileName : Utils.plainFilenamesIn(stagedDir)) {
            if (isDeleted(fileName, curDirectory)) {
                modified.add(fileName + " (deleted)");
            } else {
                File file = new File(fileName);
                byte[] vals = Utils.readContents(file);
                String hashInWorking = Utils.sha1(vals);

                File file2 = new File(".gitlet/staging_area/" + fileName);
                byte[] vals2 = Utils.readContents(file2);
                String hashInStaged = Utils.sha1(vals2);

                if (hasDiffContents(hashInWorking, hashInStaged)) {
                    modified.add(fileName + " (modified)");
                }
            }
        }
        File removedDir = new File(".gitlet/to_be_removed");
        for (String fileName : tracked.keySet()) {
            if (isDeleted(fileName, removedDir)) {
                if (isDeleted(fileName, curDirectory)) {
                    modified.add(fileName + " (deleted)");
                }
            }
            if (isDeleted(fileName, stagedDir)) {
                if (!isDeleted(fileName, curDirectory)) {
                    File file = new File(fileName);
                    byte[] vals = Utils.readContents(file);
                    String hashInWorking = Utils.sha1(vals);
                    String hashInTracked = tracked.get(fileName);
                    if (hasDiffContents(hashInWorking, hashInTracked)) {
                        modified.add(fileName + " (modified)");
                    }
                }
            }
        }
        Iterator<String> iter = modified.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }
    }

    /** Returns true if the FILENAME is not in this DIRECTORY. */
    private boolean isDeleted(String fileName, File directory) {
        List<String> filesInDir = Utils.plainFilenamesIn(directory);
        return !filesInDir.contains(fileName);
    }

    /** Returns true if HASH1 is not equal to HASH2. */
    private boolean hasDiffContents(String hash1, String hash2) {
        return !hash1.equals(hash2);
    }

    /** Prints file names present in the working directory but neither staged
     * for addition nor TRACKED. */
    private void printUntracked(HashMap<String, String> tracked) {
        File curDirectory = new File(".");
        List<String> present = Utils.plainFilenamesIn(curDirectory);

        File stageDirectory = new File(".gitlet/staging_area");
        List<String> staged = Utils.plainFilenamesIn(stageDirectory);

        for (String fileName : present) {
            if (!staged.contains(fileName)) {
                if (!tracked.containsKey(fileName)) {
                    System.out.println(fileName);
                }
            }
        }
    }

    /** Deserialize and return the commit corresponding to HASH. */
    private Commit deserialize(String hash) {
        Commit commit;
        File inFile2 = new File(".gitlet/commits/" + hash);
        try {
            ObjectInputStream inp = new ObjectInputStream(
                    new FileInputStream(inFile2));
            commit = (Commit) inp.readObject();
            inp.close();
        } catch (IOException | ClassNotFoundException excp) {

            commit = null;
        }
        return commit;
    }

    /** Deserialize and return the roadMap. */
    private RoadMap deserializeRoadMap() {
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
        return roadMap;
    }

    /** Print the file names in FILES in order. */
    private void printFiles(List<String> files) {
        for (String f : files) {
            System.out.println(f);
        }
    }
}
