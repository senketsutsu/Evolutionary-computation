package lab;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lab.tsp.RandomSolution;
import lab.tsp.SteepestLocalSearch;
import lab.tsp.GreedyWeighted;
import lab.tsp.GreedyLocalSearch;
import lab.tsp.CandidateMoves;
import lab.util.CSVReader;
import lab.util.DistanceMatrix;
import lab.tsp.SteepestLocalSearchWithMoveEvaluations; // New class
import lab.tsp.SteepestLocalSearchWithCandidates;

public class Main3_time {
    public static void main(String[] args) throws IOException {
        double[][] nodes = CSVReader.readCSV("data/TSPB.csv");
        double[][] distanceMatrix = DistanceMatrix.calculateDistanceMatrix(nodes);
        int numNodes = nodes.length;

        // min, max, avg for each method
        double minRandom = Double.MAX_VALUE, maxRandom = Double.MIN_VALUE, avgRandom = 0;
        double minSteepestLocal_intra = Double.MAX_VALUE, maxSteepestLocal_intra = Double.MIN_VALUE, avgSteepestLocal_intra = 0;
        double minGreedyLocal_intra = Double.MAX_VALUE, maxGreedyLocal_intra = Double.MIN_VALUE, avgGreedyLocal_intra = 0;
        double minSteepestLocal_inter = Double.MAX_VALUE, maxSteepestLocal_inter = Double.MIN_VALUE, avgSteepestLocal_inter = 0;
        double minGreedyLocal_inter = Double.MAX_VALUE, maxGreedyLocal_inter = Double.MIN_VALUE, avgGreedyLocal_inter = 0;
        double minSteepestLocal_intra_rand = Double.MAX_VALUE, maxSteepestLocal_intra_rand = Double.MIN_VALUE, avgSteepestLocal_intra_rand = 0;
        double minGreedyLocal_intra_rand = Double.MAX_VALUE, maxGreedyLocal_intra_rand = Double.MIN_VALUE, avgGreedyLocal_intra_rand = 0;
        double minSteepestLocal_inter_rand = Double.MAX_VALUE, maxSteepestLocal_inter_rand = Double.MIN_VALUE, avgSteepestLocal_inter_rand = 0;
        double minGreedyLocal_inter_rand = Double.MAX_VALUE, maxGreedyLocal_inter_rand = Double.MIN_VALUE, avgGreedyLocal_inter_rand = 0;
        double minCandidateMoves = Double.MAX_VALUE, maxCandidateMoves = Double.MIN_VALUE, avgCandidateMoves = 0;
        double minCandidateMoves_rand = Double.MAX_VALUE, maxCandidateMoves_rand = Double.MIN_VALUE, avgCandidateMoves_rand = 0;
        Integer[] fixedInitialSolutionArray = {89, 183, 143, 117, 0, 46, 68, 139, 193, 41, 115, 5, 42, 181, 159, 69, 108, 18, 22, 146, 34, 160, 48, 54, 177, 10, 190, 4, 112, 84, 184, 43, 116, 65, 59, 118, 51, 151, 133, 162, 123, 127, 70, 135, 154, 180, 53, 121, 100, 26, 86, 75, 44, 25, 16, 171, 175, 113, 56, 31, 78, 145, 179, 196, 81, 90, 40, 165, 185, 106, 178, 138, 14, 144, 62, 9, 148, 102, 49, 52, 55, 92, 57, 129, 82, 120, 2, 101, 1, 97, 152, 124, 94, 63, 79, 80, 176, 137, 23, 186};

        List<Integer> fixedInitialSolution = new ArrayList<>(Arrays.asList(fixedInitialSolutionArray));

        int trials = 200;

        // 50% of total nodes, rounded up
        int nodesToSelect = (int) Math.ceil(numNodes / 2.0);

        File bestPaths = new File("output/best_paths_local_search_times.csv");
        List<Integer> bestPath = new ArrayList<>();
        System.out.println("step1");


        avgRandom /= trials;


        List<Integer> bestPathSteepest_inter = new ArrayList<>();
        List<Integer> bestPathGreedyLocal_inter = new ArrayList<>();
        List<Integer> bestPathSteepest_intra = new ArrayList<>();
        List<Integer> bestPathGreedyLocal_intra = new ArrayList<>();
        List<Integer> bestPathSteepest_inter_rand = new ArrayList<>();
        List<Integer> bestPathGreedyLocal_inter_rand = new ArrayList<>();
        List<Integer> bestPathSteepest_intra_rand = new ArrayList<>();
        List<Integer> bestPathGreedyLocal_intra_rand = new ArrayList<>();
        List<Integer> bestPathCandidateMoves = new ArrayList<>();
        List<Integer> bestPathCandidateMoves_rand = new ArrayList<>();

        SteepestLocalSearch steepestLocalSearch = new SteepestLocalSearch();
        GreedyLocalSearch greedyLocalSearch = new GreedyLocalSearch();
        SteepestLocalSearchWithCandidates steepestLocalSearchCandidate = new SteepestLocalSearchWithCandidates();
        SteepestLocalSearchWithMoveEvaluations steepestLocalSearchWithMoveEvaluations = new SteepestLocalSearchWithMoveEvaluations();

        Map<Integer, List<Integer>> candidateEdges = steepestLocalSearchCandidate.generateCandidateEdges(distanceMatrix, nodes);

        numNodes = 200;

        System.out.println("step3: prefixed");
        // Fixed
        for (int j = 0; j < numNodes; j++) {
            int startNode = j;
            System.out.print("step4: " +startNode);
            List<Integer> initialSolution = GreedyWeighted.greedyWeighted(distanceMatrix, startNode, nodes);

            // Steepest Local Search
            long startTime = System.nanoTime();
            List<Integer> steepestSolution = steepestLocalSearch.optimize(initialSolution, distanceMatrix, nodes, "both", "two-edges");
            long endTime = System.nanoTime();
            double elapsedTime = (endTime - startTime) / 1e6;
            double costSteepest = elapsedTime;
            minSteepestLocal_inter = Math.min(minSteepestLocal_inter, costSteepest);
            maxSteepestLocal_inter = Math.max(maxSteepestLocal_inter, costSteepest);
            avgSteepestLocal_inter += costSteepest;
            if (minSteepestLocal_inter == costSteepest) {
                bestPathSteepest_inter = steepestSolution;
            }

            System.out.print("+");
            // Steepest Local Search with Candidate Moves
            startTime = System.nanoTime();
            List<Integer> candidateMovesSolution = steepestLocalSearchCandidate.optimize(initialSolution, distanceMatrix, nodes, "two-edges", candidateEdges);
            endTime = System.nanoTime();
            elapsedTime = (endTime - startTime) / 1e6;
            double costCandidateMoves = elapsedTime;
            minCandidateMoves = Math.min(minCandidateMoves, costCandidateMoves);
            maxCandidateMoves = Math.max(maxCandidateMoves, costCandidateMoves);
            avgCandidateMoves += costCandidateMoves;
            if (minCandidateMoves == costCandidateMoves) {
                bestPathCandidateMoves = candidateMovesSolution;
            }

            System.out.print("+");
            // Greedy Local Search
            startTime = System.nanoTime();
            List<Integer> greedyLocalSolution = greedyLocalSearch.optimize(initialSolution, distanceMatrix, nodes, "both", "two-edges");
            endTime = System.nanoTime();
            elapsedTime = (endTime - startTime) / 1e6;
            double costGreedyLocal = elapsedTime;
            minGreedyLocal_inter = Math.min(minGreedyLocal_inter, costGreedyLocal);
            maxGreedyLocal_inter = Math.max(maxGreedyLocal_inter, costGreedyLocal);
            avgGreedyLocal_inter += costGreedyLocal;
            if (minGreedyLocal_inter == costGreedyLocal) {
                bestPathGreedyLocal_inter = greedyLocalSolution;
            }

            System.out.print("+");
            // Steepest Local Search
            startTime = System.nanoTime();
            List<Integer> steepestSolution2 = steepestLocalSearch.optimize(initialSolution, distanceMatrix, nodes, "both", "two-nodes");
            endTime = System.nanoTime();
            elapsedTime = (endTime - startTime) / 1e6;
            double costSteepest2 = elapsedTime;
            minSteepestLocal_intra = Math.min(minSteepestLocal_intra, costSteepest2);
            maxSteepestLocal_intra = Math.max(maxSteepestLocal_intra, costSteepest2);
            avgSteepestLocal_intra += costSteepest2;
            if (minSteepestLocal_intra == costSteepest2) {
                bestPathSteepest_intra = steepestSolution2;
            }

            System.out.println("+");
            // Greedy Local Search
            startTime = System.nanoTime();
            List<Integer> greedyLocalSolution2 = greedyLocalSearch.optimize(initialSolution, distanceMatrix, nodes, "both", "two-nodes");
            endTime = System.nanoTime();
            elapsedTime = (endTime - startTime) / 1e6;
            double costGreedyLocal2 = elapsedTime;
            minGreedyLocal_intra = Math.min(minGreedyLocal_intra, costGreedyLocal2);
            maxGreedyLocal_intra = Math.max(maxGreedyLocal_intra, costGreedyLocal2);
            avgGreedyLocal_intra += costGreedyLocal2;
            if (minGreedyLocal_intra == costGreedyLocal2) {
                bestPathGreedyLocal_intra = greedyLocalSolution2;
            }
        }

        avgSteepestLocal_inter /= numNodes;
        avgCandidateMoves /= numNodes;
        avgGreedyLocal_inter /= numNodes;
        avgSteepestLocal_intra /= numNodes;
        avgGreedyLocal_intra /= numNodes;
        System.out.println("step4: prerand");

        // Random
        for (int j = 0; j < numNodes; j++) {
            int startNode = j;
            System.out.print("step4: " +startNode);
            List<Integer> initialSolution = RandomSolution.generateRandomSolution(nodes.length);

            // Steepest Local Search
            long startTime = System.nanoTime();
            List<Integer> steepestSolution1 = steepestLocalSearch.optimize(initialSolution, distanceMatrix, nodes, "both", "two-edges");
            long endTime = System.nanoTime();
            double elapsedTime = (endTime - startTime) / 1e6;
            double costSteepest = elapsedTime;
            minSteepestLocal_inter_rand  = Math.min(minSteepestLocal_inter_rand , costSteepest);
            maxSteepestLocal_inter_rand  = Math.max(maxSteepestLocal_inter_rand , costSteepest);
            avgSteepestLocal_inter_rand  += costSteepest;
            if (minSteepestLocal_inter_rand  == costSteepest) {
                bestPathSteepest_inter_rand = steepestSolution1;
            }

            System.out.print("+");
            // Steepest Local Search with Candidate Moves
            startTime = System.nanoTime();
            List<Integer> candidateMovesSolution1 = steepestLocalSearchCandidate.optimize(initialSolution, distanceMatrix, nodes, "two-edges", candidateEdges);
            endTime = System.nanoTime();
            elapsedTime = (endTime - startTime) / 1e6;
            double costCandidateMoves = elapsedTime;
            minCandidateMoves_rand = Math.min(minCandidateMoves_rand, costCandidateMoves);
            maxCandidateMoves_rand = Math.max(maxCandidateMoves_rand, costCandidateMoves);
            avgCandidateMoves_rand += costCandidateMoves;
            if (minCandidateMoves_rand == costCandidateMoves) {
                bestPathCandidateMoves_rand = candidateMovesSolution1;
            }

            System.out.print("+");
            // Greedy Local Search
            startTime = System.nanoTime();
            List<Integer> greedyLocalSolution1 = greedyLocalSearch.optimize(initialSolution, distanceMatrix, nodes, "both", "two-edges");
            endTime = System.nanoTime();
            elapsedTime = (endTime - startTime) / 1e6;
            double costGreedyLocal = elapsedTime;
            minGreedyLocal_inter_rand  = Math.min(minGreedyLocal_inter_rand , costGreedyLocal);
            maxGreedyLocal_inter_rand  = Math.max(maxGreedyLocal_inter_rand , costGreedyLocal);
            avgGreedyLocal_inter_rand  += costGreedyLocal;
            if (minGreedyLocal_inter_rand == costGreedyLocal) {
                bestPathGreedyLocal_inter_rand = greedyLocalSolution1;
            }

            System.out.print("+");
            // Steepest Local Search
            startTime = System.nanoTime();
            List<Integer> steepestSolution21 = steepestLocalSearch.optimize(initialSolution, distanceMatrix, nodes, "both", "two-nodes");
            endTime = System.nanoTime();
            elapsedTime = (endTime - startTime) / 1e6;
            double costSteepest21 = elapsedTime;
            minSteepestLocal_intra_rand = Math.min(minSteepestLocal_intra_rand, costSteepest21);
            maxSteepestLocal_intra_rand = Math.max(maxSteepestLocal_intra_rand, costSteepest21);
            avgSteepestLocal_intra_rand += costSteepest21;
            if (minSteepestLocal_intra_rand == costSteepest21) {
                bestPathSteepest_intra_rand = steepestSolution21;
            }

            System.out.println("+");
            // Greedy Local Search
            startTime = System.nanoTime();
            List<Integer> greedyLocalSolution21 = greedyLocalSearch.optimize(initialSolution, distanceMatrix, nodes, "both", "two-nodes");
            endTime = System.nanoTime();
            elapsedTime = (endTime - startTime) / 1e6;
            double costGreedyLocal2 = elapsedTime;
            minGreedyLocal_intra_rand  = Math.min(minGreedyLocal_intra_rand , costGreedyLocal2);
            maxGreedyLocal_intra_rand  = Math.max(maxGreedyLocal_intra_rand , costGreedyLocal2);
            avgGreedyLocal_intra_rand  += costGreedyLocal2;
            if (minGreedyLocal_intra_rand  == costGreedyLocal2) {
                bestPathGreedyLocal_intra_rand = greedyLocalSolution21;
            }
        }

        avgSteepestLocal_inter_rand  /= numNodes;
        avgCandidateMoves_rand  /= numNodes;
        avgGreedyLocal_inter_rand  /= numNodes;
        avgSteepestLocal_intra_rand  /= numNodes;
        avgGreedyLocal_intra_rand  /= numNodes;

        // add local search tests here

        try (FileWriter writer = new FileWriter(bestPaths, true)) {
            writer.write("SteepestLocalSearch_nodes");
            for (int node : bestPathSteepest_intra) writer.write("," + node);
            writer.write("\n");

            writer.write("GreedyLocalSearch_nodes");
            for (int node : bestPathGreedyLocal_intra) writer.write("," + node);
            writer.write("\n");

            writer.write("SteepestLocalSearch_egde");
            for (int node : bestPathSteepest_inter) writer.write("," + node);
            writer.write("\n");

            writer.write("GreedyLocalSearch_egde");
            for (int node : bestPathGreedyLocal_inter) writer.write("," + node);
            writer.write("\n");

            writer.write("SteepestLocalSearch_nodes_rand");
            for (int node : bestPathSteepest_intra_rand) writer.write("," + node);
            writer.write("\n");

            writer.write("GreedyLocalSearch_nodes_rand");
            for (int node : bestPathGreedyLocal_intra_rand) writer.write("," + node);
            writer.write("\n");

            writer.write("SteepestLocalSearch_egde_rand");
            for (int node : bestPathSteepest_inter_rand) writer.write("," + node);
            writer.write("\n");

            writer.write("GreedyLocalSearch_egde_rand");
            for (int node : bestPathGreedyLocal_inter_rand) writer.write("," + node);
            writer.write("\n");

            writer.write("CandidateMoves");
            for (int node : bestPathCandidateMoves) writer.write("," + node);
            writer.write("\n");

            writer.write("CandidateMoves_rand");
            for (int node : bestPathCandidateMoves_rand) writer.write("," + node);
            writer.write("\n");
        }


        System.out.println("Steepest Local_egde Search - Min: " + minSteepestLocal_inter + ", Max: " + maxSteepestLocal_inter + ", Avg: " + avgSteepestLocal_inter);
        System.out.println("Greedy Local_egde Search - Min: " + minGreedyLocal_inter + ", Max: " + maxGreedyLocal_inter + ", Avg: " + avgGreedyLocal_inter);
        System.out.println("Steepest Local_egde_rand Search - Min: " + minSteepestLocal_inter_rand + ", Max: " + maxSteepestLocal_inter_rand + ", Avg: " + avgSteepestLocal_inter_rand);
        System.out.println("Greedy Local_egde_rand Search - Min: " + minGreedyLocal_inter_rand + ", Max: " + maxGreedyLocal_inter_rand + ", Avg: " + avgGreedyLocal_inter_rand);
        System.out.println("Steepest Local_nodes Search - Min: " + minSteepestLocal_intra + ", Max: " + maxSteepestLocal_intra + ", Avg: " + avgSteepestLocal_intra);
        System.out.println("Greedy Local_nodes Search - Min: " + minGreedyLocal_intra + ", Max: " + maxGreedyLocal_intra + ", Avg: " + avgGreedyLocal_intra);
        System.out.println("Steepest Local_nodes_rand Search - Min: " + minSteepestLocal_intra_rand + ", Max: " + maxSteepestLocal_intra_rand + ", Avg: " + avgSteepestLocal_intra_rand);
        System.out.println("Greedy Local_nodes_rand Search - Min: " + minGreedyLocal_intra_rand + ", Max: " + maxGreedyLocal_intra_rand + ", Avg: " + avgGreedyLocal_intra_rand);
        System.out.println("Candidate Moves - Min: " + minCandidateMoves + ", Max: " + maxCandidateMoves + ", Avg: " + avgCandidateMoves);
        System.out.println("Candidate Moves_rand - Min: " + minCandidateMoves_rand + ", Max: " + maxCandidateMoves_rand + ", Avg: " + avgCandidateMoves_rand);

        appendResultsToCSV(minSteepestLocal_inter, maxSteepestLocal_inter, avgSteepestLocal_inter,
                minGreedyLocal_inter, maxGreedyLocal_inter, avgGreedyLocal_inter,
                minSteepestLocal_inter_rand, maxSteepestLocal_inter_rand, avgSteepestLocal_inter_rand,
                minGreedyLocal_inter_rand, maxGreedyLocal_inter_rand, avgGreedyLocal_inter_rand,
                minSteepestLocal_intra, maxSteepestLocal_intra, avgSteepestLocal_intra,
                minGreedyLocal_intra, maxGreedyLocal_intra, avgGreedyLocal_intra,
                minSteepestLocal_intra_rand, maxSteepestLocal_intra_rand, avgSteepestLocal_intra_rand,
                minGreedyLocal_intra_rand, maxGreedyLocal_intra_rand, avgGreedyLocal_intra_rand,
                minCandidateMoves, maxCandidateMoves, avgCandidateMoves,
                minCandidateMoves_rand, maxCandidateMoves_rand, avgCandidateMoves_rand);

        writeDistanceMatrixToCSV(distanceMatrix);
    }

    private static void appendResultsToCSV(
            double minSteepestLocal_inter, double maxSteepestLocal_inter, double avgSteepestLocal_inter,
            double minGreedyLocal_inter, double maxGreedyLocal_inter, double avgGreedyLocal_inter,
            double minSteepestLocal_inter_rand, double maxSteepestLocal_inter_rand, double avgSteepestLocal_inter_rand,
            double minGreedyLocal_inter_rand, double maxGreedyLocal_inter_rand, double avgGreedyLocal_inter_rand,
            double minSteepestLocal_intra, double maxSteepestLocal_intra, double avgSteepestLocal_intra,
            double minGreedyLocal_intra, double maxGreedyLocal_intra, double avgGreedyLocal_intra,
            double minSteepestLocal_intra_rand, double maxSteepestLocal_intra_rand, double avgSteepestLocal_intra_rand,
            double minGreedyLocal_intra_rand, double maxGreedyLocal_intra_rand, double avgGreedyLocal_intra_rand,
            double minCandidateMoves, double maxCandidateMoves, double avgCandidateMoves,
            double minCandidateMoves_rand, double maxCandidateMoves_rand, double avgCandidateMoves_rand
    ) throws IOException {
        File file = new File("output/results_time.csv");
        boolean fileExists = file.exists();

        try (FileWriter writer = new FileWriter(file, true)) {
            if (!fileExists) {
                writer.write("Min Steepest Local_inter,Max Steepest Local_inter,Avg Steepest Local_inter," +
                        "Min Greedy Local_inter,Max Greedy Local_inter,Avg Greedy Local_inter," +
                        "Min Steepest Local_inter_rand,Max Steepest Local_inter_rand,Avg Steepest Local_inter_rand," +
                        "Min Greedy Local_inter_rand,Max Greedy Local_inter_rand,Avg Greedy Local_inter_rand," +
                        "Min Steepest Local_intra,Max Steepest Local_intra,Avg Steepest Local_intra," +
                        "Min Greedy Local_intra,Max Greedy Local_intra,Avg Greedy Local_intra," +
                        "Min Steepest Local_intra_rand,Max Steepest Local_intra_rand,Avg Steepest Local_intra_rand," +
                        "Min Greedy Local_intra_rand,Max Greedy Local_intra_rand,Avg Greedy Local_intra_rand" +
                        "Min CandidateMoves,Max CandidateMoves,Avg CandidateMoves" +
                        "Min CandidateMoves_rand,Max CandidateMoves_rand,Avg CandidateMoves_rand" +
                        "\n");
            }

            writer.write(minSteepestLocal_inter + "," + maxSteepestLocal_inter + "," + avgSteepestLocal_inter + "," +
                    minGreedyLocal_inter + "," + maxGreedyLocal_inter + "," + avgGreedyLocal_inter + "," +
                    minSteepestLocal_inter_rand + "," + maxSteepestLocal_inter_rand + "," + avgSteepestLocal_inter_rand + "," +
                    minGreedyLocal_inter_rand + "," + maxGreedyLocal_inter_rand + "," + avgGreedyLocal_inter_rand + "," +
                    minSteepestLocal_intra + "," + maxSteepestLocal_intra + "," + avgSteepestLocal_intra + "," +
                    minGreedyLocal_intra + "," + maxGreedyLocal_intra + "," + avgGreedyLocal_intra + "," +
                    minSteepestLocal_intra_rand + "," + maxSteepestLocal_intra_rand + "," + avgSteepestLocal_intra_rand + "," +
                    minGreedyLocal_intra_rand + "," + maxGreedyLocal_intra_rand + "," + avgGreedyLocal_intra_rand +
                    minCandidateMoves + "," + maxCandidateMoves + "," + avgCandidateMoves +
                    minCandidateMoves_rand + "," + maxCandidateMoves_rand + "," + avgCandidateMoves_rand +
                    "\n");
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
