package gitlet;
/** Extra credit file for push remote command. */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**Attempts to append the current branch's commits to the end of the given
 * branch at the given remote.
 * @author Anna Cardenas and Joy Gu
 *  */
public class Push implements Command {
    /** Remote name for remote we are adding. */
    private String remoteName;
    /** Name of remote branch that remoteName is connected to. */
    private String remoteBranch;
    /** Constructor for push command that accesses the remote name RNAME and
     *  the remote branch RBRANCH. */
    Push(String rName, String rBranch) {
        remoteName = rName;
        remoteBranch = rBranch;
    }
    @Override
    public void invoke() {
        RoadMap localRM = deserializeRoadMap();
        String localHeadCommit = localRM.getHeadPointer();
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
                    remoteBranches.put(remoteBranch, localHeadCommit);
                    Commit localCommit = deserialize(localHeadCommit);
                    remoteRM.setHeadPointer(localCommit);
                    addToRemoteFiles(deserialize(localHeadCommit),
                            pathToRemoteDir);
                    addToRemoteCommits(pathToRemoteDir, localHeadCommit,
                            localCommit);
                    reserializeRoadMap(remoteRM, pathToRemoteDir);
                    return;
                } else {
                    String rmBranchHeadCommit =
                            remoteBranches.get(remoteBranch);
                    File localCommits = new File(".gitlet/commits");
                    File[] localCommitFiles = localCommits.listFiles();
                    if (containsRemoteCommit(localCommitFiles,
                            rmBranchHeadCommit)) {
                        remoteBranches.put(remoteBranch, localHeadCommit);
                        Commit localCommit = deserialize(localHeadCommit);
                        addToRemoteFiles(localCommit, pathToRemoteDir);
                        addToRemoteCommits(pathToRemoteDir, localHeadCommit,
                                localCommit);
                        remoteRM.setHeadPointer(localCommit);
                        reserializeRoadMap(remoteRM, pathToRemoteDir);
                        return;
                    } else {
                        System.out.println("Please pull down remote changes"
                                + " before pushing.");
                        return;
                    }
                }
            }
        }
    }

    /** Adds the actual COMMIT files (blobs) to the all_files directory in our
     *  remote directory, REMOTEDIR. (should be value from remotes hashmap) */
    private void addToRemoteFiles(Commit commit, String remoteDir) {
        HashMap<String, String> commitBlobs = commit.getBlobs();
        for (String fileHash : commitBlobs.values()) {
            File localFile = new File(".gitlet/all_files/" + fileHash);
            byte[] contents = Utils.readContents(localFile);
            File remoteFile = new File(remoteDir + "/all_files/" + fileHash);
            Utils.writeContents(remoteFile, contents);
        }
    }

    /** Adds to the remote's commits directory within its .gitlet directory.
     *  Takes in REMOTEDIR (pathname) and the COMMITHASH and the COMMIT. */
    private void addToRemoteCommits(String remoteDir, String commitHash,
            Commit commit) {
        File remoteCommits = new File(remoteDir + "/commits/" + commitHash);
        try {
            ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream(remoteCommits));
            out.writeObject(commit);
            out.close();
        } catch (IOException excp) {
            System.out.println(excp.getMessage());
        }
    }

    /** Checks if our local commit history contains the remote branch's head
     *  commit. RETURN true if local history contains the remote branch's
     *  commit using LOCALCOMMITFILES and RMBRANCHHEADCOMMIT. */
    private boolean containsRemoteCommit(File[] localCommitfiles,
            String rmBranchHeadCommit) {
        boolean flag = false;
        for (File f : localCommitfiles) {
            if (f.getName().equals(rmBranchHeadCommit)) {
                flag = true;
                return flag;
            }
        }
        return false;
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
    /** Reserialize our ROADMAP using ROADMAP AND REMOTEDIR. */
    private void reserializeRoadMap(RoadMap roadMap, String remoteDir) {
        File outFile2 = new File(remoteDir + "/roadMap.ser");
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
    /** RETURN a deserialized commit object using the HASH. */
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
