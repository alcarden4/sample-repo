# search.py
# ---------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


"""
In search.py, you will implement generic search algorithms which are called by
Pacman agents (in searchAgents.py).
"""

import util

class SearchProblem:
    """
    This class outlines the structure of a search problem, but doesn't implement
    any of the methods (in object-oriented terminology: an abstract class).

    You do not need to change anything in this class, ever.
    """

    def getStartState(self):
        """
        Returns the start state for the search problem.
        """
        util.raiseNotDefined()

    def isGoalState(self, state):
        """
          state: Search state

        Returns True if and only if the state is a valid goal state.
        """
        util.raiseNotDefined()

    def getSuccessors(self, state):
        """
          state: Search state

        For a given state, this should return a list of triples, (successor,
        action, stepCost), where 'successor' is a successor to the current
        state, 'action' is the action required to get there, and 'stepCost' is
        the incremental cost of expanding to that successor.
        """
        util.raiseNotDefined()

    def getCostOfActions(self, actions):
        """
         actions: A list of actions to take

        This method returns the total cost of a particular sequence of actions.
        The sequence must be composed of legal moves.
        """
        util.raiseNotDefined()


def tinyMazeSearch(problem):
    """
    Returns a sequence of moves that solves tinyMaze.  For any other maze, the
    sequence of moves will be incorrect, so only use this for tinyMaze.
    """
    from game import Directions
    s = Directions.SOUTH
    w = Directions.WEST
    return  [s, s, w, s, w, w, s, w]




def depthFirstSearch(problem):
    """
    Search the deepest nodes in the search tree first.

    Your search algorithm needs to return a list of actions that reaches the
    goal. Make sure to implement a graph search algorithm.

    To get started, you might want to try some of these simple commands to
    understand the search problem that is being passed in:

    print "Start:", problem.getStartState()
    print "Is the start a goal?", problem.isGoalState(problem.getStartState())
    print "Start's successors:", problem.getSuccessors(problem.getStartState())
    """
    "*** YOUR CODE HERE ***"

    from copy import deepcopy
    seen = list()
    fringe = util.Stack()
    directions = {}
    fringe.push(problem.getStartState())
    actions = list()
    directions[problem.getStartState()] = actions

    while True:
        if fringe.isEmpty():
            return []
        popped_node = fringe.pop()
        if problem.isGoalState(popped_node):
            return directions.get(popped_node)

        if popped_node not in seen:
            seen.append(popped_node)
            for successors in problem.getSuccessors(popped_node):
                oldPath = directions.get(popped_node)
                currentpath = deepcopy(oldPath)
                currentpath.append(successors[1])
                directions[successors[0]] = currentpath
                fringe.push(successors[0])



def breadthFirstSearch(problem):
    """Search the shallowest nodes in the search tree first."""
    "*** YOUR CODE HERE ***"

    seen = list()
    fringe = util.Queue()
    actions = []

    fringe.push((problem.getStartState(), actions))
    while not fringe.isEmpty():
        next_action, previous_actions = fringe.pop()

        if problem.isGoalState(next_action):
            return previous_actions
        if next_action not in seen:
            seen.append(next_action)
            for coord, direction, cost in problem.getSuccessors(next_action):
                fringe.push((coord, previous_actions + [direction]))

    return []



def uniformCostSearch(problem):
    """Search the node of least total cost first."""
    "*** YOUR CODE HERE ***"

    closed = list()
    fringe = util.PriorityQueue()
    directions = {}
    parents = {}
    costs = {}
    beginning = (problem.getStartState(), 0)
    fringe.push(beginning, 0)
    actions = list()
    directions[beginning] = actions
    costs[beginning] = 0


    while True:
        if fringe.isEmpty():
            return list()
        cur_position = fringe.pop()
        if problem.isGoalState(cur_position[0]):
            while parents.has_key(cur_position):
                parent = parents.get(cur_position)[0]
                actions.insert(0, directions[cur_position][0])
                cur_position = parent
            return actions

        if cur_position[0] not in closed:

            closed.append(cur_position[0])
            for successor in problem.getSuccessors(cur_position[0]):
                plist = parents.get(successor)
                if plist:
                    plist.append(cur_position)
                else:
                    plist = []
                    plist.append(cur_position)
                    parents[successor] = plist

                directionSuccessors = directions.get(successor)
                if directionSuccessors:
                    directionSuccessors.append(successor[1])

                directionSuccessors = []
                directionSuccessors.append(successor[1])
                directions[successor] = directionSuccessors
                cost = costs.get(cur_position) + successor[2]
                costs[successor] = cost
                fringe.push(successor, cost)



def nullHeuristic(state, problem=None):
    """
    A heuristic function estimates the cost from the current state to the nearest
    goal in the provided SearchProblem.  This heuristic is trivial.
    """
    return 0

def aStarSearch(problem, heuristic=nullHeuristic):
    """Search the node that has the lowest combined cost and heuristic first."""
    "*** YOUR CODE HERE ***"

    """ Implemented very similarly to UCS"""
    closed = list()
    fringe = util.PriorityQueue()
    directions = {}
    actions = {}
    costs = {}
    beginning_state = (problem.getStartState(), 0)
    fringe.push(beginning_state, heuristic(beginning_state[0], problem))
    path = list()

    directions[beginning_state] = path
    costs[beginning_state] = 0


    while True:
        if fringe.isEmpty():
            return list()
        action = fringe.pop()
        if problem.isGoalState(action[0]):
            while actions.has_key(action):
                parent = actions.get(action)[0]
                path.insert(0, directions[action][0])
                action = parent
            return path

        if action[0] not in closed:

            closed.append(action[0])
            for successor in problem.getSuccessors(action[0]):
                    parentslist = actions.get(successor)
                    if parentslist:
                        parentslist.append(action)
                    else:
                        parentslist = list()
                        parentslist.append(action)
                    actions[successor] = parentslist

                    directionSuccessors = directions.get(successor)
                    if directionSuccessors:
                        directionSuccessors.append(successor[1])
                    else:
                        directionSuccessors = []
                        directionSuccessors.append(successor[1])
                    directions[successor] = directionSuccessors
                    cost = costs.get(action) + successor[2]
                    costs[successor] = cost
                    priority = cost + heuristic(successor[0], problem)
                    fringe.push(successor, priority)


# Abbreviations
bfs = breadthFirstSearch
dfs = depthFirstSearch
astar = aStarSearch
ucs = uniformCostSearch
