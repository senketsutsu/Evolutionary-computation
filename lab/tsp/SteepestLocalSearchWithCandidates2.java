package lab.tsp;

import java.util.*;

public class SteepestLocalSearchWithCandidates {

    private static final int CANDIDATE_COUNT = 10;

    public List<Integer> optimize(List<Integer> initialSolution, double[][] distanceMatrix, double[][] nodes, Map<Integer, List<Integer>> candidateEdges, String moveVariant) {
        List<Integer> currentSolution = new ArrayList<>(initialSolution);
        double bestCost = RandomSolution.calculateCost(currentSolution, distanceMatrix, nodes);
        boolean improvement = true;

        while (improvement) {
            improvement = false;
            double bestDelta = 0;
            int[] bestI = new int[0], bestJ = new int[0];
            boolean isInterRouteMove = false;

            for (int i = 0; i < currentSolution.size(); i++) {
                int node = currentSolution.get(i);
                List<Integer> candidates = candidateEdges.getOrDefault(node, Collections.emptyList());

                // Check intra-route candidate moves
                for (int candidate : candidates) {
                    if (currentSolution.contains(candidate)) {
                        int j = currentSolution.indexOf(candidate);
                        if (Math.abs(i - j) > 1 && j != -1) { // Avoid trivial moves
                            List<int[][]> moves = getEdgeMoves(currentSolution, i, j);
                            for(int[][] move : moves){
                                if (isValidMove(move, currentSolution.size())) {
                                    int[] move_i = move[0];
                                    int[] move_j = move[1];
                                    double delta = calculateEdgeExchangeDelta(currentSolution, move_i, move_j, distanceMatrix, nodes);
                                    if (delta < bestDelta) {
                                        bestDelta = delta;
                                        bestI = move_i;
                                        bestJ = move_j;
                                        isInterRouteMove = false;
                                    }
                                }
                            }
                        }
                    }else{
                        List<int[][]> moves = getNodeMoves(currentSolution, i, candidate);
                        for(int[][] move : moves) {
                            if (isValidMove(move, currentSolution.size())) {
                                int[] move_i = move[0];
                                int[] move_j = move[1];
                                double delta = calculateNodeInsertionDelta(currentSolution, move_i[0], move_j[0], distanceMatrix, nodes);
                                if (delta < bestDelta) {
                                    bestDelta = delta;
                                    bestI = move_i;
                                    bestJ = move_j;
                                    isInterRouteMove = true;
                                }
                            }
                        }
                    }
                }
            }

            // Apply the best move found
            if (bestDelta < 0) {
                if (isInterRouteMove) {
                    int replacedNode = currentSolution.set(bestI[0], bestJ[0]); // Replace node for inter-route move
                    // System.out.printf("Replaced node %d with %d.\n", replacedNode, bestJ);
                } else {
                    applyEdgeExchange(currentSolution, bestI, bestJ, moveVariant);
                }
                bestCost += bestDelta;
                improvement = true; // Continue searching for improvements
            }
        }

        return currentSolution;
    }

    /**
     * Generates candidate edges considering both distance and node attributes.
     */
    public Map<Integer, List<Integer>> generateCandidateEdges(double[][] distanceMatrix, double[][] nodes) {
        Map<Integer, List<Integer>> candidateEdges = new HashMap<>();
        int numNodes = nodes.length;

        for (int i = 0; i < numNodes; i++) {
            List<Integer> nearest = new ArrayList<>();
            for (int j = 0; j < numNodes; j++) {
                if (i != j) nearest.add(j);
            }
            int finalI = i;
            nearest.sort(Comparator.comparingDouble(j -> distanceMatrix[finalI][j] + nodes[j][2])); // Combined cost metric
            candidateEdges.put(i, nearest.subList(0, Math.min(CANDIDATE_COUNT, nearest.size())));
        }

        return candidateEdges;
    }

    private boolean isValidMove(int[][] move, int size) {
        return Arrays.stream(move)
                .flatMapToInt(Arrays::stream)
                .allMatch(index -> index >= 0 && index < size);
    }

    /**
     * Applies an edge exchange (or two-edge reversal) between two indices in the solution.
     */
    public static void applyEdgeExchange(List<Integer> solution, int[] i, int[] j, String moveVariant) {
        if (i[0] > j[0] || (i[0] == j[0] && i[1] > j[1])) {
            int[] temp = i;
            i = j;
            j = temp;
        }
        int left = solution.indexOf(i[1]) + 1;
        int right = solution.indexOf(j[0]);

        while (left < right) {
            Collections.swap(solution, left, right);
            left++;
            right--;
        }

    }



    /**
     * Calculates the delta for exchanging edges between two nodes.
     */

    public static double calculateEdgeExchangeDelta(List<Integer> solution, int[] i, int[] j, double[][] distanceMatrix, double[][] nodes) {
        int nodeA1 = solution.indexOf(i[0]);
        int nodeA2 = solution.indexOf(i[1]);
        int nodeB1 = solution.indexOf(j[0]);
        int nodeB2 = solution.indexOf(j[1]);

        double originalCost = distanceMatrix[i[0]][i[1]] + distanceMatrix[j[0]][j[1]];
        double newCost = distanceMatrix[i[0]][j[0]] + distanceMatrix[i[1]][j[1]];

        // No change in node costs for intra-route edge exchange
        return newCost - originalCost;
    }

    /**
     * Calculates the delta for inserting a new node (inter-route move).
     */
    public static double calculateNodeInsertionDelta(List<Integer> solution, int index, int newNode, double[][] distanceMatrix, double[][] nodes) {

        int prevNode = solution.get((index - 1 + solution.size()) % solution.size());
        int nextNode = solution.get(((index + 1) % solution.size()));
        //System.out.println(prevNode+" "+index+" "+nextNode);
        int currentNode = solution.get(index);



        double originalCost = distanceMatrix[prevNode][currentNode] + distanceMatrix[currentNode][nextNode] + nodes[currentNode][2];
        double newCost = distanceMatrix[prevNode][newNode] + distanceMatrix[newNode][nextNode] + nodes[newNode][2];

        return newCost - originalCost;
    }

    public static List<int[][]> getEdgeMoves(List<Integer> solution, int nodeIndex, int candidateIndex) {
        List<int[][]> moves = new ArrayList<>();
        int n = solution.size();

        //  (solution[nodeIndex], solution[(nodeIndex+1)%n]) and (solution[candidateIndex], solution[(candidateIndex+1)%n])
        int[][] move1 = {
                {solution.get(nodeIndex), solution.get((nodeIndex + 1) % n)},
                {solution.get(candidateIndex), solution.get((candidateIndex + 1) % n)}
        };
        moves.add(move1);

        //  (solution[nodeIndex-1], solution[nodeIndex]) and (solution[candidateIndex-1], solution[candidateIndex])
        int[][] move2 = {
                {solution.get((nodeIndex - 1 + n) % n), solution.get(nodeIndex)},
                {solution.get((candidateIndex - 1 + n) % n), solution.get(candidateIndex)}
        };
        moves.add(move2);

        return moves;
    }

    public static List<int[][]> getNodeMoves(List<Integer> solution, int nodeIndex, int candidateIndex) {
        List<int[][]> moves = new ArrayList<>();
        int n = solution.size();

        //  (solution[(node_index+1)%n], candidate)
        int[][] move1 = {
                {solution.get((nodeIndex + 1) % n), solution.get((nodeIndex + 1) % n)},
                {candidateIndex, candidateIndex}
        };
        moves.add(move1);

        //  (solution[node_index-1], candidate)
        int[][] move2 = {
                {solution.get((nodeIndex - 1 + n) % n), solution.get((nodeIndex - 1 + n) % n)},
                {candidateIndex, candidateIndex}
        };
        moves.add(move2);

        return moves;
    }
}
