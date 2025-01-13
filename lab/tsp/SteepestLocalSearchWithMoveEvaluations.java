package lab.tsp;

import java.util.*;

import static lab.tsp.SteepestLocalSearchWithCandidates2.calculateEdgeExchangeDelta;
import static lab.tsp.SteepestLocalSearchWithCandidates2.calculateNodeInsertionDelta;


public class SteepestLocalSearchWithMoveEvaluations {

    private static final int CANDIDATE_COUNT = 10; // Number of nearest neighbors
    SortedSet<Move> LM = new TreeSet<Move>(Comparator.comparingDouble(move -> move.delta));
    List<Move> removedMoves = new ArrayList<>();

    public List<Integer> optimize(List<Integer> initialSolution, double[][] distanceMatrix, double[][] nodes, String moveVariant) {
        List<Integer> solution = new ArrayList<>(initialSolution);
        //SortedSet<Move> LM = new TreeSet<Move>(Comparator.comparingDouble(move -> move.delta));

        int n = solution.size();
        int N = distanceMatrix.length;
        Set<Integer> solutionSet = new HashSet<>(solution);
        Set<Integer> outerNodesSet = new HashSet<>();
        for (int i = 0; i < N; i++) {
            if (!solutionSet.contains(i)) outerNodesSet.add(i);
        }

        boolean improved = true;

        // Generate initial LM
        // Generate Inter-route Moves
        for (Integer outerNode : outerNodesSet) {
            for (int innerNodeIdx = 0; innerNodeIdx < n; innerNodeIdx++) {
                String moveType = "inter";
                int node1 = outerNode;
                int node2 = solution.get(innerNodeIdx);
                List<Object> move = Arrays.asList(new int[]{node1, node1}, new int[]{node2, node2}, new String[]{moveType, moveType});
                double delta = calculateNodeInsertionDelta(solution, innerNodeIdx, outerNode, distanceMatrix, nodes);
                if (delta < 0) {
                    LM.add(new Move(delta, move));
                }
            }
        }

        // Generate Intra-route Moves
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                // Forward direction move
                String moveType = "intra_False";
                int node1 = solution.get(i);
                int node2 = solution.get(j);
                int adjNode1 = solution.get((i + 1) % n);
                int adjNode2 = solution.get((j + 1) % n);
                List<Object> move = Arrays.asList(new int[]{node1, adjNode1}, new int[]{node2, adjNode2}, new String[]{moveType, moveType});
                double delta = calculateEdgeExchangeDelta(solution, new int[]{i, (i+1)%n}, new int[]{j, (j+1)%n} , distanceMatrix, nodes);
                if (delta < 0) {
                    LM.add(new Move(delta, move));
                }

                // Backward direction move
                moveType = "intra_True";
                adjNode1 = solution.get((i - 1 + n) % n);
                adjNode2 = solution.get((j - 1 + n) % n);
                move = Arrays.asList(new int[]{node1, adjNode1}, new int[]{node2, adjNode2}, new String[]{moveType, moveType});
                delta = calculateEdgeExchangeDelta(solution, new int[]{(i-1+n)%n, i}, new int[]{(j-1+n)%n, j} , distanceMatrix, nodes);
                if (delta < 0) {
                    LM.add(new Move(delta, move));
                }
            }
        }

        // Add a list of new moves that we need to evaluate (moves that were not possible before but after changing the current solution now they can occure)   TODO

        // while (!LM.isEmpty()) {

        while (improved) {
            improved = false;

            // Create a copy of the local moves list (LM) to iterate over safely
            List<Move> lmCopy = new ArrayList<>(LM);

            for (Move move : lmCopy) {
                List<Object> moveDetails = move.getMoveDetails();

                // Validate if the move is still applicable
                boolean applicable = checkMoveValidity(solution, moveDetails, solutionSet, move.delta, distanceMatrix, nodes, move);

                if (applicable) {
                    System.out.println("f1");
                    // Apply the move and mark improvement
                    improved = true;
                    solution = makeMove(solution, moveDetails, moveVariant, distanceMatrix, nodes);
                    removedMoves.add(move); // Remove the applied move

                    // Get the affected nodes and their neighbors
                    Set<Integer> affectedNodes = getNeighborsFromMove(moveDetails, solution);

                    // Evaluate and add potential new moves for all affected nodes
                    for (int node : affectedNodes) {
                        System.out.println("f2");
                        evaluateAndAddMoves(node, solution, solutionSet, distanceMatrix, nodes);
                    }

                    // Break to restart evaluation after applying a move
                    break;
                }
            }

            // Remove processed moves from LM
            LM.removeAll(removedMoves);
            removedMoves.clear();
        }

        return solution;
    }

    private Set<Integer> getNeighborsFromMove(List<Object> moveDetails, List<Integer> solution) {
        int[] nodesArr1 = (int[]) moveDetails.get(0);
        int[] nodesArr2 = (int[]) moveDetails.get(1);

        Set<Integer> neighbors = new HashSet<>();
        for (int node : nodesArr1) {
            neighbors.add(node);
            neighbors.add(solution.get((solution.indexOf(node) + 1) % solution.size())); // Forward neighbor
            neighbors.add(solution.get((solution.indexOf(node) - 1 + solution.size()) % solution.size())); // Backward neighbor
        }
        for (int node : nodesArr2) {
            neighbors.add(node);
            neighbors.add(solution.get((solution.indexOf(node) + 1) % solution.size())); // Forward neighbor
            neighbors.add(solution.get((solution.indexOf(node) - 1 + solution.size()) % solution.size())); // Backward neighbor
        }
        return neighbors;
    }

    private void evaluateAndAddMoves(int node, List<Integer> solution, Set<Integer> solutionSet,
                                     double[][] distanceMatrix, double[][] nodes) {
        int n = solution.size();

        // Evaluate intra-solution moves
        for (int j = 0; j < n; j++) {
            int adjNode = solution.get((j + 1) % n);

            // Forward direction move
            String moveType = "intra_False";
            double delta = calculateEdgeExchangeDelta(solution,
                    new int[]{solution.indexOf(node), (solution.indexOf(node) + 1) % n},
                    new int[]{j, (j + 1) % n}, distanceMatrix, nodes);
            if (delta < 0) {
                List<Object> newMove = Arrays.asList(
                        new int[]{node, solution.get((solution.indexOf(node) + 1) % n)},
                        new int[]{adjNode, solution.get((j + 1) % n)},
                        new String[]{moveType, moveType}
                );
                LM.add(new Move(delta, newMove));
            }

            // Backward direction move
            moveType = "intra_True";
            delta = calculateEdgeExchangeDelta(solution,
                    new int[]{(solution.indexOf(node) - 1 + n) % n, solution.indexOf(node)},
                    new int[]{(j - 1 + n) % n, j}, distanceMatrix, nodes);
            if (delta < 0) {
                List<Object> newMove = Arrays.asList(
                        new int[]{solution.get((solution.indexOf(node) - 1 + n) % n), node},
                        new int[]{solution.get((j - 1 + n) % n), adjNode},
                        new String[]{moveType, moveType}
                );
                LM.add(new Move(delta, newMove));
            }
        }

        // Evaluate inter-solution moves (node insertion)
        for (int i = 0; i < nodes.length; i++) {
            if (!solution.contains(i)) {
                String moveType = "inter";
                double delta = calculateNodeInsertionDelta(solution, solution.indexOf(node), i, distanceMatrix, nodes);
                if (delta < 0) {
                    List<Object> newMove = Arrays.asList(
                            new int[]{i, i}, new int[]{node, node}, new String[]{moveType, moveType}
                    );
                    LM.add(new Move(delta, newMove));
                }
            }
        }
    }

    private void handleInvalidMove(Move move, List<Move> removedMoves, List<Integer> solution, double[][] nodes) {
        List<Object> moveDetails = move.getMoveDetails();
        int[] nodesArr1 = (int[]) moveDetails.get(0);
        int[] nodesArr2 = (int[]) moveDetails.get(1);
        String[] moveTypes = (String[]) moveDetails.get(2);

        Integer moveStillPossible = checkMovePossibleAgain(solution, nodesArr1, nodesArr2, moveTypes);
        if (moveStillPossible == 0) {
            removedMoves.add(move); // Permanently remove invalid move
        } else if (moveStillPossible == 2) {
            removedMoves.add(move); // Optionally re-check
        }
    }



    // Checks whether a move is valid in the current solution
    private boolean checkMoveValidity(List<Integer> solution, List<Object> moveDetails, Set<Integer> solutionSet, double delta, double[][] distanceMatrix, double[][] nodes, Move move) {
        int[] node1 = (int[]) moveDetails.get(0);
        int[] node2 = (int[]) moveDetails.get(1);
        String[] moveTypes = (String[]) moveDetails.get(2);
        if (moveTypes[0].contains("intra")) {

            if(moveTypes[0].contains("False")){
                double new_delta = calculateEdgeExchangeDelta(solution, node1, node2 , distanceMatrix, nodes);
                if (new_delta <= delta) {

                    return true;
                }
            }else if(moveTypes[0].contains("True")){
                double new_delta = calculateEdgeExchangeDelta(solution, new int[]{node1[1], node1[0]}, new int[]{node2[1], node2[0]} , distanceMatrix, nodes);
                if (new_delta <= delta) {

                    return true;
                }
            }

            return solution.contains(node1[0]) && solution.contains(node1[1]) && solution.contains(node2[0]) && solution.contains(node2[1]);
        } else if (moveTypes[0].contains("inter")) {
            if(!solution.contains(node2[0])){return false;}
            double new_delta = calculateNodeInsertionDelta(solution, solution.indexOf(node2[0]), node1[0], distanceMatrix, nodes);
            if (new_delta <= delta) {

                return true;
            }
            return solution.contains(node2[0]) && !solution.contains(node1[0]);
        }
        return false;
    }

    // Applies a move to the solution
    private List<Integer> makeMove(List<Integer> solution, List<Object> moveDetails, String moveVariant, double[][] distanceMatrix, double[][] nodes) {
        int[] nodesArr1 = (int[]) moveDetails.get(0);
        int[] nodesArr2 = (int[]) moveDetails.get(1);
        String[] moveTypes = (String[]) moveDetails.get(2);

        int nodesArr1idx = solution.indexOf(nodesArr1[0]);
        int nodesArr2idx = solution.indexOf(nodesArr2[0]);
        //System.out.println("Making move: " + Arrays.toString(nodesArr1) + " <-> " + Arrays.toString(nodesArr2));

        if (moveTypes[0].contains("intra_False")) {
            // Intra-route edge exchange in the forward direction
            // Use applyEdgeExchange for edge swapping between nodesArr1 and nodesArr2
            SteepestLocalSearchWithCandidates.applyEdgeExchange(solution, nodesArr1, nodesArr2, moveVariant);
        } else if (moveTypes[0].contains("intra_True")) {
            // Intra-route edge exchange in the reverse direction
            // We will still use the edge exchange, but the direction will be reversed
            // So we just reverse the pairings when invoking applyEdgeExchange
            SteepestLocalSearchWithCandidates.applyEdgeExchange(solution, nodesArr2, nodesArr1, moveVariant);
        } else if (moveTypes[0].contains("inter")) {
            // Inter-route move: we don't use edge exchange, instead we just set the node positions
            if (nodesArr1idx >= 0 && nodesArr1idx < solution.size()) {
                solution.set(nodesArr1idx, nodesArr2[0]);
            }
            if (nodesArr2idx >= 0 && nodesArr2idx < solution.size()) {
                solution.set(nodesArr2idx, nodesArr1[0]);
            }
        }

        return solution;
    }

    private boolean isEdgeInSameDirection(List<Integer> solution, int node1, int node2) {
        int idx1 = solution.indexOf(node1);
        int idx2 = solution.indexOf(node2);

        if (idx1 == -1 || idx2 == -1) {
            return false; // At least one of the nodes does not exist
        }

        // Check if the edge appears in the same direction
        return (idx2 == (idx1 + 1) % solution.size() && idx1 == (idx2 - 1 + solution.size()) % solution.size());
    }

    private boolean isEdgeInDifferentDirection(List<Integer> solution, int node1, int node2) {
        int idx1 = solution.indexOf(node1);
        int idx2 = solution.indexOf(node2);

        if (idx1 == -1 || idx2 == -1) {
            return false; // At least one of the nodes does not exist
        }

        // Check if the edge appears in the opposite direction
        return (idx2 == (idx1 - 1 + solution.size()) % solution.size() && idx1 == (idx2 + 1) % solution.size());
    }

    private Integer checkMovePossibleAgain(List<Integer> solution, int[] nodesArr1, int[] nodesArr2, String[] moveTypes) {
        int node1 = nodesArr1[0];
        int adjNode1 = nodesArr1[1];
        int node2 = nodesArr2[0];
        int adjNode2 = nodesArr2[1];

        // Check if all nodes involved in the move are present in the solution
        boolean allNodesExist = solution.contains(node1) && solution.contains(adjNode1)
                && solution.contains(node2) && solution.contains(adjNode2);
        if (!allNodesExist) {
            return 0; // At least one node is missing; remove the move from LM
        }

        // Check edge directions for intra-moves
        if (moveTypes[0].contains("intra")) {
            boolean edgesInDifferentDirection = isEdgeInDifferentDirection(solution, node1, adjNode1)
                    && isEdgeInDifferentDirection(solution, node2, adjNode2);
            boolean edgesInSameDirection = isEdgeInSameDirection(solution, node1, adjNode1)
                    && isEdgeInSameDirection(solution, node2, adjNode2);

            if (edgesInSameDirection || edgesInDifferentDirection) {

                return 2;
            }
            else{

                return 1;
            }
        }
        return 0;
    }


    public static double calculateTwoEdgesExchangeDelta(List<Integer> currentSolution, int i, int j, double[][] distanceMatrix) {
        int nodeA1 = currentSolution.get(i);
        int nodeA2 = currentSolution.get((i + 1) % currentSolution.size());
        int nodeB1 = currentSolution.get(j);
        int nodeB2 = currentSolution.get((j + 1) % currentSolution.size());

        double originalCost = distanceMatrix[nodeA1][nodeA2] + distanceMatrix[nodeB1][nodeB2];
        double newCost = distanceMatrix[nodeA1][nodeB1] + distanceMatrix[nodeA2][nodeB2];

        return newCost - originalCost;
    }

    public static double calculateSingleNodeChangeDelta(List<Integer> currentSolution, int index, int newNode, double[][] distanceMatrix, double[][] nodes) {
        int currentNode = currentSolution.get(index);
        double currentNodeCost = nodes[currentNode][2];
        double newNodeCost = nodes[newNode][2];
        double costChange = newNodeCost - currentNodeCost;

        if (index == 0) {
            double originalCost = distanceMatrix[currentNode][currentSolution.get(index + 1)];
            double newCost = distanceMatrix[newNode][currentSolution.get(index + 1)];
            return costChange + (newCost - originalCost);
        } else if (index == currentSolution.size() - 1) {
            double originalCost = distanceMatrix[currentSolution.get(index - 1)][currentNode];
            double newCost = distanceMatrix[currentSolution.get(index - 1)][newNode];
            return costChange + (newCost - originalCost);
        } else {
            double originalCost = distanceMatrix[currentSolution.get(index - 1)][currentNode] + distanceMatrix[currentNode][currentSolution.get(index + 1)];
            double newCost = distanceMatrix[currentSolution.get(index - 1)][newNode] + distanceMatrix[newNode][currentSolution.get(index + 1)];
            return costChange + (newCost - originalCost);
        }
    }


    // Data structure for storing moves and their deltas
    private static class Move {
        double delta;
        List<Object> moveDetails;

        Move(double delta, List<Object> moveDetails) {
            this.delta = delta;
            this.moveDetails = moveDetails;
        }

        List<Object> getMoveDetails() {
            return moveDetails;
        }
    }
}