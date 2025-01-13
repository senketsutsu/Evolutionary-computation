package lab.tsp;

import java.util.*;

public class GPtsp2 {
    /**
     * Finds path using GP approach  inspired by: https://www.geeksforgeeks.org/traveling-salesman-problem-using-genetic-algorithm/?ref=ml_lbp
     *
     * @param distanceMatrix The 2D array representing the distances between nodes.
     * @param nodes The 2D array containing node coordinates and costs.
     * @return A list of node indices representing the Hamiltonian path.
     */

    static int POP_SIZE = 100;
    static int gen_thres = 1000; // 25000
    static double mut_prob = 0.8;
    static double cros_prob = 0.1;
    NearestNeighborAnyPosition NearestNeighborAnyPosition_gen = new NearestNeighborAnyPosition();

    public static List<Integer> main(double[][] nodeData, double[][] distanceMatrix){
        int generation = 0;
        List<Individual> population = new ArrayList<>();
        int noImprovementCount = 0;
        double previousBestFitness = 10000000000000.0;

        for (int i = 0; i < POP_SIZE; i++) {
            population.add(createGnome(nodeData, distanceMatrix));
        }

        while (generation < gen_thres) {
            Collections.sort(population);
            double bestFitness = population.get(0).fitness;


            if (bestFitness < previousBestFitness) {
                noImprovementCount = 0;
            } else {
                noImprovementCount++;
            }

            previousBestFitness = bestFitness;
            List<Individual> newGeneration = new ArrayList<>();


            if (noImprovementCount > 10) { //25
                // System.out.println("Population reset to avoid stagnation.");
                int s = (15 * POP_SIZE) / 100;
                for (int i = 0; i < s; i++)
                    newGeneration.add(population.get(i));
                for (int i = s; i < POP_SIZE; i++) {
                    newGeneration.add(createGnome(nodeData, distanceMatrix));
                }
                population = newGeneration;
                noImprovementCount = 0;
                continue;
            }


            int elite = 10;
            int s = (elite * POP_SIZE) / 100;
            for (int i = 0; i < s; i++)
                newGeneration.add(population.get(i));

            s = ((30) * POP_SIZE) / 100;
            for (int i = 0; i < s; i++) {
                Individual parent1 = population.get(i);

                int r2 = randomNum(0, POP_SIZE/2);
                while(i == r2)
                {
                    r2 = randomNum(0, POP_SIZE/2);
                }

                Individual parent2 = population.get(r2);
                Individual offspring;

                Individual offspring1 = swapEdges(parent1.gnome, nodeData, distanceMatrix);
                Individual offspring2 = swapNodes(parent1.gnome, nodeData, distanceMatrix);
                Individual offspring3 = swapSegments(parent1.gnome, nodeData, distanceMatrix);
                Individual offspring4 = mutateWithOutsideNodes(parent1.gnome, nodeData, distanceMatrix);

                // Add offspring to the new generation
                newGeneration.add(offspring1);
                newGeneration.add(offspring2);
                newGeneration.add(offspring3);
                newGeneration.add(offspring4);

                // Add one random shuffle of the parent to increase diversity
                Individual randomized = shuffleGenes(parent1.gnome, nodeData, distanceMatrix);
                newGeneration.add(randomized);
            }
            population = newGeneration;
            ///*
            System.out.print("Generation: " + generation + "\t");
            System.out.print("No imp.: " + noImprovementCount + "\t");
            // System.out.print("String: " + population.get(0).gnome + "\t");
            System.out.println("Fitness: " + population.get(5).fitness);
            //*/
            if( generation % 1000 == 0){
                System.out.println("Generation: " + generation + " Fitness: " + population.get(0).fitness);
            }
            generation++;
        }
        System.out.print("Generation: " + generation + "\t");
        // System.out.print("String: " + population.get(0).gnome + "\t");
        System.out.println("Fitness: " + population.get(0).fitness);

        Collections.sort(population);
        return population.get(0).gnome;
    }

    private static class Individual implements Comparable<Individual>{
        List<Integer> gnome;
        double fitness;

        public Individual(List<Integer> gnome, double[][] distanceMatrix, double[][] nodeData) {
            this.gnome = gnome;
            this.fitness = RandomSolution.calculateCost(gnome, distanceMatrix, nodeData);
        }

        @Override
        public int compareTo(Individual o) {
            return Double.compare(this.fitness, o.fitness);
        }
    }

    static Individual createGnome(double[][] nodeData, double[][] distanceMatrix) {
        int n = nodeData.length;
        List<Integer> gnome = RandomSolution.generateRandomSolution(n);
/*
        if (randomNum(0,10) == 0) {
            gnome = NearestNeighborAnyPosition.nearestNeighborAnyPosition(distanceMatrix, randomNum(0, n), nodeData);
        }*/
        return new Individual(gnome, distanceMatrix, nodeData);
    }

    static int randomNum(int start, int end) {
        return (int) (Math.random() * (end - start)) + start;
    }

    static Individual mutatedGene(List<Integer> gnome, double[][] nodeData, double[][] distanceMatrix) {
        int n = nodeData.length;
        List<Integer> newGnome = new ArrayList<>(gnome);
        for(int i = 0; i<n/4; i++) {
            int newGene;
            do {
                newGene = randomNum(0, n);
            } while (newGnome.contains(newGene));
            int position = randomNum(0, newGnome.size());
            newGnome.set(position, newGene);
        }
        return new Individual(newGnome, distanceMatrix, nodeData);
    }

    static Individual crossoverGene(List<Integer> gnome1, List<Integer> gnome2, double[][] nodeData, double[][] distanceMatrix) {
        int n = gnome1.size();
        //System.out.println(n);

        List<Integer> childGnome = new ArrayList<>(Collections.nCopies(n, -1));

        int start = randomNum(0, n);
        int end = randomNum(start, n);

        for (int i = start; i <= end; i++) {
            childGnome.set(i, gnome1.get(i));
        }

        int gnome2Index = 0;
        for (int i = 0; i < n; i++) {
            if (childGnome.get(i) != -1) {
                continue;
            }

            while (gnome2Index < n && childGnome.contains(gnome2.get(gnome2Index))) {
                gnome2Index++;
            }

            if (gnome2Index >= n) {
                for (int j = 0; j < n; j++) {
                    if (!childGnome.contains(gnome1.get(j))) {
                        childGnome.set(i, gnome1.get(j));
                        break;
                    }
                }
            } else {
                childGnome.set(i, gnome2.get(gnome2Index));
            }
        }

        return new Individual(childGnome, distanceMatrix, nodeData);
    }

    public static Individual swapEdges(List<Integer> gnome, double[][] nodeData, double[][] distanceMatrix) {
        List<Integer> newGnome = new ArrayList<>(gnome);
        int size = newGnome.size();
        int i = randomNum(0, size - 1);
        int j = randomNum(0, size - 1);

        // Ensure i < j for valid swapping
        if (i > j) {
            int temp = i;
            i = j;
            j = temp;
        }

        // Reverse the segment between i and j
        Collections.reverse(newGnome.subList(i, j + 1));
        double newCost = RandomSolution.calculateCost(newGnome, distanceMatrix, nodeData);

        return new Individual(newGnome, distanceMatrix, nodeData);
    }

    public static Individual swapNodes(List<Integer> gnome, double[][] nodeData, double[][] distanceMatrix) {
        List<Integer> newGnome = new ArrayList<>(gnome);
        int i = randomNum(0, newGnome.size());
        int j = randomNum(0, newGnome.size());

        // Swap two random nodes
        Collections.swap(newGnome, i, j);
        double newCost = RandomSolution.calculateCost(newGnome, distanceMatrix, nodeData);

        return new Individual(newGnome, distanceMatrix, nodeData);
    }

    public static Individual swapSegments(List<Integer> gnome, double[][] nodeData, double[][] distanceMatrix) {
        List<Integer> newGnome = new ArrayList<>(gnome);
        int size = newGnome.size();
        int segmentSize = randomNum(2, size / 4); // Random segment size
        int start1 = randomNum(0, size - segmentSize);
        int start2 = randomNum(0, size - segmentSize);

        // Swap segments
        for (int k = 0; k < segmentSize; k++) {
            int temp = newGnome.get(start1 + k);
            newGnome.set(start1 + k, newGnome.get(start2 + k));
            newGnome.set(start2 + k, temp);
        }

        double newCost = RandomSolution.calculateCost(newGnome, distanceMatrix, nodeData);

        return new Individual(newGnome, distanceMatrix, nodeData);
    }

    public static Individual mutateWithOutsideNodes(List<Integer> gnome, double[][] nodeData, double[][] distanceMatrix) {
        List<Integer> newGnome = new ArrayList<>(gnome);
        Set<Integer> unvisited = getUnvisitedNodes(gnome, nodeData.length);

        if (!unvisited.isEmpty()) {
            int toRemove = randomNum(0, newGnome.size());
            int toInsert = randomElement(unvisited);

            newGnome.set(toRemove, toInsert);
        }

        double newCost = RandomSolution.calculateCost(newGnome, distanceMatrix, nodeData);

        return new Individual(newGnome, distanceMatrix, nodeData);
    }

    private static Set<Integer> getUnvisitedNodes(List<Integer> gnome, int totalNodes) {
        Set<Integer> allNodes = new HashSet<>();
        for (int i = 0; i < totalNodes; i++) {
            allNodes.add(i);
        }
        allNodes.removeAll(gnome);
        return allNodes;
    }

    public static <T> T randomElement(Set<T> set) {
        List<T> list = new ArrayList<>(set);
        return list.get(randomNum(0, list.size() - 1));
    }

    public static Individual shuffleGenes(List<Integer> gnome, double[][] nodeData, double[][] distanceMatrix) {
        List<Integer> newGnome = new ArrayList<>(gnome);
        Collections.shuffle(newGnome);
        double newCost = RandomSolution.calculateCost(newGnome, distanceMatrix, nodeData);

        return new Individual(newGnome, distanceMatrix, nodeData);
    }

}
