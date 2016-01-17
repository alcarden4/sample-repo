package gitlet;
/** Extra credit file for add-remote command. */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.regex.Matcher;

/**Saves the given login information under the given remote name. Attempts
 * to push or pull from the given remote name will then attempt to use this
 * .gitlet directory.
 * @author Anna Cardenas and Joy Gu
 *  */
public class AddRemote implements Command {
    /** Remote name for remote we are adding. */
    private String remoteName;
    /** Name of remote directory that remoteName is connected to. */
    private String remoteDirectory;
    /** Constructor for the add remote that will add RNAME and RDIR to the
     *  hashmap of remotes. */
    AddRemote(String rName, String rDir) {
        remoteName = rName;
        remoteDirectory = rDir;
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
        if (remotes.containsKey(remoteName)) {
            System.out.println("A remote with that name already exists.");
            return;
        } else {
            remoteDirectory = remoteDirectory.replaceAll("/",
                    Matcher.quoteReplacement(File.separator));
            remotes.put(remoteName, remoteDirectory);
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
