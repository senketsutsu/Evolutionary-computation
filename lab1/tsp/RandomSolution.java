package lab1.tsp;

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
        Collections.shuffle(nodes, new Random()); // Shuffle nodes to generate a random order
        return nodes;
    }

    /**
     * Calculates the total cost of a given solution based on the distance matrix.
     *
     * @param solution The list of node indices representing the Hamiltonian cycle.
     * @param distanceMatrix The 2D array representing distances between nodes.
     * @return The total cost of the Hamiltonian cycle.
     */
    public static double calculateCost(List<Integer> solution, double[][] distanceMatrix) {
        if (solution == null || distanceMatrix == null || distanceMatrix.length == 0) {
            throw new IllegalArgumentException("Solution or distance matrix cannot be null or empty.");
        }
        if (solution.size() != distanceMatrix.length) {
            throw new IllegalArgumentException("Solution size must match the number of nodes in the distance matrix.");
        }

        double cost = 0;
        for (int i = 0; i < solution.size() - 1; i++) {
            cost += distanceMatrix[solution.get(i)][solution.get(i + 1)];
        }
        // Add cost to return to the starting node to complete the cycle
        cost += distanceMatrix[solution.get(solution.size() - 1)][solution.get(0)];
        return cost;
    }
}
