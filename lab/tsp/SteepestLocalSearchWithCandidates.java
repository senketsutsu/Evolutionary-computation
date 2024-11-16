package lab.tsp;

import java.util.*;

import static lab.tsp.SteepestLocalSearch.calculateTwoNodesExchangeDelta;

public class SteepestLocalSearchWithCandidates {

    private static final int CANDIDATE_COUNT = 10; // Number of nearest neighbors

    public List<Integer> optimize(List<Integer> initialSolution, double[][] distanceMatrix, double[][] nodes, String moveVariant, Map<Integer, List<Integer>> candidateEdges) {
        List<Integer> currentSolution = new ArrayList<>(initialSolution);
        HashSet<Integer> currentSolutionSet = new HashSet<>(initialSolution);
        double bestCost = RandomSolution.calculateCost(currentSolution, distanceMatrix, nodes);
        boolean improvement = true;

        while (improvement) {
            improvement = false;
            double bestDelta = 0;
            int bestI = -1, bestJ = -1;
            boolean isInterRouteMove = false;

            for (int i = 0; i < currentSolution.size(); i++) {
                int node = currentSolution.get(i);
                List<Integer> candidates = candidateEdges.getOrDefault(node, Collections.emptyList());

                // Intra-route moves
                for (int candidate : candidates) {
                    if (currentSolutionSet.contains(candidate)) {
                        //if (Math.abs(i - currentSolution.indexOf(candidate)) <= 1) continue;

                        int j = currentSolution.indexOf(candidate);
                        if (j >= 0 && j < currentSolution.size()) {

                            double delta = calculateTwoEdgesExchangeDelta(currentSolution, i, j, distanceMatrix);

                            if (delta < bestDelta) {
                                bestDelta = delta;
                                bestI = i;
                                bestJ = j;
                                isInterRouteMove = false;
                            }
                        }
                    } else {

                        double delta = calculateSingleNodeChangeDelta(currentSolution, i, candidate, distanceMatrix, nodes);
                        if (delta < bestDelta) {
                            bestDelta = delta;
                            bestI = i;
                            bestJ = candidate;
                            isInterRouteMove = true;
                        }

                    }
                }
            }

            // Apply the best move found
            if (bestDelta < 0) {
                if (isInterRouteMove) {
                    currentSolution.set(bestI, bestJ);
                    currentSolutionSet.remove(currentSolution.get(bestI));
                    currentSolutionSet.add(bestJ);
                } else {
                    applyIntraMove(currentSolution, bestI, bestJ, moveVariant);
                }
                bestCost += bestDelta;
                improvement = true;
            }
        }
        return currentSolution;
    }

    public Map<Integer, List<Integer>> generateCandidateEdges(double[][] distanceMatrix, double[][] nodes) {
        Map<Integer, List<Integer>> candidateEdges = new HashMap<>();
        for (int i = 0; i < nodes.length; i++) {
            List<Integer> nearest = new ArrayList<>();
            for (int j = 0; j < nodes.length; j++) {
                if (i != j) {
                    nearest.add(j);
                }
            }
            int finalI = i;
            nearest.sort(Comparator.comparingDouble(j -> distanceMatrix[finalI][j] + nodes[j][2]));
            candidateEdges.put(i, nearest.subList(0, Math.min(CANDIDATE_COUNT, nearest.size())));
        }
        return candidateEdges;
    }

    private boolean introducesCandidateEdge(List<Integer> solution, int i, int j, Map<Integer, List<Integer>> candidateEdges) {
        int nodeA = solution.get(i);
        int nodeB = solution.get(j);
        return candidateEdges.get(nodeA).contains(nodeB) || candidateEdges.get(nodeB).contains(nodeA);
    }

    private boolean introducesCandidateEdgeForInterRoute(List<Integer> solution, int i, int newNode, Map<Integer, List<Integer>> candidateEdges) {
        int currentNode = solution.get(i);
        return candidateEdges.get(currentNode).contains(newNode);
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
            int left = i + 1, right = j;
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

    public static List<Integer> getUnselectedNodes(List<Integer> currentSolution, int numNodes) {
        List<Integer> allNodes = new ArrayList<>();
        for (int i = 0; i < numNodes; i++) {
            if (!currentSolution.contains(i)) {
                allNodes.add(i);
            }
        }
        return allNodes;
    }
}
