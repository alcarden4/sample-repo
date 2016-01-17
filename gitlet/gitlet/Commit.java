package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Our Commit class which must store a log message, files (default to none),
 * and a parent. Commits need a time stamp, hashmap to map hashcodes to the
 * file contents the commit carries, parent reference.
 *@author Anna and Joy
 * */
public class Commit implements Serializable {
    /**
     * This just came with it, unsure about it.
     */
    private static final long serialVersionUID = 1L;
    /** The message given to the commit.*/
    private String lMessage;
    /** The timestamp the commit was created.*/
    private String timeStamp;
    /** The pointer to my parent commit. */
    private String parent;
    /** A mapping from the file name to the file's hashcode. */
    private HashMap<String, String> blobs = new HashMap<String, String>();
    /** My SHA-1 string. */
    private String hash;

    /** Creates an initial commit with LOGMESSAGE and DATE. The parent is
     * empty string, and there are no files stored.*/
    Commit(String logMessage, Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timeStamp = dateFormat.format(date);
        lMessage = logMessage;
        parent = "";
        hash = getHashCode();
    }

    /** Creates a commit object with LOGMESSAGE, the DATE, the list of FILES
     * to be committed from the staging area, and the PARENTCOMMIT.*/
    Commit(String logMessage, Date date, List<File> files,
            Commit parentCommit) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timeStamp = dateFormat.format(date);
        lMessage = logMessage;
        parent = parentCommit.getHash();
        hash = getHashCode();
        File toBeRemoved = new File(".gitlet/to_be_removed");
        String[] namesOfRmFiles = toBeRemoved.list();
        if (namesOfRmFiles.length == 0) {
            HashMap<String, String> parentBlob = parentCommit.getBlobs();
            blobs.putAll(parentBlob);
        } else {
            HashMap<String, String> parentBlob2 = parentCommit.getBlobs();
            blobs.putAll(parentBlob2);
            for (String rmFile : namesOfRmFiles) {
                if (blobs.containsKey(rmFile)) {
                    blobs.remove(rmFile);
                }
            }
        }
        for (File f : files) {
            String hashCode = Utils.sha1(Utils.readContents(f));
            blobs.put(f.getName(), hashCode);
        }
        deleteFilesInRemovedArea();
    }

    /** Returns the information displayed by the log command, shown in the
     * following format:
     *  ===
     *  Commit a0da1ea5a15ab613bf9961fd86f010cf74c7ee48
     *  2015-03-14 11:59:26
     *  A commit message.
     */
    public String toString() {
        String result = "=== " + '\n';
        result += "Commit " + this.getHash() + ' ' + '\n';
        result += this.getTimeStamp() + ' ' + '\n';
        result += this.getLogMessage() + ' ' + '\n';
        return result;
    }

    /** Return my time stamp. */
    public String getTimeStamp() {
        return timeStamp;
    }

    /** Return my log message. */
    public String getLogMessage() {
        return lMessage;
    }

    /** Returns my SHA-1 hashCode. */
    public String getHash() {
        return hash;
    }

    /** Returns my parent reference. */
    public String getParent() {
        return parent;
    }

    /** Returns my references to files. */
    public HashMap<String, String> getBlobs() {
        return this.blobs;
    }

    /** Calculates and returns the SHA-1 hashCode using the log message,
     *  time stamp, parent, and files. */
    public String getHashCode() {
        ArrayList<Object> vals = new ArrayList<Object>();

        vals.add(lMessage);
        vals.add(timeStamp);
        vals.add(parent);

        for (String blobHash : blobs.values()) {
            vals.add(blobHash);
        }
        return Utils.sha1(vals);
    }
    /** Delete the files in our to_be_removed area. */
    private void deleteFilesInRemovedArea() {
        File removedArea = new File(".gitlet/to_be_removed");
        for (File f : removedArea.listFiles()) {
            f.delete();
        }
    }
}
