package lab.tsp;

import lab.tsp.RandomSolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Msls {

    public List<Integer> optimize(double[][] distanceMatrix, double[][] nodes, String moveType, String moveVariant) {
        List<Integer> bestSolution = new ArrayList<>();
        double bestCost = Double.MAX_VALUE;

        int maxIterations = 200;

        for (int iteration = 0; iteration < maxIterations; iteration++) {
            List<Integer> initialSolution = RandomSolution.generateRandomSolution(nodes.length);
            List<Integer> currentSolution = steepestLocalSearch(initialSolution, distanceMatrix, nodes, moveType, moveVariant);
            double currentCost = RandomSolution.calculateCost(currentSolution, distanceMatrix, nodes);

            if (currentCost < bestCost) {
//                System.out.println(currentCost);
                bestCost = currentCost;
                bestSolution = new ArrayList<>(currentSolution);
            }
        }

        return bestSolution;
    }

    public List<Integer> steepestLocalSearch(List<Integer> initialSolution, double[][] distanceMatrix, double[][] nodes, String moveType, String moveVariant) {
        List<Integer> currentSolution = new ArrayList<>(initialSolution);
        double bestCost = RandomSolution.calculateCost(currentSolution, distanceMatrix, nodes);
        boolean improvement = true;

        Random random = new Random();

        while (improvement){
            improvement = false;

            int binary = random.nextInt(2);
            if(binary == 0){
                moveType = "intra";
            }else{
                moveType = "inter";
            }

            double bestDelta = 0;
            int bestI = -1, bestJ = -1;
            boolean isInterRouteMove = false;

            if (moveType.equals("intra") || moveType.equals("both")) {
                for (int i = 0; i < currentSolution.size(); i++) {
                    for (int j = i + 1; j < currentSolution.size(); j++) {
                        double delta;
                        if (moveVariant.equals("two-nodes")) {
                            delta = calculateTwoNodesExchangeDelta(currentSolution, i, j, distanceMatrix);
                        } else {
                            delta = calculateTwoEdgesExchangeDelta(currentSolution, i, j, distanceMatrix);
                        }
                        if (delta < bestDelta) {
                            bestDelta = delta;
                            bestI = i;
                            bestJ = j;
                            isInterRouteMove = false;
                        }
                    }
                }
            }

            if (moveType.equals("inter") || moveType.equals("both") || bestDelta == 0) {
                List<Integer> unselectedNodes = getUnselectedNodes(currentSolution, nodes.length);
                for (int i = 0; i < currentSolution.size(); i++) {
                    for (int node : unselectedNodes) {
                        double delta = calculateSingleNodeChangeDelta(currentSolution, i, node, distanceMatrix, nodes);
                        if (delta < bestDelta) {
                            bestDelta = delta;
                            bestI = i;
                            bestJ = node;
                            isInterRouteMove = true;
                        }
                    }
                }
            }

            if (bestDelta < 0) {
                if (isInterRouteMove) {
                    currentSolution.set(bestI, bestJ);
                    improvement = true;
                } else {
                    applyIntraMove(currentSolution, bestI, bestJ, moveVariant);
                    improvement = true;
                }
                bestCost += bestDelta;
            }
        }
        return currentSolution;
    }

    private void applyIntraMove(List<Integer> solution, int i, int j, String moveVariant) {
        if (moveVariant.equals("two-nodes")) {
            // Swap the two nodes directly
            Collections.swap(solution, i, j);
        } else if (moveVariant.equals("two-edges")) {
            // Ensure i is less than j for swapping
            if (i > j) {
                int temp = i;
                i = j;
                j = temp;
            }

            // Reverse the segment of the list between indices i and j
            int left = i + 1; // start right after i
            int right = j;    // end at j

            while (left < right) {
                Collections.swap(solution, left, right);
                left++;
                right--;
            }
        }
    }

    public static double calculateTwoEdgesExchangeDelta(List<Integer> currentSolution, int i, int j, double[][] distanceMatrix) {
        int nodeA1 = currentSolution.get(i);
        int nodeA2 = currentSolution.get((i + 1) % currentSolution.size());
        int nodeB1 = currentSolution.get(j);
        int nodeB2 = currentSolution.get((j + 1) % currentSolution.size());

        double originalCost = distanceMatrix[nodeA1][nodeA2] + distanceMatrix[nodeB1][nodeB2];
        double newCost = distanceMatrix[nodeA1][nodeB1] + distanceMatrix[nodeA2][nodeB2];

        return newCost - originalCost;
    }

    public static List<Integer> getUnselectedNodes(List<Integer> currentSolution, int numNodes) {
        List<Integer> allNodes = new ArrayList<>();
        for (int i = 0; i < numNodes; i++) {
            if (!currentSolution.contains(i)) {
                allNodes.add(i);
            }
        }
        return allNodes;
    }

    public static double calculateSingleNodeChangeDelta(List<Integer> currentSolution, int index, int newNode, double[][] distanceMatrix, double[][] nodes) {
        int currentNode = currentSolution.get(index);
        double currentNodeCost = nodes[currentNode][2];
        double newNodeCost = nodes[newNode][2];
        double costChange = newNodeCost - currentNodeCost;

        if (index == 0) {
            double originalCost = distanceMatrix[currentNode][currentSolution.get(index + 1)];
            double newCost = distanceMatrix[newNode][currentSolution.get(index + 1)];
            return costChange + (newCost - originalCost);
        } else if (index == currentSolution.size() - 1) {
            double originalCost = distanceMatrix[currentSolution.get(index - 1)][currentNode];
            double newCost = distanceMatrix[currentSolution.get(index - 1)][newNode];
            return costChange + (newCost - originalCost);
        } else {
            double originalCost = distanceMatrix[currentSolution.get(index - 1)][currentNode] + distanceMatrix[currentNode][currentSolution.get(index + 1)];
            double newCost = distanceMatrix[currentSolution.get(index - 1)][newNode] + distanceMatrix[newNode][currentSolution.get(index + 1)];
            return costChange + (newCost - originalCost);
        }
    }

    public static double calculateTwoNodesExchangeDelta(List<Integer> currentSolution, int i, int j, double[][] distanceMatrix) {
        int nodeA = currentSolution.get(i);
        int nodeB = currentSolution.get(j);

        int prevA = currentSolution.get((i - 1 + currentSolution.size()) % currentSolution.size());
        int nextA = currentSolution.get((i + 1) % currentSolution.size());
        int prevB = currentSolution.get((j - 1 + currentSolution.size()) % currentSolution.size());
        int nextB = currentSolution.get((j + 1) % currentSolution.size());

        if (Math.abs(i - j) == 1 || Math.abs(i - j) == currentSolution.size() - 1) {
            // Handle adjacent nodes (where swapping could impact fewer edges)
            // If `i` and `j` are consecutive, we adjust the calculation to avoid double-counting edges.
            double originalCost = distanceMatrix[prevA][nodeA] + distanceMatrix[nodeA][nodeB] + distanceMatrix[nodeB][nextB];
            double newCost = distanceMatrix[prevA][nodeB] + distanceMatrix[nodeB][nodeA] + distanceMatrix[nodeA][nextB];
            return newCost - originalCost;
        } else {
            // Handle non-adjacent nodes (standard case)
            double originalCost = distanceMatrix[prevA][nodeA] + distanceMatrix[nodeA][nextA]
                    + distanceMatrix[prevB][nodeB] + distanceMatrix[nodeB][nextB];
            double newCost = distanceMatrix[prevA][nodeB] + distanceMatrix[nodeB][nextA]
                    + distanceMatrix[prevB][nodeA] + distanceMatrix[nodeA][nextB];
            return newCost - originalCost;
        }
    }
}
