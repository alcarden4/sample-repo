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
 * Checks out all the files tracked by the given commit. Also moves the current
 * branch's head to that commit node. See the intro for an example of what
 * happens to the head pointer after using reset. The [commit id] may be
 * abbreviated as for checkout.
 * @author Anna Cardenas
 */
public class Reset implements Command {
    /** The ID of the commit we want to reset to. */
    private String commitId;
    /** Max ID_LENGTH our commitID is allowed to be for using shortened ID's. */
    private static final int ID_LENGTH = 40;

    /** Create a new Reset object that will reset to the files in COMMIT. */
    Reset(String commit) {
        commitId = commit;
    }

    @Override
    public void invoke() {
        File gitlet = new File(".gitlet");
        if (!gitlet.exists()) {
            System.out.println("Not in an initialized gitlet directory.");
            return;
        }

        Commit commitToResetTo;

        if (commitId.length() < ID_LENGTH) {
            HashMap<String, String> abbrevHash = new HashMap<String, String>();
            File allCommits = new File(".gitlet/commits");
            for (File commitHash : allCommits.listFiles()) {
                abbrevHash.put(
                        commitHash.getName().substring(0, commitId.length()),
                        commitHash.getName());
            }
            if (!abbrevHash.containsKey(commitId)) {
                System.out.println("No commit with that id exists.");
                return;
            }
            commitToResetTo = deserialize(abbrevHash.get(commitId));
        } else {
            commitToResetTo = deserialize(commitId);
        }
        if (commitToResetTo == null) {
            System.out.println("No commit with that id exists.");
            return;
        }
        HashMap<String, String> filesToResetTo = commitToResetTo.getBlobs();
        RoadMap roadMap = deserializeRoadMap();
        Commit currentCommit = deserialize(roadMap.getHeadPointer());
        HashMap<String, String> tracking = currentCommit.getBlobs();
        File curDir = new File(".");
        String[] fileNames = curDir.list();
        for (String f : fileNames) {
            if (!tracking.containsKey(f)) {
                if (filesToResetTo.containsKey(f)) {
                    System.out.println("There is an untracked file in the way;"
                            + " delete it or add it first.");
                    return;
                }
            }
        }
        deleteFilesinDir(curDir, tracking, filesToResetTo);
        for (String fileName : filesToResetTo.keySet()) {
            File file = new File(
                    ".gitlet/all_files/" + filesToResetTo.get(fileName));
            try {
                Files.copy(file.toPath(), (new File(fileName).toPath()),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        deleteFilesInStagingArea();
        roadMap.updateBranches(commitToResetTo);
        roadMap.setHeadPointer(commitToResetTo);
        reserializeRoadMap(roadMap);
    }
    /** RETURN commit that is deserialized using hashcode, HASH. */
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
    /** Delete the files in our stagingArea. */
    private void deleteFilesInStagingArea() {
        File stageArea = new File(".gitlet/staging_area");
        for (File f : stageArea.listFiles()) {
            f.delete();
        }
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
    /** Delete files in DIR if TRACKING hashmap contains the file and if
     *  RESETTO hashmap doesn't contain the file. */
    private void deleteFilesinDir(File dir, HashMap tracking, HashMap resetTo) {
        for (File file : dir.listFiles()) {
            if (tracking.containsKey(file.getName())) {
                if (!resetTo.containsKey(file.getName())) {
                    file.delete();
                }
            }
        }
    }

}
