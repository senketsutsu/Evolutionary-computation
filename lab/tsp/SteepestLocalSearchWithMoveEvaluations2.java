package lab.tsp;

import java.util.*;
import static lab.tsp.SteepestLocalSearchWithCandidates.calculateEdgeExchangeDelta;
import static lab.tsp.SteepestLocalSearchWithCandidates.calculateNodeInsertionDelta;

public class SteepestLocalSearchWithMoveEvaluations {

    private static final int CANDIDATE_COUNT = 10; // Number of nearest neighbors

    public List<Integer> optimize(List<Integer> initialSolution, double[][] distanceMatrix, double[][] nodes, String moveVariant) {
        List<Integer> solution = new ArrayList<>(initialSolution);
        SortedSet<Move> LM = new TreeSet<Move>(Comparator.comparingDouble(move -> move.delta));

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

        while (improved) {
            improved = false;

            // Evaluate all new moves and add improving moves to LM TODO - done in the loop below

            // Loop over the SortedSet (LM) to apply the best improvement
            Iterator<Move> iterator = LM.iterator();
            while (iterator.hasNext()) {
                Move move = iterator.next();
                List<Object> moveDetails = move.getMoveDetails();
                int[] nodesArr1 = (int[]) moveDetails.get(0);
                int[] nodesArr2 = (int[]) moveDetails.get(1);
                // Check if m is applicable and if not remove it from LM TODO
                boolean applicable = checkMoveValidity(solution, moveDetails, solutionSet, move.delta, distanceMatrix, nodes);
                if (applicable) {
                    improved = true;
                    solution = makeMove(solution, moveDetails, moveVariant, distanceMatrix, nodes);
                    iterator.remove();  // Remove the applied move

                    // Evaluate all new moves and add improving moves to LM
                    if(moveVariant.contains("intra")){
                        int[] new_moves = new int[]{nodesArr1[0], nodesArr1[1], nodesArr2[0], nodesArr2[1]};
                        for (int i = 0; i < 4; i++) {
                            for (int j = 0; j < n; j++) {
                                // Forward direction move
                                String moveType = "intra_False";
                                int node1 = new_moves[i];
                                int node2 = solution.get(j);
                                int adjNode1 = solution.get((solution.indexOf(node1) + 1) % n);
                                int adjNode2 = solution.get((j + 1) % n);
                                List<Object> new_move = Arrays.asList(new int[]{node1, adjNode1}, new int[]{node2, adjNode2}, new String[]{moveType, moveType});
                                double delta = calculateEdgeExchangeDelta(solution, new int[]{solution.indexOf(node1), (solution.indexOf(node1)+1)%n}, new int[]{j, (j+1)%n} , distanceMatrix, nodes);
                                if (delta < 0) {
                                    LM.add(new Move(delta, new_move));
                                }

                                // Backward direction move
                                moveType = "intra_True";
                                adjNode1 = solution.get((solution.indexOf(node1) - 1 + n) % n);
                                adjNode2 = solution.get((j - 1 + n) % n);
                                new_move = Arrays.asList(new int[]{node1, adjNode1}, new int[]{node2, adjNode2}, new String[]{moveType, moveType});
                                delta = calculateEdgeExchangeDelta(solution, new int[]{(solution.indexOf(node1)-1+n)%n, solution.indexOf(node1)}, new int[]{(j-1+n)%n, j} , distanceMatrix, nodes);
                                if (delta < 0) {
                                    LM.add(new Move(delta, new_move));
                                }
                            }
                        }

                        for (int i = 0; i < N; i++) {
                            if (!solution.contains(i)) {
                                for(int j=0; j<4; j++){
                                    String moveType = "inter";
                                    int node1 = i;
                                    int node2 = new_moves[j];
                                    List<Object> new_move = Arrays.asList(new int[]{node1, node1}, new int[]{node2, node2}, new String[]{moveType, moveType});
                                    double delta = calculateNodeInsertionDelta(solution, solution.indexOf(node2), node1, distanceMatrix, nodes);
                                    if (delta < 0) {
                                        LM.add(new Move(delta, new_move));
                                    }
                                }

                            }
                        }
                    }else if(moveVariant.contains("inter")){
                        int[] new_moves = new int[]{nodesArr1[0], nodesArr2[0]};
                        for (int j = 0; j < n; j++) {
                            // Forward direction move
                            String moveType = "intra_False";
                            int node1 = new_moves[nodesArr2[0]];
                            int node2 = solution.get(j);
                            int adjNode1 = solution.get((solution.indexOf(node1) + 1) % n);
                            int adjNode2 = solution.get((j + 1) % n);
                            List<Object> new_move = Arrays.asList(new int[]{node1, adjNode1}, new int[]{node2, adjNode2}, new String[]{moveType, moveType});
                            double delta = calculateEdgeExchangeDelta(solution, new int[]{solution.indexOf(node1), (solution.indexOf(node1)+1)%n}, new int[]{j, (j+1)%n} , distanceMatrix, nodes);
                            if (delta < 0) {
                                LM.add(new Move(delta, new_move));
                            }

                            // Backward direction move
                            moveType = "intra_True";
                            adjNode1 = solution.get((solution.indexOf(node1) - 1 + n) % n);
                            adjNode2 = solution.get((j - 1 + n) % n);
                            new_move = Arrays.asList(new int[]{node1, adjNode1}, new int[]{node2, adjNode2}, new String[]{moveType, moveType});
                            delta = calculateEdgeExchangeDelta(solution, new int[]{(solution.indexOf(node1)-1+n)%n, solution.indexOf(node1)}, new int[]{(j-1+n)%n, j} , distanceMatrix, nodes);
                            if (delta < 0) {
                                LM.add(new Move(delta, new_move));
                            }
                        }


                        for (int i = 0; i < N; i++) {
                            if (!solution.contains(i)) {
                                String moveType = "inter";
                                int node1 = i;
                                int node2 = nodesArr2[0];
                                List<Object> new_move = Arrays.asList(new int[]{node1, node1}, new int[]{node2, node2}, new String[]{moveType, moveType});
                                double delta = calculateNodeInsertionDelta(solution, solution.indexOf(node2), node1, distanceMatrix, nodes);
                                if (delta < 0) {
                                    LM.add(new Move(delta, new_move));
                                }
                            }else{
                                String moveType = "inter";
                                int node1 = nodesArr1[0];
                                int node2 = i;
                                List<Object> new_move = Arrays.asList(new int[]{node1, node1}, new int[]{node2, node2}, new String[]{moveType, moveType});
                                double delta = calculateNodeInsertionDelta(solution, solution.indexOf(node2), node1, distanceMatrix, nodes);
                                if (delta < 0) {
                                    LM.add(new Move(delta, new_move));
                                }
                            }
                        }
                    }
                    break;  // Restart with updated solution
                } else {
                    // Handle cases where the move may no longer be applicable

                    String[] moveTypes = (String[]) moveDetails.get(2);
                    Integer moveStillPossible = checkMovePossibleAgain(solution, nodesArr1, nodesArr2, moveTypes);

                    if (moveStillPossible == 0) {
                        iterator.remove();
                    } else if (moveStillPossible == 2){
                        improved = true;
                        iterator.remove();
                        // solution = makeMove(solution, moveDetails, moveVariant, distanceMatrix, nodes);
                        // LM.remove(move);
                    }
                }
            }
            // if move m has been found then    TODO
            //x := m(x) (accept m(x))
        }

        return solution;
    }

    // Checks whether a move is valid in the current solution
    private boolean checkMoveValidity(List<Integer> solution, List<Object> moveDetails, Set<Integer> solutionSet, double delta, double[][] distanceMatrix, double[][] nodes) {
        int[] node1 = (int[]) moveDetails.get(0);
        int[] node2 = (int[]) moveDetails.get(1);
        String[] moveTypes = (String[]) moveDetails.get(2);
        if (moveTypes[0].contains("intra")) {

            if(moveTypes[0].contains("False")){
                double new_delta = calculateEdgeExchangeDelta(solution, node1, node2 , distanceMatrix, nodes);
                if (Math.abs(new_delta - delta) > 1e-6) {
                    return false;
                }
            }else if(moveTypes[0].contains("True")){
                double new_delta = calculateEdgeExchangeDelta(solution, new int[]{node1[1], node1[0]}, new int[]{node2[1], node2[0]} , distanceMatrix, nodes);
                if (Math.abs(new_delta - delta) > 1e-6) {
                    return false;
                }
            }

            return solution.contains(node1[0]) && solution.contains(node1[1]) && solution.contains(node2[0]) && solution.contains(node2[1]);
        } else if (moveTypes[0].contains("inter")) {
            if(!solution.contains(node2[0])){return false;}
            double new_delta = calculateNodeInsertionDelta(solution, solution.indexOf(node2[0]), node1[0], distanceMatrix, nodes);
            if (Math.abs(new_delta - delta) > 1e-6) {
                return false;
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

        if (moveTypes[0].contains("intra_False")) {
            // Intra-route edge exchange in the forward direction
            if (nodesArr1idx >= 0 && nodesArr1idx < solution.size()) {
                solution.set(nodesArr1idx, nodesArr2[0]);
                solution.set((nodesArr1idx+1)%solution.size(), nodesArr2[1]);
            }

            if (nodesArr2idx >= 0 && nodesArr2idx < solution.size()) {
                solution.set(nodesArr2idx, nodesArr1[0]);
                solution.set((nodesArr2idx+1)%solution.size(), nodesArr1[1]);
            }
        } else if (moveTypes[0].contains("intra_True")) {
            // Intra-route edge exchange in the reverse direction
            if (nodesArr1idx >= 0 && nodesArr1idx < solution.size()) {
                solution.set(nodesArr1idx, nodesArr2[0]);
                solution.set((nodesArr1idx-1+solution.size())%solution.size(), nodesArr2[1]);}

            if (nodesArr2idx >= 0 && nodesArr2idx < solution.size()) {
                solution.set(nodesArr2idx, nodesArr1[0]);
                solution.set((nodesArr2idx-1+solution.size())%solution.size(), nodesArr1[1]);}
        } else if (moveTypes[0].contains("inter")) {
            solution.set(nodesArr2idx, nodesArr1[0]);
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

            if ((edgesInSameDirection && moveTypes[0].contains("False")) || (edgesInDifferentDirection && moveTypes[0].contains("True"))) {

                return 2;
            }
            else{
                
                return 1;
            }
        }
    
        // For inter-moves, simply check if the nodes are in/out of the solution appropriately
        if (moveTypes[0].contains("inter")) {
            // Additional logic for inter-move directionality can go here
            return 1; // Placeholder: Assume inter-move remains valid for now
        }
    
        return 0; // Default: Remove the move if no valid conditions are met
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