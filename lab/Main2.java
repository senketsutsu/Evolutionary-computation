package lab;

import lab.tsp.RandomSolution;
import lab.tsp.NearestNeighborEnd;
import lab.tsp.NearestNeighborAnyPosition;
import lab.tsp.GreedyCycle;
import lab.tsp.Greedy2Regret;
import lab.tsp.GreedyWeighted;
import lab.util.CSVReader;
import lab.util.DistanceMatrix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.*;
import java.io.File;
import java.io.FileWriter;

public class Main2 {
    public static void main(String[] args) throws IOException {
        double[][] nodes = CSVReader.readCSV("data/TSPA.csv");
        double[][] distanceMatrix = DistanceMatrix.calculateDistanceMatrix(nodes);
        int numNodes = nodes.length;

        // min, max, avg for each method
        double minRandom = Double.MAX_VALUE, maxRandom = Double.MIN_VALUE, avgRandom = 0;
        double minNNEnd = Double.MAX_VALUE, maxNNEnd = Double.MIN_VALUE, avgNNEnd = 0;
        double minNNAny = Double.MAX_VALUE, maxNNAny = Double.MIN_VALUE, avgNNAny = 0;
        double minGreedy = Double.MAX_VALUE, maxGreedy = Double.MIN_VALUE, avgGreedy = 0;
        double minGreedy2Regret = Double.MAX_VALUE, maxGreedy2Regret = Double.MIN_VALUE, avgGreedy2Regret = 0;
        double minGreedyWeighted = Double.MAX_VALUE, maxGreedyWeighted = Double.MIN_VALUE, avgGreedyWeighted = 0;

        int trials = 200;

        // 50% of total nodes, rounded up
        int nodesToSelect = (int) Math.ceil(numNodes / 2.0);

        File bestPaths = new File("output/best_paths.csv");
        List<Integer> bestPath = new ArrayList<>();

        // random solutions
        for (int i = 0; i < trials; i++) {
            List<Integer> randomSolution = RandomSolution.generateRandomSolution(numNodes);
            double cost = RandomSolution.calculateCost(randomSolution, distanceMatrix, nodes);
            minRandom = Math.min(minRandom, cost);
            maxRandom = Math.max(maxRandom, cost);
            avgRandom += cost;
            if( minRandom == cost ){
                bestPath = randomSolution;
            }
        }

        try (FileWriter writer = new FileWriter(bestPaths, true)) {
            writer.write("Random");
            for (int j = 0; j < bestPath.size(); j++) {
                writer.write("," + bestPath.get(j));
            }
            writer.write("\n");
        }

        avgRandom /= trials;

        List<Integer> bestPathNN = new ArrayList<>();
        List<Integer> bestPathNNany = new ArrayList<>();
        List<Integer> bestPathGreedy = new ArrayList<>();
        List<Integer> bestPathGreedy2Regret = new ArrayList<>();
        List<Integer> bestPathGreedyWeighted = new ArrayList<>();

        // nearest neighbor solutions (end)
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < numNodes; j++) {

                int startNode = (int) (j);
                List<Integer> nnEndSolution = NearestNeighborEnd.nearestNeighborEnd(distanceMatrix, startNode, nodes);
                double cost = RandomSolution.calculateCost(nnEndSolution, distanceMatrix, nodes);
                minNNEnd = Math.min(minNNEnd, cost);
                maxNNEnd = Math.max(maxNNEnd, cost);
                avgNNEnd += cost;

                if( minNNEnd == cost ){
                    bestPathNN = nnEndSolution;
                }

                // }
                // avgNNEnd /= trials;

                // nearest neighbor solutions (any position)
                // for (int i = 0; i < trials; i++) {
                // int startNode = (int) (Math.random() * numNodes);
                List<Integer> nnAnySolution = NearestNeighborAnyPosition.nearestNeighborAnyPosition(distanceMatrix, startNode, nodes);
                cost = RandomSolution.calculateCost(nnAnySolution, distanceMatrix, nodes);
                minNNAny = Math.min(minNNAny, cost);
                maxNNAny = Math.max(maxNNAny, cost);
                avgNNAny += cost;

                if( minNNAny == cost ){
                    bestPathNNany = nnAnySolution;
                }

                // }
                // avgNNAny /= trials;

                // greedy cycle
                // for (int i = 0; i < trials; i++) {
                // int startNode = (int) (Math.random() * numNodes);
                List<Integer> greedySolution = GreedyCycle.greedyCycle(distanceMatrix, startNode, nodes);
                cost = RandomSolution.calculateCost(greedySolution, distanceMatrix, nodes);
                minGreedy = Math.min(minGreedy, cost);
                maxGreedy = Math.max(maxGreedy, cost);
                avgGreedy += cost;

                if( minGreedy == cost ){
                    bestPathGreedy = greedySolution;
                }

                // greedy 2 regret

                List<Integer> greedy2RegretSolution = Greedy2Regret.greedy2Regret(distanceMatrix, startNode, nodes);
                cost = RandomSolution.calculateCost(greedy2RegretSolution, distanceMatrix, nodes);
                minGreedy2Regret = Math.min(minGreedy2Regret, cost);
                maxGreedy2Regret = Math.max(maxGreedy2Regret, cost);
                avgGreedy2Regret += cost;

                if( minGreedy2Regret == cost ){
                    bestPathGreedy2Regret = greedy2RegretSolution;
                }

                // greedy 2 regret weighted

                List<Integer> greedyWeightedSolution = GreedyWeighted.greedyWeighted(distanceMatrix, startNode, nodes);
                cost = RandomSolution.calculateCost(greedyWeightedSolution, distanceMatrix, nodes);
                minGreedyWeighted = Math.min(minGreedyWeighted, cost);
                maxGreedyWeighted = Math.max(maxGreedyWeighted, cost);
                avgGreedyWeighted += cost;

                if( minGreedyWeighted == cost ){
                    bestPathGreedyWeighted = greedyWeightedSolution;
                }
            }
        }
        avgGreedy /= (1 * numNodes);
        avgNNEnd /= (1 * numNodes);
        avgNNAny /= (1 * numNodes);
        avgGreedy2Regret /= (1 * numNodes);
        avgGreedyWeighted /= (1 * numNodes);

        try (FileWriter writer = new FileWriter(bestPaths, true)) {
            writer.write("NN End");
            for (int j = 0; j < bestPathNN.size(); j++) {
                writer.write("," + bestPathNN.get(j));
            }
            writer.write("\n");

            writer.write("NN Any");
            for (int j = 0; j < bestPathNNany.size(); j++) {
                writer.write("," + bestPathNNany.get(j));
            }
            writer.write("\n");

            writer.write("Greedy");
            for (int j = 0; j < bestPathGreedy.size(); j++) {
                writer.write("," + bestPathGreedy.get(j));
            }
            writer.write("\n");

            writer.write("Greedy2Regret");
            for (int j = 0; j < bestPathGreedy2Regret.size(); j++) {
                writer.write("," + bestPathGreedy2Regret.get(j));
            }
            writer.write("\n");

            writer.write("GreedyWeighted");
            for (int j = 0; j < bestPathGreedyWeighted.size(); j++) {
                writer.write("," + bestPathGreedyWeighted.get(j));
            }
            writer.write("\n");
        }

        System.out.println("Random Solution - Min: " + minRandom + ", Max: " + maxRandom + ", Avg: " + avgRandom);
        System.out.println("Nearest Neighbor (End) - Min: " + minNNEnd + ", Max: " + maxNNEnd + ", Avg: " + avgNNEnd);
        System.out.println("Nearest Neighbor (Any Position) - Min: " + minNNAny + ", Max: " + maxNNAny + ", Avg: " + avgNNAny);
        System.out.println("Greedy Cycle - Min: " + minGreedy + ", Max: " + maxGreedy + ", Avg: " + avgGreedy);
        System.out.println("Greedy 2 Regret - Min: " + minGreedy2Regret + ", Max: " + maxGreedy2Regret + ", Avg: " + avgGreedy2Regret);
        System.out.println("Greedy Weighted - Min: " + minGreedyWeighted + ", Max: " + maxGreedyWeighted + ", Avg: " + avgGreedyWeighted);

        appendResultsToCSV(minRandom, maxRandom, avgRandom, minNNEnd, maxNNEnd, avgNNEnd,
                minNNAny, maxNNAny, avgNNAny, minGreedy, maxGreedy, avgGreedy,
                minGreedy2Regret, maxGreedy2Regret, avgGreedy2Regret,
                minGreedyWeighted, maxGreedyWeighted, avgGreedyWeighted);

        writeDistanceMatrixToCSV(distanceMatrix);
    }

    private static void appendResultsToCSV(double minRandom, double maxRandom, double avgRandom,
                                           double minNNEnd, double maxNNEnd, double avgNNEnd,
                                           double minNNAny, double maxNNAny, double avgNNAny,
                                           double minGreedy, double maxGreedy, double avgGreedy,
                                           double minGreedy2Regret, double maxGreedy2Regret, double avgGreedy2Regret,
                                           double minGreedyWeighted, double maxGreedyWeighted, double avgGreedyWeighted) throws IOException {
        File file = new File("output/results.csv");
        boolean fileExists = file.exists();

        try (FileWriter writer = new FileWriter(file, true)) {
            if (!fileExists) {
                writer.write("Min Random,Max Random,Avg Random,Min NN End,Max NN End,Avg NN End,Min NN Any,Max NN Any,Avg NN Any,Min Greedy,Max Greedy,Avg Greedy,Min Greedy2Regret,Max Greedy2Regret,Avg Greedy2Regret,Min GreedyWeighted,Max GreedyWeighted,Avg GreedyWeighted\n");
            }

            writer.write(minRandom + "," + maxRandom + "," + avgRandom + ","
                    + minNNEnd + "," + maxNNEnd + "," + avgNNEnd + ","
                    + minNNAny + "," + maxNNAny + "," + avgNNAny + ","
                    + minGreedy + "," + maxGreedy + "," + avgGreedy + ","
                    + minGreedy2Regret + "," + maxGreedy2Regret + "," + avgGreedy2Regret + ","
                    + minGreedyWeighted + "," + maxGreedyWeighted + "," + avgGreedyWeighted + "\n");
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
