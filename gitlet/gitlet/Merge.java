package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Merges files from the given branch into the current branch.
 * @author Anna Cardenas
 */
public class Merge implements Command {
    /** The name of the branch we want to merge with the current branch. */
    private String givenBranch;
    /** Value of whether of not this Merge results in a conflicted file. */
    private boolean hasConflict = false;
    /** A list of all the examined file names so far. */
    private ArrayList<String> examined = new ArrayList<String>();
    /** The roadMap's branches. */
    private HashMap<String, String> branches;
    /** The current branch. */
    private String currentBranch;
    /** The current branch's commit. */
    private Commit currentCommit;
    /** The files tracked in the current commit. */
    private HashMap<String, String> trackedInCurr;
    /** The commit of the given branch. */
    private Commit givenCommit;
    /** The files tracked in the given commit. */
    private HashMap<String, String> trackedInGiven;

    /** Creates a new Merge command, setting the given branch to BRANCH. */
    Merge(String branch) {
        givenBranch = branch;
        getVariables();
    }
    /** Retrieve the objects we need for the command. */
    private void getVariables() {
        RoadMap roadMap = deserializeRoadMap();
        branches = roadMap.getBranches();
        currentBranch = roadMap.getCurrentBranch();
        currentCommit = deserialize(roadMap.getHeadPointer());
        trackedInCurr = currentCommit.getBlobs();
        givenCommit = deserialize(branches.get(givenBranch));
        if (givenCommit != null) {
            trackedInGiven = givenCommit.getBlobs();
        }
    }
    @Override
    public void invoke() {
        File gitlet = new File(".gitlet");
        if (!gitlet.exists()) {
            System.out.println("Not in an initialized gitlet directory.");
            return;
        }
        RoadMap roadMap = deserializeRoadMap();
        if (givenCommit == null) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        File stagedAreaDirectory = new File(".gitlet/staging_area");
        File removedDirectory = new File(".gitlet/to_be_removed");
        if (stagedAreaDirectory.list().length != 0
                || removedDirectory.list().length != 0) {
            System.out.println("You have uncommitted changes.");
            return;
        }
        File curDir = new File(".");
        for (String f : curDir.list()) {
            if (!trackedInCurr.containsKey(f)) {
                if (trackedInGiven.containsKey(f)) {
                    System.out.println("There is an untracked file in the way;"
                            + " delete it or add it first.");
                    return;
                }
            }
        }
        if (currentBranch.equals(givenBranch)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        String splitPoint = findSplitPoint(currentBranch, givenBranch);
        Commit splitPointCommit = deserialize(splitPoint);
        String currBranchCommitHash = branches.get(currentBranch);
        if (splitEqualsBranch(splitPoint, branches.get(givenBranch))) {
            System.out.println(
                    "Given branch is an ancestor of the" + " current branch.");
            return;
        }
        if (splitIsCurrBranch(splitPoint, currBranchCommitHash)) {
            String givenBranchCommit = branches.get(givenBranch);
            branches.put(currentBranch, givenBranchCommit);
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        checkBulletPoints(splitPointCommit);
        reserializeRoadMap(roadMap);
        if (hasConflict) {
            System.out.println("Encountered a merge conflict.");
            return;
        }
        File stagingArea = new File(".gitlet/staging_area");
        File[] filesInStage = stagingArea.listFiles();
        if (filesInStage.length == 0) {
            System.out.println("No changes added to the commit.");
            return;
        }
        autoCommit();
    }

    /** Checks all of the bullet point cases for the merge command, which
     * require the SPLITPOINTCOMMIT. */
    private void checkBulletPoints(Commit splitPointCommit) {
        checkBulletTwo(splitPointCommit);
        checkBulletThree(splitPointCommit);
        checkBulletFour(splitPointCommit);
        checkBulletFive(splitPointCommit);
        checkBulletSix(splitPointCommit);
        checkBulletSeven(splitPointCommit);
        checkConflictCase(splitPointCommit);
    }

    /** Automatically makes a commit. */
    private void autoCommit() {
        CommitCommand makeCommit = new CommitCommand(
                "Merged " + currentBranch + " with " + givenBranch + ".");
        makeCommit.invoke();
    }

    /** Checks if there are any conflicted files, which requires a comparision
     * to the SPLITPOINTCOMMIT. */
    private void checkConflictCase(Commit splitPointCommit) {
        HashSet<String> currAndGivenFileNames = new HashSet<String>();
        currAndGivenFileNames.addAll(trackedInCurr.keySet());
        currAndGivenFileNames.addAll(trackedInGiven.keySet());
        for (String fileToCheck : currAndGivenFileNames) {
            if (!examined.contains(fileToCheck)) {
                if (isConflicted(fileToCheck, currentCommit, givenCommit,
                        splitPointCommit)) {
                    hasConflict = true;
                    writeConflict(fileToCheck, currentCommit, givenCommit);
                }
            }
        }
    }

    /**
     * Creates a file named FILENAME in the working directory with the following
     * format: <<<<<<< HEAD contents of file in CURRENT ======= contents
     * of file in GIVEN>>>>>>>
     * Overrides the file if it already exists, otherwise creates a new file if
     * it isn't in the working directory.
     */
    private void writeConflict(String fileName, Commit current, Commit given) {
        String head = "<<<<<<< HEAD\n";
        byte[] hbyte = head.getBytes();
        String dashes = "=======\n";
        byte[] dbyte = dashes.getBytes();
        String end = ">>>>>>>\n";
        byte[] ebyte = end.getBytes();

        String file1Hash = current.getBlobs().get(fileName);
        String file2Hash = given.getBlobs().get(fileName);

        String empty = "";
        byte[] f1 = empty.getBytes();
        byte[] f2 = empty.getBytes();

        if (file1Hash == null) {
            File file2 = new File(".gitlet/all_files/" + file2Hash);
            f2 = Utils.readContents(file2);
        } else if (file2Hash == null) {
            File file1 = new File(".gitlet/all_files/" + file1Hash);
            f1 = Utils.readContents(file1);
        } else {
            File file1 = new File(".gitlet/all_files/" + file1Hash);
            f1 = Utils.readContents(file1);
            File file2 = new File(".gitlet/all_files/" + file2Hash);
            f2 = Utils.readContents(file2);
        }

        File destination = new File(fileName);
        int totalLength = hbyte.length + f1.length + dbyte.length + f2.length
                + ebyte.length;
        int start1 = hbyte.length;
        int start2 = hbyte.length + f1.length;
        int start3 = hbyte.length + f1.length + dbyte.length;
        int start4 = hbyte.length + f1.length + dbyte.length + f2.length;

        byte[] newContents = new byte[totalLength];
        System.arraycopy(hbyte, 0, newContents, 0, hbyte.length);
        System.arraycopy(f1, 0, newContents, start1, f1.length);
        System.arraycopy(dbyte, 0, newContents, start2, dbyte.length);
        System.arraycopy(f2, 0, newContents, start3, f2.length);
        System.arraycopy(ebyte, 0, newContents, start4, ebyte.length);

        Utils.writeContents(destination, newContents);
    }

    /**
     * Any files that have been modified in the given branch since
     * SPLITPOINTCOMMIT, but not modified in the current branch since the split
     * point should be changed to their versions in the given branch (checked
     * out from the commit at the front of the given branch). These files should
     * then all be automatically staged. */
    private void checkBulletTwo(Commit splitPointCommit) {
        for (String fileName : trackedInGiven.keySet()) {
            if (!examined.contains(fileName)
                    && isModified(fileName, splitPointCommit, givenCommit)) {
                if (!isModified(fileName, splitPointCommit, currentCommit)) {
                    examined.add(fileName);
                    Checkout updateVersion = new Checkout(givenCommit.getHash(),
                            fileName);
                    updateVersion.invoke2();
                    Add adder = new Add(fileName);
                    adder.invoke();
                }
            }
        }
    }

    /**
     * Any files that have been modified in the current branch but not in the
     * given branch since SPLITPOINTCOMMIT should stay as they are.
     */
    private void checkBulletThree(Commit splitPointCommit) {
        for (String fileName : trackedInCurr.keySet()) {
            if (!examined.contains(fileName)
                    && isModified(fileName, splitPointCommit, currentCommit)) {
                if (!isModified(fileName, splitPointCommit, givenCommit)) {
                    examined.add(fileName);
                }
            }
        }
    }

    /**
     * Any files that were not present at SPLITPOINTCOMMIT and are present only
     * in TRACKEDINCURR of CURRENTCOMMIT should remain as they are; also not
     * present in GIVENCOMMIT.
     */
    private void checkBulletFour(Commit splitPointCommit) {
        for (String fileName : trackedInCurr.keySet()) {
            if (!examined.contains(fileName)
                    && !isPresent(fileName, splitPointCommit)) {
                if (!isPresent(fileName, givenCommit)) {
                    examined.add(fileName);
                }
            }
        }
    }

    /**
     * Any files that were not present at SPLITPOINTCOMMIT and are present only
     * in TRACKEDINGIVEN of GIVENCOMMIT should be checked out and staged; also
     * not present in CURRENTCOMMIT.
     */
    private void checkBulletFive(Commit splitPointCommit) {
        for (String fileName : trackedInGiven.keySet()) {
            if (!examined.contains(fileName)
                    && !isPresent(fileName, splitPointCommit)) {
                if (!isPresent(fileName, currentCommit)) {
                    examined.add(fileName);
                    Checkout updateVersion = new Checkout(givenCommit.getHash(),
                            fileName);
                    updateVersion.invoke2();
                    Add adder = new Add(fileName);
                    adder.invoke();
                }
            }
        }
    }

    /**
     * Any files present at the SPLITPOINTCOMMIT, unmodified in the
     * CURRENTCOMMIT,and absent in GIVENCOMMIT should be removed (and
     * untracked).
     */
    private void checkBulletSix(Commit splitPointCommit) {
        for (String splitFileName : splitPointCommit.getBlobs().keySet()) {
            if (!examined.contains(splitFileName) && !isModified(splitFileName,
                    splitPointCommit, currentCommit)) {
                if (!trackedInGiven.containsKey(splitFileName)) {
                    examined.add(splitFileName);
                    Rm removed = new Rm(splitFileName);
                    removed.invoke();
                }
            }
        }
    }

    /**
     * Any files present at the SPLITPOINTCOMMIT, unmodified in the GIVENCOMMIT,
     * and absent in the CURRENTCOMMIT should remain absent.
     */
    private void checkBulletSeven(Commit splitPointCommit) {
        for (String splitFileName : splitPointCommit.getBlobs().keySet()) {
            if (!examined.contains(splitFileName) && !isModified(splitFileName,
                    splitPointCommit, givenCommit)) {
                if (!trackedInCurr.containsKey(splitFileName)) {
                    examined.add(splitFileName);
                }
            }
        }
    }

    /**
     * Returns true if FILENAME is in conflict, meaning it is "modified in
     * different ways" in the CURRENT and GIVEN. Modified in different
     * ways means contents of both are changed and different from other, or the
     * contents of one are changed and the other is deleted, or the file was
     * absent at the SPLITPOINT and have different contents in the given and
     * current branches.
     */
    private boolean isConflicted(String fileName, Commit current, Commit given,
            Commit splitPoint) {
        String currHash = current.getBlobs().get(fileName);
        String givenHash = given.getBlobs().get(fileName);
        String splitPointHash = splitPoint.getBlobs().get(fileName);

        if (splitPointHash == null) {
            return isModified(fileName, current, given);
        } else {
            if (givenHash == null
                    && isModified(fileName, current, splitPoint)) {
                return true;
            }
            if (currHash == null && isModified(fileName, given, splitPoint)) {
                return true;
            }
            if (currHash != null && givenHash != null) {
                return !currHash.equals(givenHash);
            }
            return false;
        }
    }

    /** Returns true if FILENAME is present(tracked) in COMMIT. */
    private boolean isPresent(String fileName, Commit commit) {
        HashMap<String, String> blobs = commit.getBlobs();
        return blobs.containsKey(fileName);
    }

    /**
     * Returns true if FILENAME is modified in C1 and C2, false otherwise.
     * Modification means the contents are changed or one may be absent
     */
    private boolean isModified(String fileName, Commit c1, Commit c2) {
        String version1 = c1.getBlobs().get(fileName);
        String version2 = c2.getBlobs().get(fileName);

        if (version1 == null && version2 == null) {
            return false;
        }
        if (version1 == null && version2 != null) {
            return true;
        }
        if (version1 != null && version2 == null) {
            return true;
        }
        return !version1.equals(version2);

    }

    /**
     * Returns true if the SPLITPOINT is the same as the given branch's head
     * commit, BRANCHCOMMIT.
     */
    private boolean splitEqualsBranch(String splitPoint, String branchCommit) {
        if (splitPoint.equals(branchCommit)) {
            return true;
        }
        return false;
    }

    /**
     * Returns true if the SPLITPOINT is equal to CURRBRANCH.
     */
    private boolean splitIsCurrBranch(String splitPoint, String currBranch) {
        if (splitPoint.equals(currBranch)) {
            return true;
        }
        return false;

    }

    /** Returns the hash of the split point between BRANCH1 and BRANCH2, using
     * the ROADMAP provided. Returns the intiial commit if there is no split. */
    private String findSplitPoint(String branch1, String branch2) {
        String b1 = branches.get(branch1);
        String b2 = branches.get(branch2);

        Commit commit1 = deserialize(b1);
        Commit commit2 = deserialize(b2);

        ArrayList<String> history = new ArrayList<String>();
        while (commit1 != null) {
            history.add(commit1.getHash());
            commit1 = deserialize(commit1.getParent());
        }

        while (commit2 != null) {
            String hash = commit2.getHash();
            if (history.contains(hash)) {
                return hash;
            }
            commit2 = deserialize(commit2.getParent());
        }
        return history.get(history.size() - 1);
    }

    /** Deserialize and return the commit corresponding to HASH. */
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

    /** Deserialize and return the roadMap. */
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
        return roadMap;
    }

    /** Reserialize the object corresponding to ROADMAP. */
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
