package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Saves a snapshot of certain files in the current commit and staging area so
 * they can be restored at a later time, creating a new commit. The commit is
 * said to be tracking the saved files. By default, each commit's snapshot of
 * files will be exactly the same as its parent commit's snapshot of files; it
 * will keep versions of files exactly as they are, and not update them. A
 * commit will only update files it is tracking that have been staged at the
 * time of commit, in which case the commit will now include the version of the
 * file that was staged instead of the version it got from its parent. A commit
 * will save and start tracking any files that were staged but weren't tracked
 * by its parent.
 * @author Anna Cardenas
 */
public class CommitCommand implements Command {
    /** Log message for the commit. */
    private String logMessage;

    /** Constructor for the commit that takes in the log message, MESSAGE. */
    CommitCommand(String message) {
        logMessage = message;
    }

    @Override
    public void invoke() {
        File gitlet = new File(".gitlet");
        if (!gitlet.exists()) {
            System.out.println("Not in an initialized gitlet directory.");
            return;
        }
        if (logMessage.length() == 0) {
            System.out.println("Please enter a commit message.");
            return;
        }
        Date date = new Date();
        File stagedAreaDirectory = new File(".gitlet/staging_area");
        File[] filesInStage = stagedAreaDirectory.listFiles();
        File removedArea = new File(".gitlet/to_be_removed");
        File[] removedFile = removedArea.listFiles();
        List<File> stagedAreaFiles = Arrays.asList(filesInStage);
        if (filesInStage.length == 0 && removedFile.length == 0) {
            System.out.println("No changes added to the commit.");
            return;
        }
        RoadMap roadMap = deserializeRoadMap();
        String parentHash = roadMap.getHeadPointer();
        Commit parentCommit = deserialize(parentHash);
        Commit newCommit = new Commit(logMessage, date, stagedAreaFiles,
                parentCommit);
        File outFile = new File(".gitlet/commits/" + newCommit.getHash());
        try {
            ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream(outFile));
            out.writeObject(newCommit);
            out.close();
        } catch (IOException excp) {
            excp.getMessage();
        }
        roadMap.setHeadPointer(newCommit);
        roadMap.updateBranches(newCommit);
        File outFile2 = new File(".gitlet/roadMap.ser");
        try {
            ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream(outFile2));
            out.writeObject(roadMap);
            out.close();
        } catch (IOException excp) {
            excp.getMessage();
        }
        String path = ".gitlet/all_files/";
        for (File file : filesInStage) {
            byte[] readFile = Utils.readContents(file);
            try {
                Files.copy(file.toPath(),
                        (new File(path + Utils.sha1(readFile))).toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (File f : filesInStage) {
            f.delete();
        }
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
}
