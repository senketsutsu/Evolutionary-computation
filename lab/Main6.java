package lab;

import lab.tsp.*;
import lab.util.CSVReader;
import lab.util.DistanceMatrix;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main6 {
    public static void main(String[] args) throws IOException {
        // Experiment parameters
        double[][] nodes = CSVReader.readCSV("data/TSPB.csv");
        double[][] distanceMatrix = DistanceMatrix.calculateDistanceMatrix(nodes);
        int numNodes = nodes.length;
        String moveVariant = "two-edges";
        int numRuns = 20;

        // Results storage
        double minSteepestLocalSearch = Double.MAX_VALUE;
        double minMsls = Double.MAX_VALUE;
        double minIls = Double.MAX_VALUE;
        double minLnsWith = Double.MAX_VALUE;
        double minLnsWithout = Double.MAX_VALUE;

        List<Integer> SteepestLocalSearch_path = new ArrayList<>();
        List<Integer> Msls_path = new ArrayList<>();
        List<Integer> Ils_path = new ArrayList<>();
        List<Integer> LnsWith_path = new ArrayList<>();
        List<Integer> LnsWithout_path = new ArrayList<>();

        List<Double> baselineCosts = new ArrayList<>();
        List<Double> mslsCosts = new ArrayList<>();
        List<Double> ilsCosts = new ArrayList<>();
        List<Double> lnsWithCosts = new ArrayList<>();
        List<Double> lnsWithoutCosts = new ArrayList<>();

        long baselineTotalTime = 0;
        long mslsTotalTime = 0;
        long ilsTotalTime = 0;
        long lnsWithTotalTime = 0;
        long lnsWithoutTotalTime = 0;

        SteepestLocalSearch steepestLocalSearch = new SteepestLocalSearch();
        Msls msls = new Msls();
        Ils ils = new Ils();
        LnsWith lnsWith = new LnsWith();
        LnsWithout lnsWithout = new LnsWithout();
        SteepestLocalSearchWithCandidates steepestLocalSearchCandidate = new SteepestLocalSearchWithCandidates();

        Map<Integer, List<Integer>> candidateEdges = steepestLocalSearchCandidate.generateCandidateEdges(distanceMatrix, nodes);

        // Run experiments
        for (int run = 0; run < numRuns; run++) {
            System.out.printf("Run %d/%d...\n", run + 1, numRuns);

            // Generate a random initial solution
            List<Integer> initialSolution = RandomSolution.generateRandomSolution(numNodes);

            // Baseline Steepest Local Search
            long startTime = System.nanoTime();
            List<Integer> baselineSolution = steepestLocalSearch.optimize(initialSolution, distanceMatrix, nodes, "both", moveVariant);
            long endTime = System.nanoTime();
            double baselineCost = RandomSolution.calculateCost(baselineSolution, distanceMatrix, nodes);
            baselineCosts.add(baselineCost);
            baselineTotalTime += (endTime - startTime) / 1e6;
            if (baselineCost < minSteepestLocalSearch) {
                minSteepestLocalSearch = baselineCost;
                SteepestLocalSearch_path = baselineSolution;
            }

            // MSLS
            startTime = System.nanoTime();
            List<Integer> mslsSolution = msls.optimize(distanceMatrix, nodes, "both", moveVariant);
            endTime = System.nanoTime();
            double mslsCost = RandomSolution.calculateCost(mslsSolution, distanceMatrix, nodes);
            mslsCosts.add(mslsCost);
            mslsTotalTime += (endTime - startTime) / 1e6;
            if (mslsCost < minMsls) {
                minMsls = mslsCost;
                Msls_path = mslsSolution;
            }

            // ILS
            startTime = System.nanoTime();
            List<Integer> ilsSolution = ils.optimize(distanceMatrix, nodes, "both", moveVariant);
            endTime = System.nanoTime();
            double ilsCost = RandomSolution.calculateCost(ilsSolution, distanceMatrix, nodes);
            ilsCosts.add(ilsCost);
            ilsTotalTime += (endTime - startTime) / 1e6;
            if (ilsCost < minIls) {
                minIls = ilsCost;
                Ils_path = ilsSolution;
            }

            // LNS With Candidates
            startTime = System.nanoTime();
            List<Integer> lnsWithSolution = lnsWith.optimize(distanceMatrix, nodes, "both", moveVariant);
            endTime = System.nanoTime();
            double lnsWithCost = RandomSolution.calculateCost(lnsWithSolution, distanceMatrix, nodes);
            lnsWithCosts.add(lnsWithCost);
            lnsWithTotalTime += (endTime - startTime) / 1e6;
            if (lnsWithCost < minLnsWith) {
                minLnsWith = lnsWithCost;
                LnsWith_path = lnsWithSolution;
            }

            // LNS Without Candidates
            startTime = System.nanoTime();
            List<Integer> lnsWithoutSolution = lnsWithout.optimize(distanceMatrix, nodes, "both", moveVariant);
            endTime = System.nanoTime();
            double lnsWithoutCost = RandomSolution.calculateCost(lnsWithoutSolution, distanceMatrix, nodes);
            lnsWithoutCosts.add(lnsWithoutCost);
            lnsWithoutTotalTime += (endTime - startTime) / 1e6;
            if (lnsWithoutCost < minLnsWithout) {
                minLnsWithout = lnsWithoutCost;
                LnsWithout_path = lnsWithoutSolution;
            }
        }

        // Save best paths
        File bestPaths = new File("output/best_paths_local_search.csv");
        try (FileWriter writer = new FileWriter(bestPaths, true)) {
            writer.write("SteepestLocalSearch");
            for (int node : SteepestLocalSearch_path) writer.write("," + node);
            writer.write("\n");

            writer.write("Msls");
            for (int node : Msls_path) writer.write("," + node);
            writer.write("\n");

            writer.write("Ils");
            for (int node : Ils_path) writer.write("," + node);
            writer.write("\n");

            writer.write("LnsWith");
            for (int node : LnsWith_path) writer.write("," + node);
            writer.write("\n");

            writer.write("LnsWithout");
            for (int node : LnsWithout_path) writer.write("," + node);
            writer.write("\n");
        }

        // Print results
        System.out.println("\n=== Results ===");
        printResults("Baseline Steepest Local Search", baselineCosts, baselineTotalTime, numRuns);
        printResults("MSLS", mslsCosts, mslsTotalTime, numRuns);
        printResults("ILS", ilsCosts, ilsTotalTime, numRuns);
        printResults("LNS With Candidates", lnsWithCosts, lnsWithTotalTime, numRuns);
        printResults("LNS Without Candidates", lnsWithoutCosts, lnsWithoutTotalTime, numRuns);
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
}
