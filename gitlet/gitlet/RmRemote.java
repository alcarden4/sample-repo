package gitlet;
/** Extra credit file for rm-remote command. */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**Remove information associated with the given remote name. The idea here is
 * that if you ever wanted to change a remote that you added, you would have
 * to first remove it and then re-add it.
 * @author Anna Cardenas and Joy Gu
 *  */
public class RmRemote implements Command {
    /** Remote name for remote we are adding. */
    private String remoteName;
    /** Constructor for the remove remote command that takes in name, RNAME. */
    RmRemote(String rName) {
        remoteName = rName;
    }
    @Override
    public void invoke() {
        File gitlet = new File(".gitlet");
        if (!gitlet.exists()) {
            System.out.println("Not in an initialized gitlet directory.");
            return;
        }
        RoadMap roadMap = deserializeRoadMap();
        HashMap<String, String> remotes = roadMap.getRemotes();
        if (!remotes.containsKey(remoteName)) {
            System.out.println("A remote with that name does not exist.");
            return;
        } else {
            remotes.remove(remoteName);
            reserializeRoadMap(roadMap);
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
}
