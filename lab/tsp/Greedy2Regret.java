package lab.tsp;

import lab.tsp.NearestNeighborAnyPosition;

import java.util.ArrayList;
import java.util.List;

public class Greedy2Regret {
    public static List<Integer> greedy2Regret(double[][] distanceMatrix, int startNode, double[][] nodes) {
        if (distanceMatrix == null || distanceMatrix.length == 0) {
            throw new IllegalArgumentException("Distance matrix cannot be null or empty.");
        }

        int n = distanceMatrix.length;
        boolean[] visited = new boolean[n];
        List<Integer> path = new ArrayList<>();
        path.add(startNode);
        visited[startNode] = true;
        int nodesToSelect = (int) Math.ceil(n / 2.0);

        // all vertices have been added (50%)
        while (path.size() < nodesToSelect) {
            int bestNode = -1;
            double bestIncrementalCost = Double.MAX_VALUE;
            int bestPosition = -1;
            double bestRegret = -1;

            for (int candidateNode = 0; candidateNode < n; candidateNode++) {
                double bestIncrementalCost1 = Double.MAX_VALUE;
                int bestPosition1 = -1;
                double bestIncrementalCost2 = Double.MAX_VALUE;
                int bestPosition2 = -1;
                double regret = -1;
                if (!visited[candidateNode]) {
                    for (int position = 0; position <= path.size(); position++) {
                        double incrementalCost = NearestNeighborAnyPosition.calculateIncrementalCost(path, distanceMatrix, candidateNode, position, nodes);
                        if (incrementalCost < bestIncrementalCost1) {
                            bestIncrementalCost2 = bestIncrementalCost1;
                            bestPosition2 = bestPosition1;
                            bestIncrementalCost1 = incrementalCost;
                            bestPosition1 = position;
                        } else if (incrementalCost < bestIncrementalCost2) {
                            bestIncrementalCost2 = incrementalCost;
                            bestPosition2 = position;
                        }
                    }
                    regret = bestIncrementalCost2 - bestIncrementalCost1;
                    if (regret > bestRegret){
                        bestRegret = regret;
                        bestIncrementalCost = bestIncrementalCost1;
                        bestNode = candidateNode;
                        bestPosition = bestPosition1;
                    }
                }
            }

            if (bestNode >= 0 && bestPosition >= 0) {
                path.add(bestPosition, bestNode); // Insert node at the best position
                visited[bestNode] = true;
            }
        }

        return path;
    }

}
