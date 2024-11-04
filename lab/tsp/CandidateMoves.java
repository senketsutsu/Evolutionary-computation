package lab.tsp;

import java.util.*;

public class CandidateMoves {

    private Map<Integer, List<Integer>> candidateEdgesMap;

    public void candidateMoves_calculate(double[][] distanceMatrix, double[][] nodes, int numCandidates) {
        this.candidateEdgesMap = calculateCandidateEdges(distanceMatrix, nodes, numCandidates);
    }

    public List<Integer> optimize(List<Integer> initialSolution, double[][] distanceMatrix, double[][] nodes, String moveType, String moveVariant) {
        long startTime = System.nanoTime();
        candidateMoves_calculate(distanceMatrix, nodes, 10);  // Calculate candidates once at start
        List<Integer> currentSolution = new ArrayList<>(initialSolution);
        double bestCost = RandomSolution.calculateCost(currentSolution, distanceMatrix, nodes);
        boolean improvement = true;
        int counter = 0;

        while (improvement) {
//            System.out.println(counter);
            counter += 1;
            improvement = false;
            double bestDelta = 0;
            int bestI = -1, bestJ = -1;
            boolean isInterRouteMove = false;

            // Randomize move type selection if needed
            moveType = (new Random().nextBoolean()) ? "intra" : "inter";

            // Intra-route move (two-node or two-edge exchange)
            if (moveType.equals("intra") || moveType.equals("both")) {
                for (int i = 0; i < currentSolution.size(); i++) {
                    for (int j : candidateEdgesMap.getOrDefault(currentSolution.get(i), Collections.emptyList())) {
                        if (!currentSolution.contains(j)) continue;
                        int j_index = currentSolution.indexOf(j);
                        if (i >= j_index) continue;  // Skip redundant moves

                        double delta = (moveVariant.equals("two-nodes"))
                                ? calculateTwoNodesExchangeDelta(currentSolution, i, j_index, distanceMatrix)
                                : calculateTwoEdgesExchangeDelta(currentSolution, i, j_index, distanceMatrix);

                        if (delta < bestDelta) {
                            bestDelta = delta;
                            bestI = i;
                            bestJ = j_index;
                            isInterRouteMove = false;
                        }
                    }
                }
            }

            // Inter-route move (swap with unselected node)
            if (moveType.equals("inter") || moveType.equals("both")) {
                List<Integer> unselectedNodes = getUnselectedNodes(currentSolution, nodes.length);
                for (int i = 0; i < currentSolution.size(); i++) {
                    for (int node : candidateEdgesMap.getOrDefault(currentSolution.get(i), Collections.emptyList())) {
                        if (!unselectedNodes.contains(node)) continue;
//                        if (!candidateEdgesMap.getOrDefault(currentSolution.get(i), Collections.emptyList()).contains(node)) continue;

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

            // Apply the best found move
            if (bestDelta < 0) {
                if (isInterRouteMove) {
                    currentSolution.set(bestI, bestJ);
                } else {
                    applyIntraMove(currentSolution, bestI, bestJ, moveVariant);
                }
                bestCost += bestDelta;
                improvement = true;
            }
        }
        long endTime = System.nanoTime();
        System.out.println("\n");
        System.out.println((endTime - startTime) / 1e6);
        System.out.println("\n");

        return currentSolution;
    }

    private Map<Integer, List<Integer>> calculateCandidateEdges(double[][] distanceMatrix, double[][] nodes, int numCandidates) {
        Map<Integer, List<Integer>> candidateEdgesMap = new HashMap<>();

        for (int i = 0; i < nodes.length; i++) {
            final int currentNode = i;  // Define currentNode as final to meet the effectively final requirement

            // Comparator to find nearest neighbors based on distance and node cost
            PriorityQueue<Integer> nearestNeighbors = new PriorityQueue<>(numCandidates, new Comparator<Integer>() {
                @Override
                public int compare(Integer a, Integer b) {
                    double distA = distanceMatrix[currentNode][a] + nodes[a][2];
                    double distB = distanceMatrix[currentNode][b] + nodes[b][2];
                    return Double.compare(distA, distB);
                }
            });

            for (int j = 0; j < nodes.length; j++) {
                if (i != j) {
                    nearestNeighbors.add(j);
                    if (nearestNeighbors.size() > numCandidates) {
                        nearestNeighbors.poll();  // Remove the farthest node to keep only the closest ones
                    }
                }
            }

            List<Integer> nearestList = new ArrayList<>(nearestNeighbors);
            candidateEdgesMap.put(i, nearestList);
        }

        return candidateEdgesMap;
    }


    private void applyIntraMove(List<Integer> solution, int i, int j, String moveVariant) {
        if (moveVariant.equals("two-nodes")) {
            Collections.swap(solution, i, j);
        } else if (moveVariant.equals("two-edges")) {
            if (i > j) {
                int temp = i;
                i = j;
                j = temp;
            }
            int left = i + 1;
            int right = j;
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

        double originalCost = distanceMatrix[prevA][nodeA] + distanceMatrix[nodeA][nextA] + distanceMatrix[prevB][nodeB] + distanceMatrix[nodeB][nextB];
        double newCost = distanceMatrix[prevA][nodeB] + distanceMatrix[nodeB][nextA] + distanceMatrix[prevB][nodeA] + distanceMatrix[nodeA][nextB];

        return newCost - originalCost;
    }
}
