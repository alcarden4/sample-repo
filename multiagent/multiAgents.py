# multiAgents.py
# --------------
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


from util import manhattanDistance
from game import Directions
import random, util


from game import Agent

class ReflexAgent(Agent):
    """
      A reflex agent chooses an action at each choice point by examining
      its alternatives via a state evaluation function.

      The code below is provided as a guide.  You are welcome to change
      it in any way you see fit, so long as you don't touch our method
      headers.
    """


    def getAction(self, gameState):
        """
        You do not need to change this method, but you're welcome to.

        getAction chooses among the best options according to the evaluation function.

        Just like in the previous project, getAction takes a GameState and returns
        some Directions.X for some X in the set {North, South, West, East, Stop}
        """
        # Collect legal moves and successor states
        legalMoves = gameState.getLegalActions()

        # Choose one of the best actions
        scores = [self.evaluationFunction(gameState, action) for action in legalMoves]
        bestScore = max(scores)
        bestIndices = [index for index in range(len(scores)) if scores[index] == bestScore]
        chosenIndex = random.choice(bestIndices) # Pick randomly among the best

        "Add more of your code here if you want to"

        return legalMoves[chosenIndex]

    def evaluationFunction(self, currentGameState, action):
        """
        Design a better evaluation function here.

        The evaluation function takes in the current and proposed successor
        GameStates (pacman.py) and returns a number, where higher numbers are better.

        The code below extracts some useful information from the state, like the
        remaining food (newFood) and Pacman position after moving (newPos).
        newScaredTimes holds the number of moves that each ghost will remain
        scared because of Pacman having eaten a power pellet.

        Print out these variables to see what you're getting, then combine them
        to create a masterful evaluation function.
        """
        # Useful information you can extract from a GameState (pacman.py)
        successorGameState = currentGameState.generatePacmanSuccessor(action)
        newPos = successorGameState.getPacmanPosition()
        newFood = successorGameState.getFood()
        newGhostStates = successorGameState.getGhostStates()
        newScaredTimes = [ghostState.scaredTimer for ghostState in newGhostStates]
        ghostPosition = successorGameState.getGhostPositions()

        "*** YOUR CODE HERE ***"

        scoreKeep = list()
        score = 0
        foodAsList = currentGameState.getFood().asList()
        posAsLst = list(newPos)
        x = posAsLst[0]
        y = posAsLst[1]

        # similar to manhattan distance for food pellets
        for food in foodAsList:
            x1 = -1 * abs(food[0] - x)
            y1 = -1 * abs(food[1] - y)
            scoreKeep.append((x1+y1))

        if newScaredTimes[0] is not 0:
            score += 70
        else:
            score += isNear(x, y, ghostPosition)

        return successorGameState.getScore() + score + max(scoreKeep)


def isNear(posX, posY, ghostStates):
    """If pacman is near a ghost, return a negative number. ghostStates must be
       be in the form from getGhostPositions() in pacman.py -> [(x,y)]
       Return 0 otherwise.
    """

    ghostLst = list(ghostStates[0])
    gX = ghostLst[0]
    gY = ghostLst[1]
    distance = manhattanDistance(ghostLst, [posX, posY])

    if distance < 4:
        return -40
    else:
        return 0


def scoreEvaluationFunction(currentGameState):
    """
      This default evaluation function just returns the score of the state.
      The score is the same one displayed in the Pacman GUI.

      This evaluation function is meant for use with adversarial search agents
      (not reflex agents).
    """
    return currentGameState.getScore()

class MultiAgentSearchAgent(Agent):
    """
      This class provides some common elements to all of your
      multi-agent searchers.  Any methods defined here will be available
      to the MinimaxPacmanAgent, AlphaBetaPacmanAgent & ExpectimaxPacmanAgent.

      You *do not* need to make any changes here, but you can if you want to
      add functionality to all your adversarial search agents.  Please do not
      remove anything, however.

      Note: this is an abstract class: one that should not be instantiated.  It's
      only partially specified, and designed to be extended.  Agent (game.py)
      is another abstract class.
    """

    def __init__(self, evalFn = 'scoreEvaluationFunction', depth = '2'):
        self.index = 0 # Pacman is always agent index 0
        self.evaluationFunction = util.lookup(evalFn, globals())
        self.depth = int(depth)
        self.alpha = -float("inf")
        self.beta = float("inf")
        self.pacManState = 0


class MinimaxAgent(MultiAgentSearchAgent):
    """
      Your minimax agent (question 2)
    """

    def getAction(self, gameState):
        """
          Returns the minimax action from the current gameState using self.depth
          and self.evaluationFunction.

          Here are some method calls that might be useful when implementing minimax.

          gameState.getLegalActions(agentIndex):
            Returns a list of legal actions for an agent
            agentIndex=0 means Pacman, ghosts are >= 1

          gameState.generateSuccessor(agentIndex, action):
            Returns the successor game state after an agent takes an action

          gameState.getNumAgents():
            Returns the total number of agents in the game

          gameState.isWin():
            Returns whether or not the game state is a winning state

          gameState.isLose():
            Returns whether or not the game state is a losing state

        The evaluation function should be called not only when the maximum
        ply depth is reached, but also when the game state is a winning state
        or a losing state.
        """
        "*** YOUR CODE HERE ***"

        move = self.maxValue(gameState,self.depth)[1]

        return move

    def maxValue(self, gameState, depth):
        if depth == 0 or gameState.isLose() or gameState.isWin():
            return self.evaluationFunction(gameState)

        possibleActions = gameState.getLegalActions()
        values = list()
        for action in possibleActions:
            values.append(self.minValue(gameState.generateSuccessor(self.index, action), depth, 1))
        maxValue = max(values)
        indexForValue = 0 #default
        for i in range(len(values)):
            if values[i] == maxValue:
                indexForValue = i

        return maxValue, possibleActions[indexForValue]

    def minValue(self, gameState, depth, agent):
        if depth == 0 or gameState.isLose() or gameState.isWin():
            return self.evaluationFunction(gameState)
        values = list()
        possibleActions = gameState.getLegalActions(agent)

        if agent != gameState.getNumAgents() - 1:
            for action in possibleActions:
                values.append(self.minValue(gameState.generateSuccessor(agent, action), depth, agent + 1))

        else:
            for action in possibleActions:
                values.append(self.maxValue(gameState.generateSuccessor(agent, action), depth - 1))
        minValue = min(values)
        indexForValue = 0 #default
        for i in range(len(values)):
            if values[i] == minValue:
                indexForValue = i
        return minValue, possibleActions[indexForValue]


class AlphaBetaAgent(MultiAgentSearchAgent):
    """
      Your minimax agent with alpha-beta pruning (question 3)
    """

    def getAction(self, gameState):
        """
          Returns the minimax action using self.depth and self.evaluationFunction
        """
        "*** YOUR CODE HERE ***"

        return self.maxValue(gameState, 0, self.alpha, self.beta)[1]


    def maxValue(self, gameState, depth, alpha, beta):
        if depth == self.depth:
            return (self.evaluationFunction(gameState), "noState")

        possibleActions = gameState.getLegalActions(self.index)
        score = self.alpha
        chosenAction = ""

        if len(possibleActions) == 0:
            return (self.evaluationFunction(gameState), "noState")

        for action in possibleActions:
            if (alpha > beta):
                return (score, action)
            updatedScore = self.minValue(gameState.generateSuccessor(self.index, action), 1, depth, alpha, beta)[0]
            if (updatedScore > score):
                score = updatedScore
                chosenAction = action

            if (updatedScore > alpha):
                alpha = updatedScore
        return (score, chosenAction)



    def minValue(self, gameState, agent, depth, alpha, beta):
        possibleActions = gameState.getLegalActions(agent)
        chosenAction = ""
        score = self.beta

        if len(possibleActions) == 0:
            return (self.evaluationFunction(gameState), "")

        for action in possibleActions:
            if (alpha > beta):
                return (score, "")


            if (agent == gameState.getNumAgents() - 1):
                updatedScore = self.maxValue(gameState.generateSuccessor(agent, action), depth + 1, alpha, beta)[0]
            else:
                updatedScore = self.minValue(gameState.generateSuccessor(agent, action), agent + 1, depth, alpha, beta)[0]

            if (updatedScore < score):
                score = updatedScore
                chosenAction = action
            if (updatedScore < beta):
                beta = updatedScore
        return (score, chosenAction)




class ExpectimaxAgent(MultiAgentSearchAgent):
    """
      Your expectimax agent (question 4)
    """

    def getAction(self, gameState):
        """
          Returns the minimax action from the current gameState using self.depth
          and self.evaluationFunction.
          Here are some method calls that might be useful when implementing minimax.
          gameState.getLegalActions(agentIndex):
            Returns a list of legal actions for an agent
            agentIndex=0 means Pacman, ghosts are >= 1
          Directions.STOP:
            The stop direction, which is always legal
          gameState.generateSuccessor(agentIndex, action):
            Returns the successor game state after an agent takes an action
          gameState.getNumAgents():
            Returns the total number of agents in the game
        """
        "*** YOUR CODE HERE ***"
        action = self.calcActionVal(gameState, self.index, 0)
        return action[0]

    def maxValue(self, gameState, agent, depth):
        value = ("", self.alpha)

        if len(gameState.getLegalActions(agent)) == 0:
            return self.evaluationFunction(gameState)

        for action in gameState.getLegalActions(agent):
            if action == "Stop":
                continue

            updatedVal = self.calcActionVal(gameState.generateSuccessor(agent, action), agent + 1, depth)
            if isinstance(updatedVal, tuple):
                updatedVal = updatedVal[1]

            maxVal = max(value[1], updatedVal)

            if maxVal != value[1]:
                value = (action, maxVal)

        return value

    def expectimaxVal(self, gameState, agent, depth):
        value = ["", self.index]

        if len(gameState.getLegalActions(agent)) == 0:
            return self.evaluationFunction(gameState)

        expectimax_prob = 1.0/len(gameState.getLegalActions(agent))

        for action in gameState.getLegalActions(agent):
            if action == "Stop":
                continue

            retVal = self.calcActionVal(gameState.generateSuccessor(agent, action), agent + 1, depth)
            if isinstance(retVal, tuple):
                retVal = retVal[1]

            value[1] += retVal * expectimax_prob
            value[0] = action

        return tuple(value)

    def calcActionVal(self, gameState, agent, depth):
        if agent >= gameState.getNumAgents():

            depth += 1
            agent = self.index

        if depth == self.depth:
            return self.evaluationFunction(gameState)

        if agent == 0: #pacman index
            return self.maxValue(gameState, agent, depth)
        else:
            return self.expectimaxVal(gameState, agent, depth)



def betterEvaluationFunction(currentGameState):
    """
    Your extreme ghost-hunting, pellet-nabbing, food-gobbling, unstoppable
    evaluation function (question 5).
    DESCRIPTION: Evaluation is based on the
    -> Distance to the nearest food
    -> Closest Ghost
    -> The current score
    -> How many capsules are on the board
    -> How many scared ghosts there are
    The goal is to try to increase the score while getting closer to other food pellets,
    staying away from the closestGhost (if possible) unless
    are scared, getting rid of capsules, and eating
    scaredGhosts whenever possible
    """
    "*** YOUR CODE HERE ***"
    gStateFood = currentGameState.getFood().asList()

    availCapsules = currentGameState.getCapsules()
    pacPosition = currentGameState.getPacmanPosition()
    ghostPositions = currentGameState.getGhostPositions()
    ghostStates = currentGameState.getGhostStates()
    #ghostStates.scaretTimer > 0 if they are scared

    pacPosLst = list(pacPosition)

    capsuleLst = list(availCapsules)

    foodDist = list()

    capsDist = list()
    ghostDist = list()
    scaredGhosts = 0

    for f in gStateFood:
        x = abs(f[0] - pacPosLst[0])
        y = abs(f[1] - pacPosLst[1])
        foodDist.append(-1 * (x + y))
    if len(foodDist) == 0:
        foodDist.append(0)

    for g in ghostStates:
        if g.scaredTimer >= 0:
            scaredGhosts += 1
            ghostDist.append(1)
            continue
        ghost_pos = g.getPosition()
        x = abs(ghost_pos[0] - pacPosLst[0])
        y = abs(ghost_pos[1] - pacPosLst[1])
        if x + y == 0:
            ghostDist.append(0)

        else:
            ghostDist.append(-1 * x)

    foodVal = max(foodDist)
    closestGhost = min(ghostDist)
    current_score = currentGameState.getScore()
    numCapsules = len(capsuleLst)
    curGhostsnum = len(ghostStates)

    score = foodVal + closestGhost + current_score - 80 * numCapsules - 50 * curGhostsnum - scaredGhosts


    return score


# Abbreviation
better = betterEvaluationFunction