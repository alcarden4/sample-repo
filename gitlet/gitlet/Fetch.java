package gitlet;
/** Extra credit file for fetch remote command. */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**Brings down commits from the remote gitlet into the local gitlet.
 * Basically, this copies all commits and blobs from the given branch in the
 * remote repository (that are not already in the current repository) into a
 * branch named [remote name]/[remote branch name] in the local .gitlet
 * (just as in real git), changing [remote name]/[remote branch name] to point
 * to the head commit (thus copying the contents of the branch from the remote
 * repository to the current one). This branch is created in the local
 * repository if it did not previously exist.
 * @author Anna Cardenas
 *  */
public class Fetch implements Command {
    /** Remote name for remote we are adding. */
    private String remoteName;
    /** Name of remote branch that remoteName is connected to. */
    private String remoteBranch;
    /** Constructor for fetch command that accesses the remote name RNAME and
     *  the remote branch RBRANCH. */
    Fetch(String rName, String rBranch) {
        remoteName = rName;
        remoteBranch = rBranch;
    }
    @Override
    public void invoke() {
        RoadMap localRM = deserializeRoadMap();
        HashMap<String, String> localsRemotes = localRM.getRemotes();
        if (!localsRemotes.containsKey(remoteName)) {
            System.out.println("did not have this remote name");
            return;
        } else {
            String pathToRemoteDir = localsRemotes.get(remoteName);
            File remoteDirectory = new File(pathToRemoteDir);
            if (!remoteDirectory.exists()) {
                System.out.println("Remote directory not found.");
                return;
            } else {
                RoadMap remoteRM = deserializeRoadMap(pathToRemoteDir);
                HashMap<String, String> remoteBranches = remoteRM.getBranches();
                if (!remoteBranches.containsKey(remoteBranch)) {
                    System.out.println(" That remote does not have "
                            + "that branch.");
                    return;
                }
                String rmBranchHeadCommit = remoteBranches.get(remoteBranch);
                HashMap<String, String> localBranches = localRM.getBranches();
                localBranches.put(remoteName + "/" + remoteBranch,
                        rmBranchHeadCommit);
                Commit remoteCommit = deserializeRemoteCommit
                        (rmBranchHeadCommit, pathToRemoteDir);
                while (!remoteCommit.getParent().equals("")) {
                    addToLocalFiles(remoteCommit, pathToRemoteDir);
                    addToLocalCommits(remoteCommit.getHash(), remoteCommit);
                    remoteCommit = deserializeRemoteCommit(
                            remoteCommit.getParent(), pathToRemoteDir);
                }
                addToLocalFiles(remoteCommit, pathToRemoteDir);
                addToLocalCommits(remoteCommit.getHash(), remoteCommit);
                reserializeRoadMap(localRM);
            }
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
    /** Adds the actual COMMIT files (blobs) from our REMOTEDIR to the
     *  all_files directory in our local directory. */
    private void addToLocalFiles(Commit commit, String remoteDir) {
        HashMap<String, String> commitBlobs = commit.getBlobs();
        for (String fileHash : commitBlobs.values()) {
            File file = new File(remoteDir + "/all_files/" + fileHash);
            byte[] contents = Utils.readContents(file);
            File remoteFile = new File(".gitlet/all_files/" + fileHash);
            Utils.writeContents(remoteFile, contents);
        }
    }
    /** Adds to the local commits directory using the remote's COMMITHASH
     *  and the COMMIT. */
    private void addToLocalCommits(String commitHash, Commit commit) {
        File remoteCommits = new File(".gitlet/commits/" + commitHash);
        try {
            ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream(remoteCommits));
            out.writeObject(commit);
            out.close();
        } catch (IOException excp) {
            System.out.println(excp.getMessage());
        }
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
    /** Deserialize the roadMap with PATHNAME and RETURNS the roadmap. */
    private RoadMap deserializeRoadMap(String pathName) {
        RoadMap roadMap;
        File inFile = new File(pathName + "/roadMap.ser");
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
    /** RETURN a deserialized commit object from REMOTEDIR using the HASH. */
    private Commit deserializeRemoteCommit(String hash, String remoteDir) {
        Commit commit;
        File inFile2 = new File(remoteDir + "/commits/" + hash);
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
