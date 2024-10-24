package lab2.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CSVReader {
    public static double[][] readCSV(String filePath) throws IOException {
        int numberOfNodes = 0;

        // First pass to count the number of nodes
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while (br.readLine() != null) {
                numberOfNodes++;
            }
        }

        // Initialize the data array
        double[][] data = new double[numberOfNodes][3]; // x, y, cost

        // Second pass to read the data into the array
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int index = 0;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";"); // Use semicolon as the delimiter
                if (parts.length < 3) {
                    throw new IOException("Insufficient data on line: " + (index + 1));
                }
                try {
                    data[index][0] = Double.parseDouble(parts[0].trim()); // x
                    data[index][1] = Double.parseDouble(parts[1].trim()); // y
                    data[index][2] = Double.parseDouble(parts[2].trim()); // cost
                } catch (NumberFormatException e) {
                    throw new IOException("Invalid number format on line: " + (index + 1), e);
                }
                index++;
            }
        }

        return data;
    }
}
