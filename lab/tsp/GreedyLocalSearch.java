package lab.tsp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GreedyLocalSearch {

    public List<Integer> optimize(List<Integer> initialSolution, double[][] distanceMatrix, double[][] nodes, String moveType, String moveVariant) {
        List<Integer> currentSolution = new ArrayList<>(initialSolution);
        double currentCost = RandomSolution.calculateCost(currentSolution, distanceMatrix, nodes);
        boolean improvement = true;
        int iterationCount = 0;
        int maxIterations = 20000000;
        Random random = new Random();

        while (improvement){ //&& iterationCount < maxIterations) {
            improvement = false;
            int binary = random.nextInt(2);
            if(binary == 0){
                moveType = "intra";
            }else{
                moveType = "inter";
            }

            // Generate possible intra and inter moves based on the specified moveType
            List<int[]> intraMoves = moveType.equals("intra") || moveType.equals("both") ? generateIntraMoves(currentSolution.size()) : Collections.emptyList();
            List<int[]> interMoves = moveType.equals("inter") || moveType.equals("both") ? generateInterMoves(currentSolution, nodes.length) : Collections.emptyList();

            // Shuffle the moves to ensure randomness
            Collections.shuffle(intraMoves);
            Collections.shuffle(interMoves);

            // TODO: Add random binary if that selects which move is done

            // Check for intra-route improvements first
            for (int[] move : intraMoves) {
                double delta;
                // Determine the delta based on the type of intra-move
                if (moveVariant.equals("two-nodes")) {
                    delta = SteepestLocalSearch.calculateTwoNodesExchangeDelta(currentSolution, move[0], move[1], distanceMatrix);
                } else {
                    delta = SteepestLocalSearch.calculateTwoEdgesExchangeDelta(currentSolution, move[0], move[1], distanceMatrix);
                }
                // If the move improves the solution, apply it
                if (delta < 0) {
                    applyIntraMove(currentSolution, move[0], move[1], moveVariant);
                    currentCost += delta;
                    improvement = true;
                    break; // Break to restart the search after applying an improvement
                }
            }

            // If no intra-route improvement was found, check for inter-route improvements
            if (!improvement) {
                for (int[] move : interMoves) {
                    double delta = SteepestLocalSearch.calculateSingleNodeChangeDelta(currentSolution, move[0], move[1], distanceMatrix, nodes);
                    // If the inter-route move improves the solution, apply it
                    if (delta < 0) {
                        currentSolution.set(move[0], move[1]);
                        currentCost += delta;
                        improvement = true;
                        break; // Break to restart the search after applying an improvement
                    }
                }
            }
            iterationCount++;
        }
        return currentSolution; // Return the optimized solution
    }

    private void applyIntraMove(List<Integer> solution, int i, int j, String moveVariant) {
        if (moveVariant.equals("two-nodes")) {
            // Swap the two nodes directly
            Collections.swap(solution, i, j);
        } else if (moveVariant.equals("two-edges")) {
            // Ensure i is less than j for swapping
            if (i > j) {
                int temp = i;
                i = j;
                j = temp;
            }

            // Reverse the segment of the list between indices i and j
            int left = i + 1; // start right after i
            int right = j;    // end at j

            while (left < right) {
                Collections.swap(solution, left, right);
                left++;
                right--;
            }
        }
    }

    public static List<int[]> generateIntraMoves(int numSelectedNodes) {
        List<int[]> intraMoves = new ArrayList<>();
        // Generate all combinations of intra-move pairs
        for (int i = 0; i < numSelectedNodes; i++) {
            for (int j = i + 1; j < numSelectedNodes; j++) {
                intraMoves.add(new int[]{i, j});
            }
        }
        return intraMoves;
    }

    public static List<int[]> generateInterMoves(List<Integer> selectedNodes, int totalNodes) {
        List<int[]> interMoves = new ArrayList<>();
        // Get unselected nodes to generate inter-moves
        List<Integer> unselectedNodes = SteepestLocalSearch.getUnselectedNodes(selectedNodes, totalNodes);
        for (int i = 0; i < selectedNodes.size(); i++) {
            for (int node : unselectedNodes) {
                interMoves.add(new int[]{i, node});
            }
        }
        return interMoves;
    }
}
