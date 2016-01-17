package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.junit.Test;
import static org.junit.Assert.*;

public class CommitTest {
    /** Again, I understand that the File path given doesn't work when running
     *  from this junit application, but this is the path needed for running
     *  make check. */

    @Test
    public void testCommitClass() {
        Command init1 = new Init();
        init1.invoke();
        File wug = new File("../testing/src/wug.txt");
        File notwug = new File("../testing/src/notwug.txt");
        ArrayList<File> filesToAdd = new ArrayList<File>();
        filesToAdd.add(wug);

        ArrayList<File> filesToAdd2 = new ArrayList<File>();
        filesToAdd2.add(notwug);

        Commit init = new Commit("initial commit", new Date());
        Commit c1 = new Commit("2nd commit", new Date(), filesToAdd, init);
        Commit c2 = new Commit("3rd commit", new Date(), filesToAdd2, c1);


        HashMap<String, String> c1Files = c1.getBlobs();
        HashMap<String, String> c2Files = c2.getBlobs();

        assertEquals(1, c1Files.size());
        assertEquals(2, c2Files.size());

    }
}
