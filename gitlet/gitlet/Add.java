package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

/**
 * Adds a copy of the file as it currently exists to the staging area (see the
 * description of the commit command). For this reason, adding a file is also
 * called staging the file. The staging area should be somewhere in .gitlet. If
 * the current working version of the file is identical to the version in the
 * repository, do nothing.
 * @author Anna Cardenas
 */
public class Add implements Command {
    /** The name of the file we are staging. */
    private String fileName;

    /**
     * Create a new Add object, storing the S as the name of the file to stage.
     */
    Add(String s) {
        fileName = s;
    }

    @Override
    public void invoke() {
        File gitlet = new File(".gitlet");
        if (!gitlet.exists()) {
            System.out.println("Not in an initialized gitlet directory.");
            return;
        }
        File toStage = new File(fileName);
        if (!toStage.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        String toStageHash = Utils.sha1(Utils.readContents(toStage));
        RoadMap roadMap = deserializeRoadMap();
        Commit currentCommit = deserialize(roadMap.getHeadPointer());

        File inRemove = new File(".gitlet/to_be_removed/" + fileName);
        if (inRemove.exists()) {
            inRemove.delete();
        }
        HashMap<String, String> currBlobs = currentCommit.getBlobs();
        if (currBlobs.containsKey(fileName)
                && currBlobs.get(fileName).equals(toStageHash)) {
            return;
        }

        byte[] contents = Utils.readContents(toStage);
        File copiedFile = new File(".gitlet/staging_area/" + fileName);

        try {
            copiedFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Utils.writeContents(copiedFile, contents);
    }
    /** Deserializes a file into a commit object using a HASH and RETURNS
     *  this commit object. */
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
}
