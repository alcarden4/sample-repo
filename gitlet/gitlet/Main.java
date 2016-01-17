package gitlet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Driver class for Gitlet, the tiny stupid version-control system.
 *
 * @author Anna and Joy
 */

public class Main {
    /** Our args enetered into command line. */
    private static String[] argsList;
    /** Command values possible for gitlet. */
    private static final String[] COMMAND_VALUES = new String[] { "init", "add",
        "commit", "rm", "log", "global-log", "find", "status", "checkout",
        "branch", "rm-branch", "reset", "merge", "add-remote", "rm-remote",
        "push", "fetch", "pull"};
    /** List of possible commands for our program. */
    private static final Set<String> COMMANDS = new HashSet<String>(
            Arrays.asList(COMMAND_VALUES));

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND> ....
     */
    public static void main(String... args) {
        argsList = args;
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        if (!COMMANDS.contains(args[0])) {
            System.out.println("No command with that name exists.");
            return;
        }
        checkInit();
        checkAdd();
        checkCommit();
        checkRm();
        checkLog();
        checkGlobalLog();
        checkFind();
        checkStatus();
        checkCheckOut();
        checkBranch();
        checkRmBranch();
        checkReset();
        checkMerge();
        checkAddRemote();
        checkRmRemote();
        checkPush();
        checkFetch();
        checkPull();
    }

    /** Checks the init command. */
    private static void checkInit() {
        if (argsList[0].equals("init")) {
            if (argsList.length == 1) {
                Command init = new Init();
                init.invoke();
                return;
            } else {
                System.out.println("Incorrect operands.");
                return;
            }
        }
    }

    /** Checks the add command. */
    private static void checkAdd() {
        if (argsList[0].equals("add")) {
            if (argsList.length == 2) {
                Command add = new Add(argsList[1]);
                add.invoke();
                return;
            } else {
                System.out.println("Incorrect operands.");
                return;
            }
        }
    }

    /** Checks the commit command. */
    private static void checkCommit() {
        if (argsList[0].equals("commit")) {
            if (argsList.length == 2) {
                Command commit = new CommitCommand(argsList[1]);
                commit.invoke();
                return;
            } else {
                System.out.println("Please enter a commit message.");
                return;
            }
        }
    }

    /** Checks rm command. */
    private static void checkRm() {
        if (argsList[0].equals("rm")) {
            if (argsList.length == 2) {
                Command rm = new Rm(argsList[1]);
                rm.invoke();
                return;
            } else {
                System.out.println("Please enter a commit message.");
                return;
            }
        }
    }

    /** Checks log command. */
    private static void checkLog() {
        if (argsList[0].equals("log")) {
            if (argsList.length == 1) {
                Command log = new Log();
                log.invoke();
                return;
            } else {
                System.out.println("Incorrect operands.");
                return;
            }
        }
    }

    /** Checks global-log command. */
    private static void checkGlobalLog() {
        if (argsList[0].equals("global-log")) {
            if (argsList.length == 1) {
                Command globalLog = new GlobalLog();
                globalLog.invoke();
                return;
            } else {
                System.out.println("Incorrect operands.");
                return;
            }
        }
    }

    /** Checks find command. */
    private static void checkFind() {
        if (argsList[0].equals("find")) {
            if (argsList.length == 2) {
                Command find = new Find(argsList[1]);
                find.invoke();
                return;
            } else {
                System.out.println("Incorrect operands.");
                return;
            }
        }
    }

    /** Checks status command. */
    private static void checkStatus() {
        if (argsList[0].equals("status")) {
            if (argsList.length == 1) {
                Command status = new Status();
                status.invoke();
                return;
            } else {
                System.out.println("Incorrect operands.");
                return;
            }
        }
    }

    /** Check checkout command. In order written:
     *  java gitlet.Main checkout -- [file name]
     *  java gitlet.Main checkout [commit id] -- [file name]
     *  java gitlet.Main checkout [branch name]  */
    private static void checkCheckOut() {
        if (argsList[0].equals("checkout")) {
            if (argsList.length == 3) {
                if (argsList[1].equals("--")) {
                    Command checkout = new Checkout(argsList[2]);
                    checkout.invoke();
                    return;
                } else {
                    System.out.println("Incorrect operands.");
                    return;
                }
            } else if (argsList.length == 4) {
                if (argsList[2].equals("--")) {
                    Checkout checkout = new Checkout(argsList[1], argsList[3]);
                    checkout.invoke2();
                    return;
                } else {
                    System.out.println("Incorrect operands.");
                    return;
                }
            } else if (argsList.length == 2) {
                Checkout checkout = new Checkout(argsList[1]);
                checkout.invoke3();
                return;
            } else {
                System.out.println("Incorrect operands.");
                return;
            }
        }
    }

    /** Check branch command. */
    private static void checkBranch() {
        if (argsList[0].equals("branch")) {
            if (argsList.length == 2) {
                Command branch = new Branch(argsList[1]);
                branch.invoke();
                return;
            } else {
                System.out.println("Incorrect operands.");
                return;
            }
        }
    }

    /** Check rm-branch command. */
    private static void checkRmBranch() {
        if (argsList[0].equals("rm-branch")) {
            if (argsList.length == 2) {
                Command rmBranch = new RmBranch(argsList[1]);
                rmBranch.invoke();
                return;
            } else {
                System.out.println("Incorrect operands.");
                return;
            }
        }
    }

    /** Check reset command. */
    private static void checkReset() {
        if (argsList[0].equals("reset")) {
            if (argsList.length == 2) {
                Command reset = new Reset(argsList[1]);
                reset.invoke();
                return;
            } else {
                System.out.println("Incorrect operands.");
                return;
            }
        }
    }

    /** Check merge command. */
    private static void checkMerge() {
        if (argsList[0].equals("merge")) {
            if (argsList.length == 2) {
                Command merge = new Merge(argsList[1]);
                merge.invoke();
                return;
            } else {
                System.out.println("Incorrect operands.");
                return;
            }
        }
    }
    /** EXTRA CREDIT COMMANDS. */
    /** Check add-remote command. */
    private static void checkAddRemote() {
        if (argsList[0].equals("add-remote")) {
            if (argsList.length == 3) {
                Command addRemote = new AddRemote(argsList[1], argsList[2]);
                addRemote.invoke();
                return;
            } else {
                System.out.println("Incorrect operands.");
                return;
            }
        }
    }

    /** Check rm-remote command. */
    private static void checkRmRemote() {
        if (argsList[0].equals("rm-remote")) {
            if (argsList.length == 2) {
                Command rmRemote = new RmRemote(argsList[1]);
                rmRemote.invoke();
                return;
            } else {
                System.out.println("Incorrect operands.");
                return;
            }
        }
    }

    /** Check push command. */
    private static void checkPush() {
        if (argsList[0].equals("push")) {
            if (argsList.length == 3) {
                Command push = new Push(argsList[1], argsList[2]);
                push.invoke();
                return;
            } else {
                System.out.println("Incorrect operands.");
                return;
            }
        }
    }

    /** Check fetch command. */
    private static void checkFetch() {
        if (argsList[0].equals("fetch")) {
            if (argsList.length == 3) {
                Command push = new Fetch(argsList[1], argsList[2]);
                push.invoke();
                return;
            } else {
                System.out.println("Incorrect operands.");
                return;
            }
        }
    }

    /** Check pull command. */
    private static void checkPull() {
        if (argsList[0].equals("pull")) {
            if (argsList.length == 3) {
                Command push = new Pull(argsList[1], argsList[2]);
                push.invoke();
                return;
            } else {
                System.out.println("Incorrect operands.");
                return;
            }
        }
    }
}
