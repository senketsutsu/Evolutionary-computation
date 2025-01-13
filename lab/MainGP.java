package lab;
import lab.tsp.RandomSolution;
import lab.tsp.SteepestLocalSearchWithMoveEvaluations;
import lab.tsp.GPtsp;
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
import java.util.concurrent.*;

public class MainGP {
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        // Experiment parameters
        double[][] nodes = CSVReader.readCSV("data/TSPB.csv");
        double[][] distanceMatrix = DistanceMatrix.calculateDistanceMatrix(nodes);
        int numNodes = nodes.length;
        String moveVariant = "two-edges";
        int candidateCount = 10;
        int numRuns = 200;
        int nodesToSelect = (int) Math.ceil(numNodes / 2.0);


        GPtsp GPtsp = new GPtsp();
        ExecutorService executor = Executors.newFixedThreadPool(4);

        List<Callable<List<Integer>>> tasks = new ArrayList<>();
        for (int i = 0; i < numRuns; i++) {
            tasks.add(() -> GPtsp.main(nodes, distanceMatrix));
        }

        List<Future<List<Integer>>> results = executor.invokeAll(tasks);
        List<Double> costs = new ArrayList<>();
        List<List<Integer>> solutions = new ArrayList<>();
        long totalTime = 0;

        // Process results
        for (Future<List<Integer>> result : results) {
            long startTime = System.currentTimeMillis();
            List<Integer> best = result.get(); // Get the best solution from each run
            solutions.add(best); // Save all solutions
            long endTime = System.currentTimeMillis();

            // Calculate the cost of the solution
            double cost = RandomSolution.calculateCost(best, distanceMatrix, nodes);
            costs.add(cost);

            // Accumulate total runtime
            totalTime += (endTime - startTime);
        }

        // Find the best solution (minimum cost)
        double minCost = costs.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
        int bestIndex = costs.indexOf(minCost);
        List<Integer> bestSolution = solutions.get(bestIndex);

        // Print results to console
        printResults("Genetic Algorithm (GPtsp)", costs, totalTime, numRuns);

        // Save results to file
        saveResultsToFile("results.csv", costs, totalTime, numRuns, minCost, bestSolution);
        saveBestSolutionToFile("best_solution.txt", bestSolution, minCost);

        // Shut down executor
        executor.shutdown();

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

    private static void saveResultsToFile(String fileName, List<Double> costs, long totalTime, int numRuns, double minCost, List<Integer> bestSolution) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {
            // Write header
            writer.write("Run,Cost\n");

            // Write costs
            for (int i = 0; i < costs.size(); i++) {
                writer.write((i + 1) + "," + costs.get(i) + "\n");
            }

            // Write summary statistics
            writer.write("\nSummary Statistics:\n");
            double meanCost = costs.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double medianCost = calculateMedian(costs);
            double stdDevCost = calculateStandardDeviation(costs, meanCost);
            writer.write(String.format("Mean Cost: %.2f\n", meanCost));
            writer.write(String.format("Median Cost: %.2f\n", medianCost));
            writer.write(String.format("Min Cost: %.2f\n", minCost));
            writer.write(String.format("Standard Deviation: %.2f\n", stdDevCost));
            writer.write(String.format("Average Runtime: %.2f ms\n", (double) totalTime / numRuns));
        }
    }

    private static void saveBestSolutionToFile(String fileName, List<Integer> bestSolution, double minCost) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("Best Solution:\n");
            writer.write(bestSolution.toString() + "\n");
            writer.write(String.format("Cost: %.2f\n", minCost));
        }
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
}