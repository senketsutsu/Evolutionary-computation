package lab;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import lab.tsp.RandomSolution;
import lab.tsp.SteepestLocalSearch;
import lab.tsp.SteepestLocalSearchWithCandidates;
import lab.tsp.GreedyWeighted;
import lab.tsp.GreedyLocalSearch;
import lab.util.CSVReader;
import lab.util.DistanceMatrix;
import lab.util.LocalOptimaGeneration;
import lab.util.SimilarityCalculator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main_L8 {
    public static void main(String[] args) throws IOException {
        // Example CSV reading (replace with actual data)
        double[][] nodes = CSVReader.readCSV("data/TSPA.csv");

        double[][] distanceMatrix = DistanceMatrix.calculateDistanceMatrix(nodes);

        // Generate multiple local optima
        List<List<Integer>> localOptimaList = LocalOptimaGeneration.generateLocalOptima(1000, distanceMatrix, nodes, "two-edges");

        List<Integer> baselineSolution = findBaselineSolution(localOptimaList, distanceMatrix, nodes);


        List<Double> totalCosts = new ArrayList<>();
        List<Integer> nodeSimilaritiesToBest = new ArrayList<>();
        List<Integer> edgeSimilaritiesToBest = new ArrayList<>();
        List<Double> avgNodeSimilaritiesToAll = new ArrayList<>();
        List<Double> avgEdgeSimilaritiesToAll = new ArrayList<>();

        // Calculate fitness values (costs) and similarities
        List<Double> fitnessValues = new ArrayList<>();
        for (List<Integer> solution : localOptimaList) {
            double cost = RandomSolution.calculateCost(solution, distanceMatrix, nodes);
            fitnessValues.add(cost);
            totalCosts.add(cost);

            // Calculate the similarity to the best solution
            int nodeSimilarity = SimilarityCalculator.calculateNodeSimilarity(solution, baselineSolution);
            int edgeSimilarity = SimilarityCalculator.calculateEdgeSimilarity(solution, baselineSolution);
            nodeSimilaritiesToBest.add(nodeSimilarity);
            edgeSimilaritiesToBest.add(edgeSimilarity);

            // Calculate average similarities to all other solutions
            double avgNodeSim = calculateAverageSimilarity(solution, localOptimaList, "node");
            double avgEdgeSim = calculateAverageSimilarity(solution, localOptimaList, "edge");
            avgNodeSimilaritiesToAll.add(avgNodeSim);
            avgEdgeSimilaritiesToAll.add(avgEdgeSim);
        }

        // Save data to CSV
        saveDataToCSV(totalCosts, nodeSimilaritiesToBest, edgeSimilaritiesToBest, avgNodeSimilaritiesToAll, avgEdgeSimilaritiesToAll, "output/local_optima_similarityA.csv");
    }

    public static List<Integer> findBaselineSolution(List<List<Integer>> localOptimaList, double[][] distanceMatrix, double[][] nodes) {
        List<Integer> bestSolution = null;
        double bestCost = Double.MAX_VALUE;

        for (List<Integer> solution : localOptimaList) {
            double cost = RandomSolution.calculateCost(solution, distanceMatrix, nodes);
            if (cost < bestCost) {
                bestCost = cost;
                bestSolution = solution;
            }
        }

        System.out.println("Baseline solution found with cost: " + bestCost);
        return bestSolution;
    }

    public static void saveDataToCSV(List<Double> totalCosts, List<Integer> nodeSimilarities, List<Integer> edgeSimilarities, List<Double> avgNodeSimilarities, List<Double> avgEdgeSimilarities, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Write the header row
            writer.write("total_costs,Node similarity to best solution,Edge similarity to best solution,Average Node similarity to all other local optima,Average Edge similarity to all other local optima\n");

            // Write the data rows
            for (int i = 0; i < totalCosts.size(); i++) {
                writer.write(
                        totalCosts.get(i) + "," +
                                nodeSimilarities.get(i) + "," +
                                edgeSimilarities.get(i) + "," +
                                avgNodeSimilarities.get(i) + "," +
                                avgEdgeSimilarities.get(i) + "\n"
                );
            }

            System.out.println("Data saved to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double calculateAverageSimilarity(List<Integer> solution, List<List<Integer>> allSolutions, String similarityType) {
        double totalSimilarity = 0;
        int count = 0;

        for (List<Integer> otherSolution : allSolutions) {
            if (solution != otherSolution) {
                if (similarityType.equals("node")) {
                    totalSimilarity += SimilarityCalculator.calculateNodeSimilarity(solution, otherSolution);
                } else if (similarityType.equals("edge")) {
                    totalSimilarity += SimilarityCalculator.calculateEdgeSimilarity(solution, otherSolution);
                }
                count++;
            }
        }

        return count > 0 ? totalSimilarity / count : 0;
    }
}
