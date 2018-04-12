import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

// A glorified matrix class. It throws exceptions if the input isn't a square matrix.
public class Puzzle {

    private int dimensions;
    private int[][] grid;

    // Default constructor leaves everything as empty;
    public Puzzle() {
        dimensions = 0;
        grid = null;
    }

    // Creates a new Puzzle from another Puzzle.
    public Puzzle(Puzzle puzzle) {
        dimensions = puzzle.dimensions;
        grid = arrayCopy(puzzle.grid);
    }

    // Creates a new Puzzle from a text file. This just fills in the matrix row by row, column by column.
    public Puzzle(Path fileName) {
        List<int[]> list = readFile(fileName);
        grid = new int[dimensions][dimensions];

        for (int i = 0; i < dimensions; i++) {
            grid[i] = list.get(i);
        }
    }

    // Helper method to copy arrays because two dimensional arrays have trouble being copied.
    private int[][] arrayCopy(int[][] arr) {
        int[][] ret = new int[dimensions][dimensions];

        for (int row = 0; row < dimensions; row++) {
            for (int col = 0; col < dimensions; col++) {
                ret[row][col] = arr[row][col];
            }
        }

        return ret;
    }

    // Reads in a file and returns a list containing the contents of the file. Each element of the file will be parsed
    // in order into the list. The elements of the file are determined by space separation. The elements should also
    // only be integers. The number of elements in each row of the file should be equivalent to the number of rows too.
    // Nothing is stopping an invalid 15 puzzle from being entered aside from this. So a matrix of 16 1's could be
    // entered without complaint. The search will just crash the program.
    private List<int[]> readFile(Path fileName) {
        List<int[]> list = new ArrayList<>();

        try {
            BufferedReader reader = Files.newBufferedReader(fileName);
            String rawLine = reader.readLine();
            String[] line = rawLine.split("\\s");
            dimensions = line.length;

            int count = 0;
            while (rawLine != null) {
                line = rawLine.split("\\s");

                if (line.length != dimensions) {
                    throw new IllegalArgumentException("All Line Lengths Not Equal");
                }

                int[] row = new int[dimensions];

                for (int i = 0; i < dimensions; i++) {
                    row[i] = Integer.parseInt(line[i]);
                }

                list.add(row);
                count++;

                rawLine = reader.readLine();
            }

            if (count != dimensions) {
                throw new IllegalArgumentException("Not Square Matrix");
            }

        } catch (Exception e) {
            System.out.println("Error with file");
        }

        return list;
    }

    // Returns the point associated with a value in the Puzzle
    public Point getPoint(int val) {
        for (int row = 0; row < dimensions; row++) {
            for (int col = 0; col < dimensions; col++) {
                if (grid[row][col] == val) {
                    return new Point(row, col);
                }
            }
        }

        throw new IllegalArgumentException("No value found");
    }

    // Returns the value associated with the location in the Puzzle
    public int getValue(int row, int col) {
        return grid[row][col];
    }

    // Sets the value at the specified locaiton in the Puzzle
    public void setValue(int row, int col, int val) {
        grid[row][col] = val;
    }

    // Returns the value associated with the location in the Puzzle
    public int getValue(Point space) {
        return getValue((int)space.getX(), (int)space.getY());
    }

    public int[][] getGrid() {
        return grid;
    }

    public int getDimensions() {
        return dimensions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int row = 0; row < dimensions; row++) {
            for (int col = 0; col < dimensions; col++) {
                sb.append(grid[row][col] + " ");
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Puzzle)) {
            return false;
        }

        Puzzle puzzle = (Puzzle) obj;

        return dimensions == puzzle.dimensions &&
                Arrays.deepEquals(grid, puzzle.grid);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + dimensions;
        result = 31 * result + Arrays.deepHashCode(grid);
        return result;
    }
}
