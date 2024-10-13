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

public class Main {
    public static void main(String[] args) throws IOException {
        // Load node data from CSV
        double[][] nodes = CSVReader.readCSV("data/TSPA.csv");
        double[][] distanceMatrix = DistanceMatrix.calculateDistanceMatrix(nodes);
        int numNodes = nodes.length;

        // Store min, max, avg for each method
        double minRandom = Double.MAX_VALUE, maxRandom = Double.MIN_VALUE, avgRandom = 0;
        double minNNEnd = Double.MAX_VALUE, maxNNEnd = Double.MIN_VALUE, avgNNEnd = 0;
        double minNNAny = Double.MAX_VALUE, maxNNAny = Double.MIN_VALUE, avgNNAny = 0;
        double minGreedy = Double.MAX_VALUE, maxGreedy = Double.MIN_VALUE, avgGreedy = 0;

        int trials = 200;

        // Calculate how many nodes to select (50% of total nodes, rounded up)
        int nodesToSelect = (int) Math.ceil(numNodes / 2.0);

        // Generate 200 random solutions
        for (int i = 0; i < trials; i++) {
            List<Integer> randomSolution = RandomSolution.generateRandomSolution(nodesToSelect, numNodes);
            double cost = RandomSolution.calculateCost(randomSolution, distanceMatrix, nodes);
            minRandom = Math.min(minRandom, cost);
            maxRandom = Math.max(maxRandom, cost);
            avgRandom += cost;
        }
        avgRandom /= trials;

        // Generate 200 nearest neighbor solutions (end)
        for (int i = 0; i < trials; i++) {
            int startNode = (int) (Math.random() * numNodes);
            List<Integer> nnEndSolution = NearestNeighborEnd.nearestNeighborEnd(distanceMatrix, startNode, nodesToSelect);
            double cost = RandomSolution.calculateCost(nnEndSolution, distanceMatrix, nodes);
            minNNEnd = Math.min(minNNEnd, cost);
            maxNNEnd = Math.max(maxNNEnd, cost);
            avgNNEnd += cost;
        }
        avgNNEnd /= trials;

        // Generate 200 nearest neighbor solutions (any position)
        for (int i = 0; i < trials; i++) {
            int startNode = (int) (Math.random() * numNodes);
            List<Integer> nnAnySolution = NearestNeighborAnyPosition.nearestNeighborAnyPosition(distanceMatrix, startNode, nodesToSelect);
            double cost = RandomSolution.calculateCost(nnAnySolution, distanceMatrix, nodes);
            minNNAny = Math.min(minNNAny, cost);
            maxNNAny = Math.max(maxNNAny, cost);
            avgNNAny += cost;
        }
        avgNNAny /= trials;

        // Generate 200 greedy cycle solutions
        for (int i = 0; i < trials; i++) {
            int startNode = (int) (Math.random() * numNodes);
            List<Integer> greedySolution = GreedyCycle.greedyCycle(distanceMatrix, startNode, nodesToSelect);
            double cost = RandomSolution.calculateCost(greedySolution, distanceMatrix, nodes);
            minGreedy = Math.min(minGreedy, cost);
            maxGreedy = Math.max(maxGreedy, cost);
            avgGreedy += cost;
        }
        avgGreedy /= trials;

        // Output results
        System.out.println("Random Solution - Min: " + minRandom + ", Max: " + maxRandom + ", Avg: " + avgRandom);
        System.out.println("Nearest Neighbor (End) - Min: " + minNNEnd + ", Max: " + maxNNEnd + ", Avg: " + avgNNEnd);
        System.out.println("Nearest Neighbor (Any Position) - Min: " + minNNAny + ", Max: " + maxNNAny + ", Avg: " + avgNNAny);
        System.out.println("Greedy Cycle - Min: " + minGreedy + ", Max: " + maxGreedy + ", Avg: " + avgGreedy);
    }
}
