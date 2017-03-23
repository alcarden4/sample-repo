# valueIterationAgents.py
# -----------------------
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



import mdp, util

from learningAgents import ValueEstimationAgent
import collections


class ValueIterationAgent(ValueEstimationAgent):
    """
        * Please read learningAgents.py before reading this.*

        A ValueIterationAgent takes a Markov decision process
        (see mdp.py) on initialization and runs value iteration
        for a given number of iterations using the supplied
        discount factor.
    """
    def __init__(self, mdp, discount = 0.9, iterations = 100):
        """
          Your value iteration agent should take an mdp on
          construction, run the indicated number of iterations
          and then act according to the resulting policy.

          Some useful mdp methods you will use:
              mdp.getStates()
              mdp.getPossibleActions(state)
              mdp.getTransitionStatesAndProbs(state, action)
              mdp.getReward(state, action, nextState)
              mdp.isTerminal(state)

              mpd.getStartState()

        """
        self.mdp = mdp
        self.discount = discount
        self.iterations = iterations
        self.values = util.Counter() # A Counter is a dict with default 0
        self.runValueIteration()

    def runValueIteration(self):
        # Write value iteration code here
        "*** YOUR CODE HERE ***"

        num_iterations = range(self.iterations)
        values = util.Counter()

        for i in num_iterations:
            values = util.Counter()
            for s in self.mdp.getStates():
                if self.mdp.isTerminal(s):
                    values[s] = 0
                else:
                    max_val = float("-inf") # will be used to compare max from other values
                    for a in self.mdp.getPossibleActions(s):
                        num = 0
                        for tranState, prob in self.mdp.getTransitionStatesAndProbs(s, a):
                            num += prob * (self.mdp.getReward(s, a, tranState) + (self.values[tranState] * self.discount))
                        max_val = max(num, max_val)
                        values[s] = max_val
            self.values = values



    def getValue(self, state):
        """
          Return the value of the state (computed in __init__).
        """
        return self.values[state]


    def computeQValueFromValues(self, state, action):
        """
          Compute the Q-value of action in state from the
          value function stored in self.values.
        """
        "*** YOUR CODE HERE ***"

        # similar to what I did above in value iteration
        num = 0
        for transState, prob in self.mdp.getTransitionStatesAndProbs(state, action):

            num += prob * (self.mdp.getReward(state, action, transState) + (self.discount * self.values[transState]))
        return num


    def computeActionFromValues(self, state):
        """
          The policy is the best action in the given state
          according to the values currently stored in self.values.

          You may break ties any way you see fit.  Note that if
          there are no legal actions, which is the case at the
          terminal state, you should return None.
        """
        "*** YOUR CODE HERE ***"

        num = 0
        actions = self.mdp.getPossibleActions(state)
        if len(actions) > 0:
            values = util.Counter()
            for action in actions:
                values[action] = self.getQValue(state, action)

            num = values.argMax()
        return num



    def getPolicy(self, state):
        return self.computeActionFromValues(state)

    def getAction(self, state):
        "Returns the policy at the state (no exploration)."
        return self.computeActionFromValues(state)

    def getQValue(self, state, action):
        return self.computeQValueFromValues(state, action)

class AsynchronousValueIterationAgent(ValueIterationAgent):
    """
        * Please read learningAgents.py before reading this.*

        An AsynchronousValueIterationAgent takes a Markov decision process
        (see mdp.py) on initialization and runs cyclic value iteration
        for a given number of iterations using the supplied
        discount factor.
    """
    def __init__(self, mdp, discount = 0.9, iterations = 1000):
        """
          Your cyclic value iteration agent should take an mdp on
          construction, run the indicated number of iterations,
          and then act according to the resulting policy. Each iteration
          updates the value of only one state, which cycles through
          the states list. If the chosen state is terminal, nothing
          happens in that iteration.

          Some useful mdp methods you will use:
              mdp.getStates()
              mdp.getPossibleActions(state)
              mdp.getTransitionStatesAndProbs(state, action)
              mdp.getReward(state)
              mdp.isTerminal(state)
        """
        # In the first iteration, only update the value of the first state in
        # the states list. In the second iteration, only update the value of
        # the second. Keep going until you have updated the value of each state
        # once, then start back at the first state for the subsequent iteration.
        # If the state picked for updating is terminal, nothing happens in that
        # iteration. You should be indexing into the states variable defined in
        # the code skeleton.
        # self.mdp = mdp
        # self.discount = discount
        # self.iterations = iterations


        ValueIterationAgent.__init__(self, mdp, discount, iterations)


    def runValueIteration(self):
        "*** YOUR CODE HERE ***"

        states = self.mdp.getStates()
        num_iterations = range(self.iterations)
        for i in num_iterations:
            state = states[i % len(states)]

            if not self.mdp.isTerminal(state):
                action_from_val = self.computeActionFromValues(state)
                q_val = self.computeQValueFromValues(state, action_from_val)
                self.values[state] = q_val


class PrioritizedSweepingValueIterationAgent(AsynchronousValueIterationAgent):
    """
        * Please read learningAgents.py before reading this.*

        A PrioritizedSweepingValueIterationAgent takes a Markov decision process
        (see mdp.py) on initialization and runs prioritized sweeping value iteration
        for a given number of iterations using the supplied parameters.
    """
    def __init__(self, mdp, discount = 0.9, iterations = 100, theta = 1e-5):
        """
          Your prioritized sweeping value iteration agent should take an mdp on
          construction, run the indicated number of iterations,
          and then act according to the resulting policy.
        """
        self.theta = theta
        ValueIterationAgent.__init__(self, mdp, discount, iterations)
        self.mdp = mdp
        self.discount = discount
        self.iterations = iterations


    """ Gets the non zero probability states that can be reached from
        parameter state. Returns a set of these states."""
    def get_predecessors(self, state):
        non_zero_prob_states = set()
        if not self.mdp.isTerminal(state):
            states = self.mdp.getStates()

            for s in states:
                if not self.mdp.isTerminal(s):
                    for a in self.mdp.getPossibleActions(s):

                        transition = self.mdp.getTransitionStatesAndProbs(s, a)
                        for nextState in transition:
                            if nextState[0] == state and nextState[1] > 0:
                                non_zero_prob_states.add(s)
        return non_zero_prob_states


    def highestQValue(self, state):
        highest_q = float("-inf")
        for a in self.mdp.getPossibleActions(state):
            if self.getQValue(state, a) > highest_q:
                highest_q = self.getQValue(state, a)
        return highest_q


    def runValueIteration(self):
        "*** YOUR CODE HERE ***"
        states = self.mdp.getStates()
        num_iterations = range(self.iterations)

        predecessorsOfAllStates = {}

        # populate the dictionary with predecessors of each state
        for s in states:
            predecessorsOfAllStates[s] = self.get_predecessors(s)

        priority_val = util.PriorityQueue()

        for s in states:
            if not self.mdp.isTerminal(s):
                diff = abs(self.values[s] - self.highestQValue(s))
                priority_val.update(s, -diff)

        for i in num_iterations:
            if priority_val.isEmpty():
                return

            state = priority_val.pop()
            self.values[state] = self.highestQValue(state)

            for p in list(predecessorsOfAllStates[state]):
                diff = abs(self.values[p] - self.highestQValue(p))

                if diff > self.theta:
                    priority_val.update(p, -diff)
