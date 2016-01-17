package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

/**
 * Implementation of checking out files. NOTE: LOTS OF SIMILAR CODE. PERHAPS TRY
 * TO CONDENSE LATER.
 * @author Anna Cardenas
 */

public class Checkout implements Command {
    /**
     * objName is either the branch name or file name depending on command
     * entered.
     */
    private String objName;
    /** The commit hash that we want to pull the file from. */
    private String commitID;
    /** Max IDLENGTH our commitID is allowed to be for using shortened ID's. */
    private static final int ID_LENGTH = 40;

    /** Constructor used when checking out a branch using the
     *  file name FNAME. */
    Checkout(String fName) {
        objName = fName;
    }
    /** Constructor used when checking out a branch given the COMMITHASH
     *  and the file name, FNAME. */
    Checkout(String commitHash, String fName) {
        commitID = commitHash;
        objName = fName;
    }

    /**
     * 1) Takes the version of the file as it exists in the head commit, the
     * front of the current branch, and puts it in the working directory,
     * overwriting the version of the file that's already there if there is one.
     * For command: java gitlet.Main checkout -- hello.txt
     */
    @Override
    public void invoke() {
        /**
         * Takes file as it is in the head commit and copies it to the working
         * directory.
         */
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

        String mostRecentCommit = roadMap.getHeadPointer();
        Commit ourCommit = deserialize(mostRecentCommit);

        if (ourCommit == null) {
            System.out.println("parent commit was null somehow?");
            return;
        }
        HashMap<String, String> blobs = ourCommit.getBlobs();
        if (!blobs.containsKey(objName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        String fileHash = blobs.get(objName);
        File file = new File(".gitlet/all_files/" + fileHash);
        try {
            Files.copy(file.toPath(), (new File(objName).toPath()),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Invoke for command: java gitlet.Main checkout [commit id] -- [file name]
     * 2)Takes the version of the file as it exists in the commit with the given
     * id, and puts it in the working directory, overwriting the version of the
     * file that's already there if there is one.
     * Hashmap abbrev hash holds the abbreviated hash as the key and the full
     * hash as the value.
     */
    public void invoke2() {
        File gitlet = new File(".gitlet");
        if (!gitlet.exists()) {
            System.out.println("Not in an initialized gitlet directory.");
            return;
        }
        if (commitID.length() < ID_LENGTH) {
            HashMap<String, String> abbrevHash = new HashMap<String, String>();
            File allCommits = new File(".gitlet/commits");
            for (File commitHash : allCommits.listFiles()) {
                abbrevHash.put(
                        commitHash.getName().substring(0, commitID.length()),
                        commitHash.getName());
            }
            if (!abbrevHash.containsKey(commitID)) {
                System.out.println("No commit with that id exists.");
                return;
            }
            Commit ourCommit = deserialize(abbrevHash.get(commitID));
            if (ourCommit == null) {
                System.out.println("No commit with that id exists.");
                return;
            }
            HashMap<String, String> blobs = ourCommit.getBlobs();
            if (!blobs.containsKey(objName)) {
                System.out.println("File does not exist in that commit.");
                return;
            }
            String fileHash = blobs.get(objName);
            File file = new File(".gitlet/all_files/" + fileHash);
            try {
                Files.copy(file.toPath(), (new File(objName).toPath()),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Commit ourCommit = deserialize(commitID);

            if (ourCommit == null) {
                System.out.println("No commit with that id exists.");
                return;
            }
            HashMap<String, String> blobs = ourCommit.getBlobs();
            if (!blobs.containsKey(objName)) {
                System.out.println("File does not exist in that commit.");
                return;
            }
            String fileHash = blobs.get(objName);
            File file = new File(".gitlet/all_files/" + fileHash);
            try {
                Files.copy(file.toPath(), (new File(objName).toPath()),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Invoke for command: java gitlet.Main checkout [branch name] 3) Takes all
     * files in the commit at the head of the given branch, and puts them in the
     * working directory, overwriting the versions of the files that are already
     * there if they exist. Also, at the end of this command, the given branch
     * will now be considered the current branch (HEAD). Any files that are
     * tracked in the current directory but are not present in the checked-out
     * branch are deleted.
     */
    public void invoke3() {
        File gitlet = new File(".gitlet");
        if (!gitlet.exists()) {
            System.out.println("Not in an initialized gitlet directory.");
            return;
        }
        RoadMap roadMap = deserializeRoadMap();
        HashMap<String, String> branches = roadMap.getBranches();
        if (!branches.containsKey(objName)) {
            System.out.println("No such branch exists.");
            return;
        }
        if (objName.equals(roadMap.getCurrentBranch())) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        String mostRecentCommit = branches.get(objName);
        Commit ourCommit = deserialize(mostRecentCommit);
        if (ourCommit == null) {
            System.out.println("parent commit was null somehow?");
            return;
        }
        HashMap<String, String> blobs = ourCommit.getBlobs();
        Commit currentCommit = deserialize(roadMap.getHeadPointer());
        HashMap<String, String> tracking = currentCommit.getBlobs();
        File curDir = new File(".");
        String[] fileNames = curDir.list();
        for (String f : fileNames) {
            if (!tracking.containsKey(f)) {
                if (blobs.containsKey(f)) {
                    System.out.println("There is an untracked file in the way;"
                            + " delete it or add it first.");
                    return;
                }
            }
        }
        for (File file : curDir.listFiles()) {
            if (tracking.containsKey(file.getName())) {
                if (!blobs.containsKey(file.getName())) {
                    file.delete();
                }
            }
        }
        for (String fileName : blobs.keySet()) {
            File file = new File(".gitlet/all_files/" + blobs.get(fileName));
            try {
                Files.copy(file.toPath(), (new File(fileName).toPath()),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        deleteFilesInStagingArea();
        roadMap.setBranchPointer(objName);
        Commit currBranchCommit = deserialize(
                roadMap.getBranches().get(objName));
        roadMap.setHeadPointer(currBranchCommit);
        reserializeRoadMap(roadMap);
    }
    /** Deserialize the roadMap and RETURNS the roadmap. */
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
        if (roadMap == null) {
            System.out.println("road map was null somehow?");
        }
        return roadMap;

    }
    /** RETURN a deserialized commit object using the HASH. */
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
    /** Reserialize our ROADMAP. */
    private void reserializeRoadMap(RoadMap roadMap) {
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
    /** Delete the files in our stagingArea. */
    private void deleteFilesInStagingArea() {
        File stageArea = new File(".gitlet/staging_area");
        for (File f : stageArea.listFiles()) {
            f.delete();
        }
    }
}
