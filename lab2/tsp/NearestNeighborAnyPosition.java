package lab.tsp;

import java.util.ArrayList;
import java.util.List;

public class NearestNeighborAnyPosition {

    /**
     * Finds path using a nearest neighbor approach, allowing insertion at any position in the path (s.47).
     *
     * @param distanceMatrix The 2D array representing the distances between nodes.
     * @param startNode     The index of the starting node for the path.
     * @param nodes The 2D array containing node coordinates and costs.
     * @return A list of node indices representing the Hamiltonian path.
     */
    public static List<Integer> nearestNeighborAnyPosition(double[][] distanceMatrix, int startNode, double[][] nodes) {
        if (distanceMatrix == null || distanceMatrix.length == 0) {
            throw new IllegalArgumentException("Distance matrix cannot be null or empty.");
        }

        int n = distanceMatrix.length;
        boolean[] visited = new boolean[n];
        List<Integer> path = new ArrayList<>();
        path.add(startNode);
        visited[startNode] = true;
        int nodesToSelect = (int) Math.ceil(n / 2.0);

        for (int i = 0; i < nodesToSelect - 1; i++) {
            int bestNode = -1;
            double bestIncrementalCost = Double.MAX_VALUE;
            int bestPosition = -1;

            // add to the solution the vertex (and the leading edge) closest to any
            for (int candidateNode = 0; candidateNode < n; candidateNode++) {
                if (!visited[candidateNode]) {
                    for (int position = 0; position <= path.size(); position++) {
                        double incrementalCost = calculateIncrementalCost(path, distanceMatrix, candidateNode, position, nodes);
                        if (incrementalCost < bestIncrementalCost) {
                            bestIncrementalCost = incrementalCost;
                            bestNode = candidateNode;
                            bestPosition = position;
                        }
                    }
                }
            }

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

        if (position > 0) {
            cost -= distanceMatrix[previousNode][nextNode];
        }

        return cost;
    }
}
