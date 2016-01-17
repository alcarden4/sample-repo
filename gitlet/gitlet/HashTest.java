package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;
import static org.junit.Assert.*;

public class HashTest {
/** This does not pass when running from eclipse, but it passes when running
 *  the command make check. I believe it is because they are accessing the
 *  files from different locations. To pass the junit test, I would have
 *  changed the file to "testing/src/wug.txt". */
    @Test
    public void testFileHash() {
        File wug = new File("../testing/src/wug.txt");
        String contentsWug = "This is a wug.";
        Utils.writeContents(wug, contentsWug.getBytes());
        File notwug = new File("../testing/src/notwug.txt");
        String contentsNotWug = "This is not a wug.";
        Utils.writeContents(notwug, contentsNotWug.getBytes());

        String wugHash = Utils.sha1(Utils.readContents(wug));
        String notwugHash = Utils.sha1(Utils.readContents(notwug));

        assertEquals(false, wugHash.equals(notwugHash));
    }

    @Test
    public void testCommitHash() {
        Command init1 = new Init();
        init1.invoke();
        File wug = new File("../testing/src/wug.txt");
        String contentsWug = "This is a wug.";
        Utils.writeContents(wug, contentsWug.getBytes());
        File notwug = new File("../testing/src/notwug.txt");
        String contentsNotWug = "This is not a wug.";
        Utils.writeContents(notwug, contentsNotWug.getBytes());
        ArrayList<File> filesToAdd = new ArrayList<File>();
        filesToAdd.add(wug);
        filesToAdd.add(notwug);

        Commit init = new Commit("initial commit", new Date());
        Commit init2 = new Commit("initial commit", new Date());
        Commit c1 = new Commit("2nd commit", new Date(), filesToAdd, init);


        String c1Hash = c1.getHash();
        String initHash = init.getHash();
        String init2Hash = init2.getHash();

        assertEquals(false, c1Hash.equals(initHash));
        assertEquals(true, initHash.equals(init2Hash));
    }
}
