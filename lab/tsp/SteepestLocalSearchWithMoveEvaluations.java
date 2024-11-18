package lab.tsp;

import java.util.*;

public class SteepestLocalSearchWithMoveEvaluations {

    private static final int CANDIDATE_COUNT = 10; // Number of nearest neighbors

    public List<Integer> optimize(List<Integer> initialSolution, double[][] distanceMatrix, Map<Integer, List<Integer>> candidateEdges, String moveVariant, double[][] nodes) {
        List<Integer> currentSolution = new ArrayList<>(initialSolution);
        HashSet<Integer> currentSolutionSet = new HashSet<>(initialSolution);
        double bestCost = RandomSolution.calculateCost(currentSolution, distanceMatrix, nodes);
        boolean improvement = true;

        while (improvement) {
            improvement = false;

            // Initialize candidate moves
            List<Move> LM = initializeLM(currentSolution, SteepestLocalSearchWithCandidates.getUnselectedNodes(currentSolution, distanceMatrix.length), distanceMatrix, candidateEdges, nodes);

            // Process the moves in LM starting from the best move
            for (Move move : LM) {
                if (isMoveValid(move, currentSolution, currentSolutionSet)) {
                    applyMove(currentSolution, move, currentSolutionSet, nodes, distanceMatrix, moveVariant);
                    improvement = true;
                    break; // Recompute LM after applying the move
                }
            }
        }

        return currentSolution;
    }

    public List<Move> initializeLM(List<Integer> currentSolution, List<Integer> remainingNodes, double[][] distanceMatrix, Map<Integer, List<Integer>> candidateEdges, double[][] nodes) {
        List<Move> LM = new ArrayList<>();

        // Intra-solution moves (two-edges exchange)
        for (int i = 0; i < currentSolution.size(); i++) {
            for (int j = i + 1; j < currentSolution.size(); j++) {
                // Calculate delta for two-edges exchange
                double delta = SteepestLocalSearchWithCandidates.calculateTwoEdgesExchangeDelta(currentSolution, i, j, distanceMatrix);
                if (delta < 0) {
                    LM.add(new Move(i, j, delta, "intra"));
                }
            }
        }

        // Inter-solution moves (inserting nodes from remainingNodes into the solution)
        for (int i = 0; i < currentSolution.size(); i++) {
            int nodeInSolution = currentSolution.get(i);
            List<Integer> candidates = candidateEdges.getOrDefault(nodeInSolution, Collections.emptyList());

            for (int candidate : candidates) {
                if (remainingNodes.contains(candidate)) {
                    double delta = SteepestLocalSearchWithCandidates.calculateSingleNodeChangeDelta(currentSolution, i, candidate, distanceMatrix, nodes);
                    if (delta < 0) {
                        LM.add(new Move(i, candidate, delta, "inter"));
                    }
                }
            }
        }

        // Sort LM based on delta values (best improvement first)
        LM.sort(Comparator.comparingDouble(Move::getDelta));
        return LM;
    }

    private boolean isMoveValid(Move move, List<Integer> currentSolution, HashSet<Integer> currentSolutionSet) {
        if ("inter".equals(move.getType())) {
            // Inter-move: Ensure the node is not in the current solution
            return !currentSolutionSet.contains(move.getJ());
        } else if ("intra".equals(move.getType())) {
            // Intra-move: Ensure indices are valid and distinct
            return move.getI() >= 0 && move.getJ() >= 0 && move.getI() < currentSolution.size() && move.getJ() < currentSolution.size();
        }
        return false;
    }

    private void applyMove(List<Integer> currentSolution, Move move, HashSet<Integer> currentSolutionSet, double[][] nodes, double[][] distanceMatrix, String moveVariant) {
        if ("inter".equals(move.getType())) {
            // Inter-move: Replace a node in the solution with an outer node
            int oldNode = currentSolution.get(move.getI());
            int newNode = move.getJ();
            currentSolution.set(move.getI(), newNode);
            currentSolutionSet.remove(oldNode);
            currentSolutionSet.add(newNode);
        } else if ("intra".equals(move.getType())) {
            // Intra-move: Swap or reverse two edges
            SteepestLocalSearchWithCandidates.applyIntraMove(currentSolution, move.getI(), move.getJ(), moveVariant);
        }
    }

    static class Move {
        private final int i;
        private final int j;
        private final double delta;
        private final String type;

        public Move(int i, int j, double delta, String type) {
            this.i = i;
            this.j = j;
            this.delta = delta;
            this.type = type;
        }

        public int getI() {
            return i;
        }

        public int getJ() {
            return j;
        }

        public double getDelta() {
            return delta;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return "Move{" + "i=" + i + ", j=" + j + ", delta=" + delta + ", type='" + type + '\'' + '}';
        }
    }
}
