package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Untrack the file; that is, indicate (somewhere in the .gitlet directory)
 *  that it is not to be included in the next commit, even if it is
 *  tracked in the current commit (which will become the next commit's parent).
 *  Remove the file from the working directory if it was tracked in the
 *  current commit. If the file had been staged, then unstage it, but don't
 *  remove it from the working directory unless it was tracked in the
 *  current commit.
 * @author Anna Cardenas
 **/

public class Rm implements Command {
    /** The file name we will remove. */
    private String fileName;
    /** Rm constructor that takes in the filename FILE. */
    Rm(String file) {
        fileName = file;
    }

    @Override
    public void invoke() {
        File gitlet = new File(".gitlet");
        if (!gitlet.exists()) {
            System.out.println("Not in an initialized gitlet directory.");
            return;
        }
        if (!stageHoldsFile() && !trackedByHead()) {
            System.out.println("No reason to remove the file");
            return;
        }
        File rmFile = new File(fileName);

        if (!rmFile.exists()) {
            RoadMap roadMap = deserializeRoadMap();
            Commit headCommit = deserialize(roadMap.getHeadPointer());
            rmFile = new File(".gitlet/all_files/"
                    + headCommit.getBlobs().get(fileName));
        }

        byte[] contents = Utils.readContents(rmFile);
        File copiedFile = new File(".gitlet/to_be_removed/" + fileName);
        try {
            copiedFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Utils.writeContents(copiedFile, contents);
        if (trackedByHead()) {
            Utils.restrictedDelete(fileName);
        }
        if (stageHoldsFile()) {
            File deletedFile = new File(".gitlet/staging_area/" + fileName);
            deletedFile.delete();
            if (!trackedByHead()) {
                File deleteTrack = new File(".gitlet/to_be_removed/"
                        + fileName);
                deleteTrack.delete();
            }
        }
    }
    /** Returns true if the staging area holds the file. */
    public boolean stageHoldsFile() {
        File stagingArea = new File(".gitlet/staging_area");
        File[] filesInStage = stagingArea.listFiles();
        List<File> stagedAreaFiles =  Arrays.asList(filesInStage);
        boolean flag = false;
        for (File f : stagedAreaFiles) {
            if (f.getName().equals(fileName)) {
                flag = true;
                return flag;
            }
        }
        return flag;
    }
    /** Returns true if the file is tracked by the head commit. */
    public boolean trackedByHead() {
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
            return false;
        }
        String parentHash = roadMap.getHeadPointer();
        Commit parentCommit;
        File inFile2 = new File(".gitlet/commits/" + parentHash);
        try {
            ObjectInputStream inp =
                new ObjectInputStream(new FileInputStream(inFile2));
            parentCommit = (Commit) inp.readObject();
            inp.close();
        } catch (IOException | ClassNotFoundException excp) {
            parentCommit = null;
        }
        if (parentCommit == null) {
            System.out.println("parent commit was null somehow?");
            return false;
        }
        HashMap<String, String> myBlobs = parentCommit.getBlobs();
        if (myBlobs.containsKey(fileName)) {
            return true;
        }
        File outFile2 = new File(".gitlet/roadMap.ser");
        try {
            ObjectOutputStream out =
                new ObjectOutputStream(new FileOutputStream(outFile2));
            out.writeObject(roadMap);
            out.close();
        } catch (IOException excp) {
            excp.getMessage();
        }
        return false;
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
}
