package lab.tsp;

import java.util.ArrayList;
import java.util.List;

public class GreedyCycle {

    /**
     * Generates cycle using a greedy algorithm based on the distance matrix (s.57).
     *
     * @param distanceMatrix 2D array representing the distances between nodes.
     * @param startNode     The index of the starting node for the cycle.
     * @param nodes The 2D array containing node coordinates and costs.
     * @return A list of node indices representing the Hamiltonian cycle.
     */
    public static List<Integer> greedyCycle(double[][] distanceMatrix, int startNode, double[][] nodes) {
        if (distanceMatrix == null || distanceMatrix.length == 0) {
            throw new IllegalArgumentException("Distance matrix cannot be null or empty.");
        }

        int n = distanceMatrix.length;
        boolean[] visited = new boolean[n];
        List<Integer> path = new ArrayList<>();
        path.add(startNode);
        visited[startNode] = true;
        int nodesToSelect = (int) Math.ceil(n / 2.0);

        // all vertices have been added (50%)
        while (path.size() < nodesToSelect) {
            int bestNode = -1;
            double bestIncrementalCost = Double.MAX_VALUE;
            int bestPosition = -1;

            // insert into the current cycle in the best possible place the vertex
            // causing the smallest increase in cycle length

            for (int position = 0; position <= path.size(); position++) {
                for (int candidateNode = 0; candidateNode < n; candidateNode++) {
                    if (!visited[candidateNode]) {
                        double incrementalCost = calculateIncrementalCost(path, distanceMatrix, candidateNode, position, nodes);
                        if (incrementalCost < bestIncrementalCost) {
                            bestIncrementalCost = incrementalCost;
                            bestNode = candidateNode;
                            bestPosition = position;
                            }
                    }
                }
            }

            if (bestNode >= 0 && bestPosition >= 0) {
                path.add(bestPosition, bestNode); // Insert node at the best position
                visited[bestNode] = true;
            }
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
     * @param nodes The 2D array containing node coordinates and costs.
     * @return The cost increase if the new node is added at the specified position.
     */
    private static double calculateIncrementalCost(List<Integer> path, double[][] distanceMatrix, int newNode, int position, double[][] nodes) {
        double cost = 0.0;
        int previousNode = position > 0 ? path.get(position - 1) : path.get(path.size() - 1);
        int nextNode = position < path.size() ? path.get(position) : path.get(0);

        cost += distanceMatrix[previousNode][newNode];
        cost += distanceMatrix[newNode][nextNode];
        cost += nodes[newNode][2];

        if (position > 0 || path.size() > 1) {   // change to also include the last (if last than only one edge)
            cost -= distanceMatrix[previousNode][nextNode];
        }

        return cost;
    }
}
