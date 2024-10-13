package lab1.tsp;

import java.util.ArrayList;
import java.util.List;

public class NearestNeighborEnd {

    /**
     * Finds a Hamiltonian path using a nearest neighbor approach, starting from the specified node.
     *
     * @param distanceMatrix The 2D array representing the distances between nodes.
     * @param startNode     The index of the starting node for the path.
     * @return A list of node indices representing the Hamiltonian path.
     */
    public static List<Integer> nearestNeighborEnd(double[][] distanceMatrix, int startNode) {
        if (distanceMatrix == null || distanceMatrix.length == 0) {
            throw new IllegalArgumentException("Distance matrix cannot be null or empty.");
        }

        int n = distanceMatrix.length;
        boolean[] visited = new boolean[n];
        List<Integer> path = new ArrayList<>();
        path.add(startNode);
        visited[startNode] = true;

        for (int i = 1; i < n; i++) {
            int lastNode = path.get(path.size() - 1);
            int nearestNode = -1;
            double minDistance = Double.MAX_VALUE;

            // Find the nearest unvisited node
            for (int candidateNode = 0; candidateNode < n; candidateNode++) {
                if (!visited[candidateNode] && distanceMatrix[lastNode][candidateNode] < minDistance) {
                    minDistance = distanceMatrix[lastNode][candidateNode];
                    nearestNode = candidateNode;
                }
            }

            // If a nearest node was found, add it to the path
            if (nearestNode != -1) {
                path.add(nearestNode);
                visited[nearestNode] = true;
            }
        }

        // Optional: Return to the starting node to complete the cycle
        // path.add(startNode);

        return path;
    }
}
