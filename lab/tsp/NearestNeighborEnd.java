package lab.tsp;

import java.util.ArrayList;
import java.util.List;

public class NearestNeighborEnd {

    /**
     * Finds path using a nearest neighbor approach, starting from the specified node (s.47).
     *
     * @param distanceMatrix The 2D array representing the distances between nodes.
     * @param startNode     The index of the starting node for the path.
     * @param nodes The 2D array containing node coordinates and costs.
     * @return A list of node indices representing the Hamiltonian path.
     */
    public static List<Integer> nearestNeighborEnd(double[][] distanceMatrix, int startNode, double[][] nodes) {
        if (distanceMatrix == null || distanceMatrix.length == 0) {
            throw new IllegalArgumentException("Distance matrix cannot be null or empty.");
        }

        int n = distanceMatrix.length;
        boolean[] visited = new boolean[n];
        int nodesToSelect = (int) Math.ceil(n / 2.0);
        List<Integer> path = new ArrayList<>();
        path.add(startNode);
        visited[startNode] = true;

        for (int i = 0; i < nodesToSelect - 1; i++) {
            int lastNode = path.get(path.size() - 1);
            int nearestNode = -1;
            double minDistance = Double.MAX_VALUE;

            // add to the solution the vertex (and the leading edge) closest to
            // the last one added
            for (int candidateNode = 0; candidateNode < n; candidateNode++) {
                if (!visited[candidateNode] && distanceMatrix[lastNode][candidateNode] + nodes[candidateNode][2] < minDistance) {
                    minDistance = distanceMatrix[lastNode][candidateNode] + nodes[candidateNode][2];
                    nearestNode = candidateNode;
                }
            }

            if (nearestNode != -1) {
                path.add(nearestNode);
                visited[nearestNode] = true;
            }
        }

        return path;
    }
}
