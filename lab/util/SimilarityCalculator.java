package lab.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimilarityCalculator {

    public static int calculateNodeSimilarity(List<Integer> solution1, List<Integer> solution2) {
        if (solution1.size() != solution2.size()) {
            throw new IllegalArgumentException("Solutions must have the same size.");
        }

        // Convert both solutions to sets to find the intersection
        Set<Integer> set1 = new HashSet<>(solution1);
        Set<Integer> set2 = new HashSet<>(solution2);

        // Calculate intersection of the two sets
        set1.retainAll(set2);

        // Return the similarity as a percentage
        return (int) ((set1.size() / (double) solution1.size()) * 100);
    }

    // Calculate Edge Similarity (set intersection of edges)
    public static int calculateEdgeSimilarity(List<Integer> solution1, List<Integer> solution2) {
        if (solution1.size() != solution2.size()) {
            throw new IllegalArgumentException("Solutions must have the same size.");
        }

        // Create sets of edges for both solutions
        Set<String> edges1 = new HashSet<>();
        Set<String> edges2 = new HashSet<>();
        int n = solution1.size();

        // Add edges for both solutions (including the wraparound edge)
        for (int i = 0; i < n; i++) {
            int node1 = solution1.get(i);
            int node2 = solution1.get((i + 1) % n);
            edges1.add(formatEdge(node1, node2));

            node1 = solution2.get(i);
            node2 = solution2.get((i + 1) % n);
            edges2.add(formatEdge(node1, node2));
        }

        // Retain only the common edges
        edges1.retainAll(edges2);

        // Return the edge similarity as a percentage
        return (int) ((edges1.size() / (double) n) * 100);
    }

    // Helper method to format edges as strings (sorted to ensure undirected)
    private static String formatEdge(int node1, int node2) {
        return node1 < node2 ? node1 + "," + node2 : node2 + "," + node1;
    }
}
