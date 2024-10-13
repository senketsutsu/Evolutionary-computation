package lab1;

import lab1.tsp.RandomSolution;
import lab1.tsp.NearestNeighborEnd;
import lab1.tsp.NearestNeighborAnyPosition;
import lab1.tsp.GreedyCycle;
import lab1.util.CSVReader;
import lab1.util.DistanceMatrix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.*;
import java.io.File;
import java.io.FileWriter;

public class Main {
    public static void main(String[] args) throws IOException {
        double[][] nodes = CSVReader.readCSV("data/TSPA.csv");
        double[][] distanceMatrix = DistanceMatrix.calculateDistanceMatrix(nodes);
        int numNodes = nodes.length;

        // min, max, avg for each method
        double minRandom = Double.MAX_VALUE, maxRandom = Double.MIN_VALUE, avgRandom = 0;
        double minNNEnd = Double.MAX_VALUE, maxNNEnd = Double.MIN_VALUE, avgNNEnd = 0;
        double minNNAny = Double.MAX_VALUE, maxNNAny = Double.MIN_VALUE, avgNNAny = 0;
        double minGreedy = Double.MAX_VALUE, maxGreedy = Double.MIN_VALUE, avgGreedy = 0;

        int trials = 200;

        // 50% of total nodes, rounded up
        int nodesToSelect = (int) Math.ceil(numNodes / 2.0);

        // random solutions
        for (int i = 0; i < trials; i++) {
            List<Integer> randomSolution = RandomSolution.generateRandomSolution(numNodes);
            double cost = RandomSolution.calculateCost(randomSolution, distanceMatrix, nodes);
            minRandom = Math.min(minRandom, cost);
            maxRandom = Math.max(maxRandom, cost);
            avgRandom += cost;
        }
        avgRandom /= trials;

        // nearest neighbor solutions (end)
        for (int i = 0; i < trials; i++) {
            int startNode = (int) (Math.random() * numNodes);
            List<Integer> nnEndSolution = NearestNeighborEnd.nearestNeighborEnd(distanceMatrix, startNode, nodes);
            double cost = RandomSolution.calculateCost(nnEndSolution, distanceMatrix, nodes);
            minNNEnd = Math.min(minNNEnd, cost);
            maxNNEnd = Math.max(maxNNEnd, cost);
            avgNNEnd += cost;
        }
        avgNNEnd /= trials;

        // nearest neighbor solutions (any position)
        for (int i = 0; i < trials; i++) {
            int startNode = (int) (Math.random() * numNodes);
            List<Integer> nnAnySolution = NearestNeighborAnyPosition.nearestNeighborAnyPosition(distanceMatrix, startNode, nodes);
            double cost = RandomSolution.calculateCost(nnAnySolution, distanceMatrix, nodes);
            minNNAny = Math.min(minNNAny, cost);
            maxNNAny = Math.max(maxNNAny, cost);
            avgNNAny += cost;
        }
        avgNNAny /= trials;

        // greedy cycle
        for (int i = 0; i < trials; i++) {
            int startNode = (int) (Math.random() * numNodes);
            List<Integer> greedySolution = GreedyCycle.greedyCycle(distanceMatrix, startNode, nodes);
            double cost = RandomSolution.calculateCost(greedySolution, distanceMatrix, nodes);
            minGreedy = Math.min(minGreedy, cost);
            maxGreedy = Math.max(maxGreedy, cost);
            avgGreedy += cost;
        }
        avgGreedy /= trials;

        System.out.println("Random Solution - Min: " + minRandom + ", Max: " + maxRandom + ", Avg: " + avgRandom);
        System.out.println("Nearest Neighbor (End) - Min: " + minNNEnd + ", Max: " + maxNNEnd + ", Avg: " + avgNNEnd);
        System.out.println("Nearest Neighbor (Any Position) - Min: " + minNNAny + ", Max: " + maxNNAny + ", Avg: " + avgNNAny);
        System.out.println("Greedy Cycle - Min: " + minGreedy + ", Max: " + maxGreedy + ", Avg: " + avgGreedy);

        appendResultsToCSV(minRandom, maxRandom, avgRandom, minNNEnd, maxNNEnd, avgNNEnd,
                minNNAny, maxNNAny, avgNNAny, minGreedy, maxGreedy, avgGreedy);

        writeDistanceMatrixToCSV(distanceMatrix);
    }

    private static void appendResultsToCSV(double minRandom, double maxRandom, double avgRandom,
                                           double minNNEnd, double maxNNEnd, double avgNNEnd,
                                           double minNNAny, double maxNNAny, double avgNNAny,
                                           double minGreedy, double maxGreedy, double avgGreedy) throws IOException {
        File file = new File("output/results.csv");
        boolean fileExists = file.exists();

        try (FileWriter writer = new FileWriter(file, true)) {
            if (!fileExists) {
                writer.write("Min Random,Max Random,Avg Random,Min NN End,Max NN End,Avg NN End,Min NN Any,Max NN Any,Avg NN Any,Min Greedy,Max Greedy,Avg Greedy\n");
            }

            writer.write(minRandom + "," + maxRandom + "," + avgRandom + ","
                    + minNNEnd + "," + maxNNEnd + "," + avgNNEnd + ","
                    + minNNAny + "," + maxNNAny + "," + avgNNAny + ","
                    + minGreedy + "," + maxGreedy + "," + avgGreedy + "\n");
        }
    }

    private static void writeDistanceMatrixToCSV(double[][] distanceMatrix) throws IOException {
        File file = new File("output/distance_matrix.csv");

        try (FileWriter writer = new FileWriter(file)) {
            for (int i = 0; i < distanceMatrix.length; i++) {
                for (int j = 0; j < distanceMatrix[i].length; j++) {
                    writer.write(i + "," + j + "," + distanceMatrix[i][j] + "\n");
                }
            }
        }
    }
}