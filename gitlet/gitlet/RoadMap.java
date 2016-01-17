package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;


/** Representation of the Commit Tree.
 * @author Joy Gu. */


public class RoadMap implements Serializable {
    /** HashMap of remotes added through our remote add command. Map of remote's
     *  name to its remote directory's name:[name of remote directory]/.gitlet
     *  */
    private HashMap<String, String> remotes;
    /** Head Pointer represented as the hashcode string for the most
     *  recent commit. */
    private String headPointer;

    /** Pointer to the name of the current branch. */
    private String branchPointer;

    /** Map of branch name to the commit hashcode of the most recent
     * for that branch. */
    private HashMap<String, String> branches;
    /** Constructor creating a roadmap from the INITIALCOMMIT.  */
    RoadMap(String initialCommit) {
        remotes = new HashMap<String, String>();
        headPointer = initialCommit;
        branches = new HashMap<String, String>();
        branches.put("master", initialCommit);
        branchPointer = "master";
    }

    /** Returns my the remote directories I currently have. */
    public HashMap<String, String> getRemotes() {
        return remotes;
    }
    /** Return the current commit (as a hash, which is the file
     * name in the .gitlet/commits directory. */
    public String getHeadPointer() {
        return headPointer;
    }
    /** Return the branch I am currently on. */
    public String getCurrentBranch() {
        return branchPointer;
    }
    /** Updates the headPointer to the hash of NEWCOMMIT.*/
    public void setHeadPointer(Commit newCommit) {
        headPointer = newCommit.getHash();
    }
    /** Updates the branchPointer to the BRANCHNAME.*/
    public void setBranchPointer(String branchName) {
        branchPointer = branchName;
    }
    /** Updates the branches hashMap. Replaces the value of
     * the current branch's mapping to NEWCOMMIT. */
    public void updateBranches(Commit newCommit) {
        branches.put(branchPointer, newCommit.getHash());
    }
    /** Returns my representation of my branches. */
    public HashMap<String, String> getBranches() {
        return branches;
    }
    /** Serializes this roadmap, R, back into a file.  */
    public void serialize(RoadMap r) {
        File outFile = new File(".gitlet/roadMap.ser");
        try {
            ObjectOutputStream out =
                new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(r);
            out.close();
        } catch (IOException excp) {
            excp.getMessage();
        }
    }
    /** Desirialize this roadmap, R. */
    public void desirialize(RoadMap r) {
        File inFile = new File(".gitlet/roadMap.ser");
        try {
            ObjectInputStream inp =
                new ObjectInputStream(new FileInputStream(inFile));
            r = (RoadMap) inp.readObject();
            inp.close();
        } catch (IOException | ClassNotFoundException excp) {
            r = null;
        }
        if (r == null) {
            System.out.println("road map was null somehow?");
            return;
        }
    }
    /** Desirializes the specified commit according to the hashcode,
     *  PARENTHASH. */
    public void desirializeCommit(String parentHash) {
        Commit parentCommit;
        File inFile = new File(".gitlet/commits/" + parentHash);
        try {
            ObjectInputStream inp =
                new ObjectInputStream(new FileInputStream(inFile));
            parentCommit = (Commit) inp.readObject();
            inp.close();
        } catch (IOException | ClassNotFoundException excp) {
            parentCommit = null;
        }
        if (parentCommit == null) {
            System.out.println("parent commit was null somehow?");
            return;
        }
    }
}
