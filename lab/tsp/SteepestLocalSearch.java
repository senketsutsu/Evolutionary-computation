package lab.tsp;

import lab.tsp.RandomSolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SteepestLocalSearch {

    public List<Integer> optimize(List<Integer> initialSolution, double[][] distanceMatrix, double[][] nodes, String moveType) {
        List<Integer> currentSolution = new ArrayList<>(initialSolution);
        double bestCost = RandomSolution.calculateCost(currentSolution, distanceMatrix, nodes);
        boolean improvement = true;

        while (improvement) {
            improvement = false;
            double bestDelta = 0;
            int bestI = -1, bestJ = -1;
            boolean isInterRouteMove = false;

            // Perform moves based on moveType flag
            if (moveType.equals("intra")) {
                // Intra-route moves: Try two-nodes and two-edges exchanges
                for (int i = 0; i < currentSolution.size(); i++) {
                    for (int j = i + 1; j < currentSolution.size(); j++) {
                        double delta = calculateTwoNodesExchangeDelta(currentSolution, i, j, distanceMatrix);
                        if (delta < bestDelta) {
                            bestDelta = delta;
                            bestI = i;
                            bestJ = j;
                            isInterRouteMove = false;
                        }
                    }
                }
            }

            if (moveType.equals("inter")) {
                // Inter-route moves: Swap one selected with one not selected
                List<Integer> unselectedNodes = getUnselectedNodes(currentSolution, nodes.length);
                //System.out.println("nodes: "+nodes.length);
                for (int i = 0; i < currentSolution.size(); i++) {
                    //System.out.println("i"+i);
                    for (int node : unselectedNodes) {
                        if (node >= nodes.length) { // Check if node index exceeds bounds
                            break; // Break the inner loop if node exceeds bounds
                        }
                        //System.out.println(node);
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

            // Apply the best move found
            if (bestDelta < 0) {
                if (isInterRouteMove) {
                    int replacedNode = currentSolution.get(bestI);
                    currentSolution.set(bestI, bestJ);
                    improvement = true;
                } else if (bestJ != -1) {
                    // Apply two-nodes or two-edges exchange
                    Collections.swap(currentSolution, bestI, bestJ);
                    improvement = true;
                }
                bestCost += bestDelta;

            }
        }
        return currentSolution;
    }

    public static double calculateTwoNodesExchangeDelta(List<Integer> currentSolution, int i, int j, double[][] distanceMatrix) {
        int nodeA = currentSolution.get(i);
        int nodeB = currentSolution.get(j);

        // Get the previous and next nodes, wrapping around the solution list
        int prevA = currentSolution.get((i - 1 + currentSolution.size()) % currentSolution.size());
        int nextA = currentSolution.get((i + 1) % currentSolution.size());
        int prevB = currentSolution.get((j - 1 + currentSolution.size()) % currentSolution.size());
        int nextB = currentSolution.get((j + 1) % currentSolution.size());

        // Calculate the original cost (before the swap)
        double originalCost =
                distanceMatrix[prevA][nodeA]  // Cost from previous node A to node A
                        + distanceMatrix[nodeA][nextA] // Cost from node A to its next node
                        + distanceMatrix[prevB][nodeB]  // Cost from previous node B to node B
                        + distanceMatrix[nodeB][nextB]; // Cost from node B to its next node

        // Calculate the new cost (after the swap)
        double newCost =
                distanceMatrix[prevA][nodeB]  // Cost from previous node A to node B (after swap)
                        + distanceMatrix[nodeB][nextA] // Cost from node B to its next node (node A's old next node)
                        + distanceMatrix[prevB][nodeA]  // Cost from previous node B to node A (after swap)
                        + distanceMatrix[nodeA][nextB]; // Cost from node A to its next node (node B's old next node)

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
        // Get the node currently at the index
        int currentNode = currentSolution.get(index);

        // Cost of the current node and the new node
        double currentNodeCost = nodes[currentNode][2]; // Assuming index 2 holds the cost for the node
        double newNodeCost = nodes[newNode][2];

        // Calculate cost change considering node costs
        double costChange = newNodeCost - currentNodeCost;

        // If the index is the first node
        if (index == 0) {
            // Cost involving the next node
            double originalCost = distanceMatrix[currentNode][currentSolution.get(index + 1)];
            double newCost = distanceMatrix[newNode][currentSolution.get(index + 1)];
            return costChange + (newCost - originalCost);
        }
        // If the index is the last node
        else if (index == currentSolution.size() - 1) {
            // Cost involving the previous node
            double originalCost = distanceMatrix[currentSolution.get(index - 1)][currentNode];
            double newCost = distanceMatrix[currentSolution.get(index - 1)][newNode];
            return costChange + (newCost - originalCost);
        }
        // For nodes in the middle
        else {
            double originalCost = distanceMatrix[currentSolution.get(index - 1)][currentNode] +
                    distanceMatrix[currentNode][currentSolution.get(index + 1)];
            double newCost = distanceMatrix[currentSolution.get(index - 1)][newNode] +
                    distanceMatrix[newNode][currentSolution.get(index + 1)];
            return costChange + (newCost - originalCost);
        }
    }

}
