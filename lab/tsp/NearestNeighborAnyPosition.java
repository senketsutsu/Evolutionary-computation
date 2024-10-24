package lab.tsp;

import lab.tsp.RandomSolution;

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
    public static double calculateIncrementalCost(List<Integer> path, double[][] distanceMatrix, int newNode, int position, double[][] nodes) {
        int pathSize = path.size();

        // If the node is inserted at the end of the path, including the edge back to the start node
        if (position == pathSize) {
            int lastNode = path.get(pathSize - 1);
            int firstNode = path.get(0);
            double costNewNode = nodes[newNode][2];

            // (lastNode -> newNode -> firstNode) minus (lastNode -> firstNode)
            return (distanceMatrix[lastNode][newNode] + costNewNode + distanceMatrix[newNode][firstNode] - distanceMatrix[lastNode][firstNode]);
        }

        // If the node is inserted at the beginning of the path
        if (position == 0) {
            int lastNode = path.get(pathSize - 1);
            int firstNode = path.get(0);
            double costNewNode = nodes[newNode][2];

            // (lastNode -> newNode -> firstNode) minus (lastNode -> firstNode)
            return (distanceMatrix[lastNode][newNode] + costNewNode + distanceMatrix[newNode][firstNode] - distanceMatrix[lastNode][firstNode]);
        }

        // If the node is inserted in between two existing nodes in the path
        int prevNode = path.get(position - 1);
        int nextNode = path.get(position);
        double costNewNode = nodes[newNode][2];

        return (distanceMatrix[prevNode][newNode] + costNewNode + distanceMatrix[newNode][nextNode] - distanceMatrix[prevNode][nextNode]);
    }
}

