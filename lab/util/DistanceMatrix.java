package lab2.util;

public class DistanceMatrix {
    /**
     * Calculates the distance matrix from a given set of node coordinates.
     *
     * @param nodes A 2D array where each row represents a node with x, y coordinates.
     * @return A 2D array representing the distance matrix.
     */
    public static double[][] calculateDistanceMatrix(double[][] nodes) {
        int n = nodes.length; // Number of nodes
        double[][] distanceMatrix = new double[n][n]; // Initialize distance matrix

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    double dx = nodes[i][0] - nodes[j][0]; // x coordinates difference
                    double dy = nodes[i][1] - nodes[j][1]; // y coordinates difference
                    distanceMatrix[i][j] = Math.round(Math.sqrt(dx * dx + dy * dy)); // Euclidean distance rounded to nearest integer
                } else {
                    distanceMatrix[i][j] = 0; // Distance to self
                }
            }
        }
        return distanceMatrix;
    }
}
