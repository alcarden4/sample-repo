package gitlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 * Creates a new gitlet version-control system in the current directory. This
 * system will automatically start with one commit: a commit that contains no
 * files and has the commit message initial commit.
 * @author Anna Cardenas
 */
public class Init implements Command {

    @Override
    public void invoke() {
        File gitlet = new File(".gitlet");
        if (gitlet.exists()) {
            System.out.println("A gitlet version-control system already exists"
                    + " in the current directory.");
            return;
        } else {
            gitlet.mkdir();
            File commits = new File(".gitlet/commits");
            commits.mkdir();
            Date date = new Date();
            Commit initial = new Commit("initial commit", date);
            File firstFile = new File(".gitlet/commits/" + initial.getHash());
            try {
                ObjectOutputStream out = new ObjectOutputStream(
                        new FileOutputStream(firstFile));
                out.writeObject(initial);
                out.close();
            } catch (IOException excp) {
                System.out.println(excp.getMessage());
            }
            RoadMap roadMap = new RoadMap(initial.getHash());
            File roadMapSer = new File(".gitlet/roadMap.ser");
            try {
                ObjectOutputStream out = new ObjectOutputStream(
                        new FileOutputStream(roadMapSer));
                out.writeObject(roadMap);
                out.close();
            } catch (IOException excp) {
                System.out.println(excp.getMessage());
            }
            File allFiles = new File(".gitlet/all_files");
            allFiles.mkdir();
            File stagingArea = new File(".gitlet/staging_area");
            stagingArea.mkdir();
            File removed = new File(".gitlet/to_be_removed");
            removed.mkdir();
        }
    }
}
