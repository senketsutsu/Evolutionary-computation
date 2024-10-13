package lab1.tsp;

import java.util.ArrayList;
import java.util.List;

public class GreedyCycle {

    /**
     * Generates a Hamiltonian cycle using a greedy algorithm based on the distance matrix.
     *
     * @param distanceMatrix 2D array representing the distances between nodes.
     * @param startNode     The index of the starting node for the cycle.
     * @return A list of node indices representing the Hamiltonian cycle.
     */
    public static List<Integer> greedyCycle(double[][] distanceMatrix, int startNode) {
        if (distanceMatrix == null || distanceMatrix.length == 0) {
            throw new IllegalArgumentException("Distance matrix cannot be null or empty.");
        }

        int n = distanceMatrix.length;
        boolean[] visited = new boolean[n];
        List<Integer> path = new ArrayList<>();
        path.add(startNode);
        visited[startNode] = true;

        while (path.size() < n) {
            int bestNode = -1;
            double bestIncrementalCost = Double.MAX_VALUE;
            int bestPosition = -1;

            // Find the best node to add to the path
            for (int candidateNode = 0; candidateNode < n; candidateNode++) {
                if (!visited[candidateNode]) {
                    // Check for each position in the current path
                    for (int position = 0; position <= path.size(); position++) {
                        double incrementalCost = calculateIncrementalCost(path, distanceMatrix, candidateNode, position);
                        if (incrementalCost < bestIncrementalCost) {
                            bestIncrementalCost = incrementalCost;
                            bestNode = candidateNode;
                            bestPosition = position;
                        }
                    }
                }
            }

            // Add the best node to the path at the best position
            path.add(bestPosition, bestNode);
            visited[bestNode] = true;
        }

        return path;
    }

    /**
     * Calculates the incremental cost of inserting a node into the current path.
     *
     * @param path           The current path.
     * @param distanceMatrix The distance matrix.
     * @param newNode       The node to be added.
     * @param position      The position in the path where the node will be added.
     * @return The cost increase if the new node is added at the specified position.
     */
    private static double calculateIncrementalCost(List<Integer> path, double[][] distanceMatrix, int newNode, int position) {
        double cost = 0.0;
        int previousNode = position > 0 ? path.get(position - 1) : newNode; // Previous node in the path
        int nextNode = position < path.size() ? path.get(position) : newNode; // Next node in the path

        // Calculate the cost of adding the new node
        cost += distanceMatrix[previousNode][newNode]; // Cost from previous to new node
        cost += distanceMatrix[newNode][nextNode];     // Cost from new node to next node

        // Subtract the cost of the edges that will be removed
        if (position > 0) {
            cost -= distanceMatrix[previousNode][nextNode]; // Remove cost between previous and next node
        }

        return cost;
    }
}
