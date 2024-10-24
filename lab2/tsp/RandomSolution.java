package lab.tsp;

import java.util.*;

public class RandomSolution {
    
    /**
     * Generates a random permutation of node indices for a Hamiltonian cycle.
     *
     * @param n The total number of nodes.
     * @return A list containing a random order of node indices.
     */
    public static List<Integer> generateRandomSolution(int n) {
        List<Integer> nodes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            nodes.add(i);
        }
        int nodesToSelect = (int) Math.ceil(n / 2.0);
        Collections.shuffle(nodes, new Random()); // Shuffle nodes to generate a random order
        return nodes.subList(0, nodesToSelect);
    }

    /**
     * Calculates the total cost of a given solution based on the distance matrix.
     *
     * @param solution The list of node indices representing the Hamiltonian cycle.
     * @param distanceMatrix The 2D array representing distances between nodes.
     * @param nodeData The 2D array containing node coordinates and costs.
     * @return The total cost of the Hamiltonian cycle.
     */
    public static double calculateCost(List<Integer> solution, double[][] distanceMatrix, double[][] nodeData) {
        if (solution == null || distanceMatrix == null || distanceMatrix.length == 0) {
            throw new IllegalArgumentException("Solution or distance matrix cannot be null or empty.");
        }
        if (solution.size() * 2 < distanceMatrix.length) {
            throw new IllegalArgumentException("Solution size must match the number of nodes in the distance matrix.");
        }

        double cost = 0;
        for (int i = 0; i < solution.size() - 1; i++) {
            int currentNode = solution.get(i);
            int nextNode = solution.get(i + 1);
            cost += distanceMatrix[currentNode][nextNode];
            cost += nodeData[currentNode][2];
        }

        int lastNode = solution.get(solution.size() - 1);
        int startNode = solution.get(0);
        cost += distanceMatrix[lastNode][startNode];
        cost += nodeData[lastNode][2];

        return cost;
    }
}
