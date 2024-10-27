package lab.tsp;

import lab.tsp.SteepestLocalSearch;
import lab.tsp.RandomSolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GreedyLocalSearch {

    private static final Random random = new Random();

    public List<Integer> optimize(List<Integer> initialSolution, double[][] distanceMatrix, double[][] nodes, String moveType) {
        List<Integer> currentSolution = new ArrayList<>(initialSolution);
        double currentCost = RandomSolution.calculateCost(currentSolution, distanceMatrix, nodes);
        boolean improvement = true;

        while (improvement) {
            improvement = false;

            List<int[]> intraMoves = moveType.equals("intra") || moveType.equals("both") ? generateIntraMoves(currentSolution.size()) : Collections.emptyList();
            List<int[]> interMoves = moveType.equals("inter") || moveType.equals("both") ? generateInterMoves(currentSolution, nodes.length) : Collections.emptyList();

            Collections.shuffle(intraMoves);
            Collections.shuffle(interMoves);

            for (int[] move : intraMoves) {
                double delta = SteepestLocalSearch.calculateTwoNodesExchangeDelta(currentSolution, move[0], move[1], distanceMatrix);
                if (delta < 0) {
                    Collections.swap(currentSolution, move[0], move[1]);
                    currentCost += delta;
                    improvement = true;
                    break;
                }
            }

            // Apply the first improving inter-route move if no intra-route improvement was found
            if (!improvement) {
                for (int[] move : interMoves) {
                    double delta = SteepestLocalSearch.calculateSingleNodeChangeDelta(currentSolution, move[0], move[1], distanceMatrix, nodes);
                    if (delta < 0) {
                        int replacedNode = currentSolution.get(move[0]);
                        currentSolution.set(move[0], move[1]);
                        currentCost += delta;
                        improvement = true;
                        break;
                    }
                }
            }
        }
        return currentSolution;
    }

    public static List<int[]> generateIntraMoves(int numSelectedNodes) {
        List<int[]> intraMoves = new ArrayList<>();
        for (int i = 0; i < numSelectedNodes; i++) {
            for (int j = i + 1; j < numSelectedNodes; j++) {
                intraMoves.add(new int[]{i, j});
            }
        }
        return intraMoves;
    }

    public static List<int[]> generateInterMoves(List<Integer> selectedNodes, int totalNodes) {
        List<int[]> interMoves = new ArrayList<>();
        List<Integer> unselectedNodes = SteepestLocalSearch.getUnselectedNodes(selectedNodes, totalNodes);
        for (int i = 0; i < selectedNodes.size(); i++) {
            for (int node : unselectedNodes) {
                interMoves.add(new int[]{i, node});
            }
        }
        return interMoves;
    }

}
