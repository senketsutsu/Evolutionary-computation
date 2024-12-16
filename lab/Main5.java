package lab;

import lab.tsp.RandomSolution;
import lab.tsp.SteepestLocalSearchWithMoveEvaluations; // New class
import lab.tsp.SteepestLocalSearchWithCandidates;
import lab.tsp.SteepestLocalSearch;
import lab.util.CSVReader;
import lab.util.DistanceMatrix;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Main5 {
    public static void main(String[] args) throws IOException {
        // Experiment parameters
        double[][] nodes = CSVReader.readCSV("data/TSPB.csv");
        double[][] distanceMatrix = DistanceMatrix.calculateDistanceMatrix(nodes);
        int numNodes = nodes.length;
        String moveVariant = "two-edges";
        int candidateCount = 10;
        int numRuns = 200;
        int nodesToSelect = (int) Math.ceil(numNodes / 2.0);

        // Results storage
        double minSteepestLocalSearch = Double.MAX_VALUE;
        double minsteepestLocalSearchCandidate = Double.MAX_VALUE;
        double minsteepestLocalSearchWithMoveEvaluations = Double.MAX_VALUE;
        List<Integer> SteepestLocalSearch_path = new ArrayList<>();
        List<Integer> steepestLocalSearchCandidate_path = new ArrayList<>();
        List<Integer> steepestLocalSearchWithMoveEvaluations_path = new ArrayList<>();
        List<Double> baselineCosts = new ArrayList<>();
        List<Double> candidateCosts = new ArrayList<>();
        List<Double> moveEvalCosts = new ArrayList<>(); // New list for SteepestLocalSearchWithMoveEvaluations
        long baselineTotalTime = 0;
        long candidateTotalTime = 0;
        long moveEvalTotalTime = 0; // New variable to track total time for move evaluations

        SteepestLocalSearch steepestLocalSearch = new SteepestLocalSearch();
        SteepestLocalSearchWithCandidates steepestLocalSearchCandidate = new SteepestLocalSearchWithCandidates();
        SteepestLocalSearchWithMoveEvaluations steepestLocalSearchWithMoveEvaluations = new SteepestLocalSearchWithMoveEvaluations(); // New instance
        Map<Integer, List<Integer>> candidateEdges = steepestLocalSearchCandidate.generateCandidateEdges(distanceMatrix, nodes);

        // Run experiments
        for (int run = 0; run < numRuns; run++) {
            System.out.printf("Run %d/%d...\n", run + 1, numRuns);

            // Generate a random initial solution
            List<Integer> initialSolution = RandomSolution.generateRandomSolution(numNodes);
            System.out.println("step1");

            // Baseline Steepest Local Search
            long startTime = System.nanoTime();
            List<Integer> baselineSolution = steepestLocalSearch.optimize(initialSolution, distanceMatrix, nodes, "both", moveVariant);
            long endTime = System.nanoTime();
            double baselineCost = RandomSolution.calculateCost(baselineSolution, distanceMatrix, nodes);
            baselineCosts.add(baselineCost);
            double elapsedTime = (endTime - startTime) / 1e6;
            baselineTotalTime += elapsedTime;
            minSteepestLocalSearch = Math.min(minSteepestLocalSearch, baselineCost);
            if (minSteepestLocalSearch == baselineCost) {
                SteepestLocalSearch_path = baselineSolution;
            }
            System.out.println("step2");

            /*
            // Steepest Local Search with Candidates
            startTime = System.nanoTime();
            List<Integer> candidateSolution = steepestLocalSearchCandidate.optimize(initialSolution, distanceMatrix, nodes, candidateEdges, moveVariant);
            endTime = System.nanoTime();
            double candidateCost = RandomSolution.calculateCost(candidateSolution, distanceMatrix, nodes);
            candidateCosts.add(candidateCost);
            elapsedTime = (endTime - startTime) / 1e6;
            candidateTotalTime += elapsedTime;
            minsteepestLocalSearchCandidate = Math.min(minsteepestLocalSearchCandidate, candidateCost);
            if (minsteepestLocalSearchCandidate == candidateCost) {
                steepestLocalSearchCandidate_path = baselineSolution;
            } */
            System.out.println("step3");

            // Steepest Local Search with Move Evaluations
            startTime = System.nanoTime();
            List<Integer> moveEvalSolution = steepestLocalSearchWithMoveEvaluations.optimize(initialSolution, distanceMatrix, nodes, moveVariant); // Updated call to the new method
            endTime = System.nanoTime();
            elapsedTime = (endTime - startTime) / 1e6;
            double moveEvalCost = RandomSolution.calculateCost(moveEvalSolution, distanceMatrix, nodes);
            moveEvalCosts.add(moveEvalCost);
            moveEvalTotalTime += elapsedTime;
            minsteepestLocalSearchWithMoveEvaluations = Math.min(minsteepestLocalSearchWithMoveEvaluations, moveEvalCost);
            if (minsteepestLocalSearchWithMoveEvaluations == moveEvalCost) {
                steepestLocalSearchWithMoveEvaluations_path = baselineSolution;
            }
        }

        File bestPaths = new File("output/best_paths_local_search.csv");
        try (FileWriter writer = new FileWriter(bestPaths, true)) {
            writer.write("SteepestLocalSearch");
            for (int node : SteepestLocalSearch_path) writer.write("," + node);
            writer.write("\n");

            writer.write("steepestLocalSearchCandidate");
            for (int node : steepestLocalSearchCandidate_path) writer.write("," + node);
            writer.write("\n");

            writer.write("steepestLocalSearchWithMoveEvaluations");
            for (int node : steepestLocalSearchWithMoveEvaluations_path) writer.write("," + node);
            writer.write("\n");}

        // Print results
        System.out.println("\n=== Results ===");
        printResults("Baseline Steepest Local Search", baselineCosts, baselineTotalTime, numRuns);
        //printResults("Steepest Local Search with Candidate Moves", candidateCosts, candidateTotalTime, numRuns);
        printResults("Steepest Local Search with Move Evaluations", moveEvalCosts, moveEvalTotalTime, numRuns); // New results
    }

    private static void printResults(String methodName, List<Double> costs, long totalTime, int numRuns) {
        double meanCost = costs.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double minCost = costs.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
        double maxCost = costs.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
        double medianCost = calculateMedian(costs);
        double stdDevCost = calculateStandardDeviation(costs, meanCost);

        System.out.println("\n" + methodName + ":");
        System.out.printf("Mean Cost: %.2f\n", meanCost);
        System.out.printf("Median Cost: %.2f\n", medianCost);
        System.out.printf("Min Cost: %.2f\n", minCost);
        System.out.printf("Max Cost: %.2f\n", maxCost);
        System.out.printf("Standard Deviation: %.2f\n", stdDevCost);
        System.out.printf("Average Runtime: %.2f ms\n", (double) totalTime / numRuns);
    }

    private static double calculateMedian(List<Double> values) {
        List<Double> sorted = new ArrayList<>(values);
        sorted.sort(Double::compareTo);
        int size = sorted.size();
        if (size % 2 == 0) {
            return (sorted.get(size / 2 - 1) + sorted.get(size / 2)) / 2.0;
        } else {
            return sorted.get(size / 2);
        }
    }

    private static double calculateStandardDeviation(List<Double> values, double mean) {
        double variance = values.stream().mapToDouble(v -> Math.pow(v - mean, 2)).sum() / values.size();
        return Math.sqrt(variance);
    }

    private static double[][] generateRandomDistanceMatrix(int numNodes) {
        Random random = new Random();
        double[][] distanceMatrix = new double[numNodes][numNodes];
        for (int i = 0; i < numNodes; i++) {
            for (int j = i + 1; j < numNodes; j++) {
                double distance = random.nextDouble() * 100;
                distanceMatrix[i][j] = distance;
                distanceMatrix[j][i] = distance;
            }
        }
        return distanceMatrix;
    }

    private static double[][] generateRandomNodes(int numNodes) {
        Random random = new Random();
        double[][] nodes = new double[numNodes][3];
        for (int i = 0; i < numNodes; i++) {
            nodes[i][0] = random.nextDouble() * 100; // x-coordinate
            nodes[i][1] = random.nextDouble() * 100; // y-coordinate
            nodes[i][2] = random.nextDouble() * 50;  // Node cost
        }
        return nodes;
    }
}
