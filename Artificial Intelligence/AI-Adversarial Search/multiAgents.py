# multiAgents.py
# --------------
# Licensing Information:  You are free to use or extend these projects for 
# educational purposes provided that (1) you do not distribute or publish 
# solutions, (2) you retain this notice, and (3) you provide clear 
# attribution to UC Berkeley, including a link to 
# http://inst.eecs.berkeley.edu/~cs188/pacman/pacman.html
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero 
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and 
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


from util import manhattanDistance
from game import Directions
from game import Actions
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

        "*** YOUR CODE HERE ***"
        distances = []
        ghostDistances = []
        foodList = newFood.asList()
        agentDir = successorGameState.getPacmanState().getDirection()
        ghostPos = successorGameState.getGhostPositions()
        if newPos in currentGameState.getFood().asList():
            foodDist = 0.00001;
            #print("fouqhfouqhfoiqwhfoiqhfoqhfqoifhqoifhqoifqiow")
        else:
            for xy in foodList: 
                distances.append(manhattanDistance(newPos,xy))        
            foodDist = min(distances)
        totalDist = newFood.width + newFood.height
        #return (foodDist/ float(totalDist))*100
        #return 1/foodDist
        #for i in newScaredTimes:
        #    if i > 0:
        #        ghostPos.remove(i)
        #        newGhostStates.remove(i)
        #
        for xy in ghostPos:
            ghostDistances.append(manhattanDistance(newPos, xy))
        ghostDist = min(ghostDistances)
        #print(agentDir)
        if currentGameState.getPacmanPosition() == newPos:
            return 0
        #print(0.001* ghostDist)
        #print("foodDist:   "  , 1/float(foodDist))
        if ghostDist < 4:
          return 0.01
        return 1/float(foodDist) + 0.01*ghostDist
        #ghostDirs = []
        #for i in range(len(newGhostStates)):
        #    ghostDirs.append(newGhostStates[i].getDirection())
        #agentDir = successorGameState.getPacmanState().getDirection()
        #
        #dirs = 1
        #if newPos[0] in [x[0] for x in ghostPos] and agentDir in ['East', 'West'] and REVERSE[agentDir] in ghostDirs:
        #    dirs = 0.1
        #if newPos[0] in [x[1] for x in ghostPos] and agentDir in ['North', 'South'] and REVERSE[agentDir] in ghostDirs:
        #    dirs = 0.1
        #    
        #return (ghostDist + 1/foodDist)*dirs
        #return successorGameState.getScore()

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
        """
        "*** YOUR CODE HERE ***"
        depth = self.depth
        numAgents = gameState.getNumAgents() 
        acts = {}

        def value(state, remaining):
            turn = ((depth*numAgents)-remaining)%numAgents
            if state.isWin() or state.isLose() or remaining == 0:
                return self.evaluationFunction(state)
            elif turn == 0:
                return maxValue(state, remaining)
            else:
                return minValue(state, turn, remaining)
    
        def maxValue(state, remaining):
            v = float('-inf')
            for action in state.getLegalActions(0):
                val = value(state.generateSuccessor(0, action),remaining-1)
                v = max(v, val) 
                if remaining == depth*numAgents:
                    acts[val] = action
            return v
        
        def minValue(state, turn, remaining):
            v = float('inf')
            for action in state.getLegalActions(turn):
                v = min(v, value(state.generateSuccessor(turn, action), remaining-1)) 
            return v

        return acts[maxValue(gameState,self.depth*numAgents)]

class AlphaBetaAgent(MultiAgentSearchAgent):
    """
      Your minimax agent with alpha-beta pruning (question 3)
    """

    def getAction(self, gameState):
        """
          Returns the minimax action using self.depth and self.evaluationFunction
        """
        "*** YOUR CODE HERE ***"
        depth = self.depth
        numAgents = gameState.getNumAgents() 
        acts = {}

        def value(state, remaining, alpha, beta):
            turn = ((depth*numAgents)-remaining)%numAgents
            if state.isWin() or state.isLose() or remaining == 0:
                return self.evaluationFunction(state)
            elif turn == 0:
                return maxValue(state, remaining, alpha, beta)
            else:
                return minValue(state, turn, remaining, alpha, beta)
    
        def maxValue(state, remaining, alpha, beta):
            v = float('-inf')
            for action in state.getLegalActions(0):
                val = value(state.generateSuccessor(0, action),remaining-1, alpha, beta)
                v = max(v, val) 
                if remaining == depth*numAgents:
                    acts[val] = action
                if v > beta:
                    return v
                alpha = max(alpha, v)
            return v
    
        def minValue(state, turn, remaining, alpha, beta):
            v = float('inf')
            for action in state.getLegalActions(turn):
                v = min(v, value(state.generateSuccessor(turn, action), remaining-1, alpha, beta)) 
                if v < alpha:
                    return v
                beta = min(beta, v)
            return v

        return acts[maxValue(gameState,self.depth*numAgents, float('-inf'), float('inf'))]


class ExpectimaxAgent(MultiAgentSearchAgent):
    """
      Your expectimax agent (question 4)
    """

    def getAction(self, gameState):
        """
          Returns the expectimax action using self.depth and self.evaluationFunction

          All ghosts should be modeled as choosing uniformly at random from their
          legal moves.
        """
        "*** YOUR CODE HERE ***"
        depth = self.depth
        numAgents = gameState.getNumAgents() 
        acts = {}

        def value(state, remaining):
            turn = ((depth*numAgents)-remaining)%numAgents
            if state.isWin() or state.isLose() or remaining == 0:
                return self.evaluationFunction(state)
            elif turn == 0:
                return maxValue(state, remaining)
            else:
                return expectedValue(state, turn, remaining)
    
        def maxValue(state, remaining):
            v = float('-inf')
            for action in state.getLegalActions(0):
                val = value(state.generateSuccessor(0, action),remaining-1)
                v = max(v, val) 
                if remaining == depth*numAgents:
                    acts[val] = action
            return v
    
        def expectedValue(state, turn, remaining):
            options = state.getLegalActions(turn)
            v = 0
            for action in options:
                v = v + (1/float(len(options)))* value(state.generateSuccessor(turn, action), remaining-1)
            return v

        exp = maxValue(gameState,self.depth*numAgents)
        #print("chosen value:     ", exp)
        #print("action: ", acts[exp])
        return acts[exp]

distDict = {}
ghostDict = {}
def betterEvaluationFunction(currentGameState):
    """
      Your extreme ghost-hunting, pellet-nabbing, food-gobbling, unstoppable
      evaluation function (question 5).

      DESCRIPTION: <write something here so we know what you did>
    """
    "*** YOUR CODE HERE ***"
    if currentGameState.isWin():
        return 9999 + 10*currentGameState.getScore()
    if currentGameState.isLose():
        return -99999

    pos = currentGameState.getPacmanPosition()
    distances = []
    ghostDistances = []
    capsuleDistances = []

    #--------------FOOD DISTANCE--------------#
    for xy in currentGameState.getFood().asList(): 
        key = (pos,xy)
        if key not in distDict:
            mazeDist = mazeDistance(pos,xy,currentGameState)
            distances.append(mazeDist)
            distDict[key] = mazeDist
        else:
            distances.append(distDict[key])       
    foodDist1 = 10/float(min(distances)+1)
    foodDist2 = 0
    if len(distances) != 1:
        distances.remove(min(distances))
        foodDist2 = 8/float(min(distances)+1)

    #-------------CAPSULE DISTANCE------------#
    capsules = currentGameState.getCapsules()
    for xy in capsules: 
        key = (pos,xy)
        if key not in distDict:
            mazeDist = mazeDistance(pos,xy,currentGameState)
            capsuleDistances.append(mazeDist)
            distDict[key] = mazeDist
        else:
            capsuleDistances.append(distDict[key]) 
    capsuleDist = 0
    if len(capsules) != 0:      
        capsuleDist = 1/float(min(capsuleDistances)+1)
    capsulesLeft = len(capsules)

    return foodDist1 + foodDist2 + currentGameState.getScore() +capsuleDist - 1000*capsulesLeft

# Abbreviation
better = betterEvaluationFunction




def breadthFirstSearch(problem):
    """
    Search the shallowest nodes in the search tree first.
    """
    "*** YOUR CODE HERE ***"
    closed = set()
    fringe = util.Queue()
    fringe.push(Node(problem.getStartState(),[],None))
    while(1):
        if fringe.isEmpty():
            return ['STOP']
        node = fringe.pop()
        if problem.isGoalState(node.getState()):
            return node.getActions()
        if node.getState() not in closed:
            closed.add(node.getState())
            for childNode in problem.getSuccessors(node.getState()):
                fringe.push(Node(childNode[0],[childNode[1]],node))

bfs = breadthFirstSearch


class Node:
    def __init__(self,state,act,prev, pathCost=0):
        self.state = state
        self.pathCost = pathCost
        if prev != None:
            self.act = prev.getActions() + act
        else:
            self.act = act

    def getState(self):
        return self.state

    def getActions(self):
        return self.act

    def getPathCost(self):
        return self.pathCost



class PositionSearchProblem():
    """
    A search problem defines the state space, start state, goal test,
    successor function and cost function.  This search problem can be
    used to find paths to a particular point on the pacman board.

    The state space consists of (x,y) positions in a pacman game.

    Note: this search problem is fully specified; you should NOT change it.
    """
    def __init__(self, gameState, costFn = lambda x: 1, goal=(1,1), start=None, warn=True, visualize=True):
        """
        Stores the start and goal.

        gameState: A GameState object (pacman.py)
        costFn: A function from a search state (tuple) to a non-negative number
        goal: A position in the gameState
        """
        self.walls = gameState.getWalls()
        self.startState = gameState.getPacmanPosition()
        if start != None: self.startState = start
        self.goal = goal
        self.costFn = costFn
        self.visualize = visualize
        if warn and (gameState.getNumFood() != 1 or not gameState.hasFood(*goal)):
            print 'Warning: this does not look like a regular search maze'

        # For display purposes
        self._visited, self._visitedlist, self._expanded = {}, [], 0 # DO NOT CHANGE

    def getStartState(self):
        return self.startState

    def isGoalState(self, state):
        isGoal = state == self.goal

        # For display purposes only
        if isGoal and self.visualize:
            self._visitedlist.append(state)
            import __main__
            if '_display' in dir(__main__):
                if 'drawExpandedCells' in dir(__main__._display): #@UndefinedVariable
                    __main__._display.drawExpandedCells(self._visitedlist) #@UndefinedVariable

        return isGoal

    def getSuccessors(self, state):
        """
        Returns successor states, the actions they require, and a cost of 1.

         As noted in search.py:
             For a given state, this should return a list of triples,
         (successor, action, stepCost), where 'successor' is a
         successor to the current state, 'action' is the action
         required to get there, and 'stepCost' is the incremental
         cost of expanding to that successor
        """
        successors = []
        for action in [Directions.NORTH, Directions.SOUTH, Directions.EAST, Directions.WEST]:
            x,y = state
            dx, dy = Actions.directionToVector(action)
            nextx, nexty = int(x + dx), int(y + dy)
            if not self.walls[nextx][nexty]:
                nextState = (nextx, nexty)
                cost = self.costFn(nextState)
                successors.append( ( nextState, action, cost) )

        # Bookkeeping for display purposes
        self._expanded += 1 # DO NOT CHANGE
        if state not in self._visited:
            self._visited[state] = True
            self._visitedlist.append(state)

        return successors

    def getCostOfActions(self, actions):
        """
        Returns the cost of a particular sequence of actions.  If those actions
        include an illegal move, return 999999
        """
        if actions == None: return 999999
        x,y= self.getStartState()
        cost = 0
        for action in actions:
            # Check figure out the next state and see whether its' legal
            dx, dy = Actions.directionToVector(action)
            x, y = int(x + dx), int(y + dy)
            if self.walls[x][y]: return 999999
            cost += self.costFn((x,y))
        return cost

def mazeDistance(point1, point2, gameState):
    """
    Returns the maze distance between any two points, using the search functions
    you have already built.  The gameState can be any game state -- Pacman's position
    in that state is ignored.

    Example usage: mazeDistance( (2,4), (5,6), gameState)

    This might be a useful helper function for your ApproximateSearchAgent.
    """
    x1, y1 = point1
    x2, y2 = point2
    walls = gameState.getWalls()
    assert not walls[x1][y1], 'point1 is a wall: ' + point1
    assert not walls[x2][y2], 'point2 is a wall: ' + str(point2)
    prob = PositionSearchProblem(gameState, start=point1, goal=point2, warn=False, visualize=False)
    return len(bfs(prob))