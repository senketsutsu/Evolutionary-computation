# Evolutionary-computation

This Java project implements various algorithms to solve the Traveling Salesman Problem (TSP), including Random Solution, Nearest Neighbor (End and Any Position), and Greedy Cycle. Node data (coordinates and costs) are read from a CSV file.

## Project Structure
```
lab1
├── Main.java                
├── tsp                      
│   ├── RandomSolution.java       
│   ├── NearestNeighborEnd.java 
│   ├── NearestNeighborAnyPosition.java
│   └── GreedyCycle.java       
└── util               
│   ├── CSVReader.java      
│   └── DistanceMatrix.java      
└── output                      
    ├── best_paths.csv
    ├── destance_matrix.csv          
    └── results.csv 
```
## Getting Started

### Prerequisites

- JDK 8 or higher

### Installation

1. Clone the repository:
   ```
   git clone https://github.com/senketsutsu/Evolutionary-computation.git
   ```

2. Run Main.java to execute the algorithms. The output will display each algorithm's minimum, maximum, and average costs.


   


