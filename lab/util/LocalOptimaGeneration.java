package lab.util;

import lab.tsp.GreedyLocalSearch;
import lab.tsp.RandomSolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LocalOptimaGeneration {

    public static List<List<Integer>> generateLocalOptima(int numOptima, double[][] distanceMatrix, double[][] nodes, String moveVariant) {
        List<List<Integer>> localOptimaList = new ArrayList<>();
        Random random = new Random();

        // Generate 'numOptima' different local optima
        for (int i = 0; i < numOptima; i++) {
            List<Integer> initialSolution = RandomSolution.generateRandomSolution(nodes.length); // Generate random solution
            GreedyLocalSearch greedyLocalSearch = new GreedyLocalSearch();
            List<Integer> localOptimum = greedyLocalSearch.optimize(initialSolution, distanceMatrix, nodes, "intra", moveVariant);
            localOptimaList.add(localOptimum);
        }

        return localOptimaList;
    }
}
