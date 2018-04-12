import java.awt.*;
import java.nio.file.Path;
import java.util.*;

// Class representing a node. A node contains a Puzzle representing the 15 puzzle board and all the data needed to
// represent any associated values (i.e. f, f', g values, etc.). It also contains the getSuccessors() method which
// returns a list of valid successor configurations.
public class Node {

    // Constants that represent the empty space in the 15 puzzle and each heuristic type.
    private static final int EMPTY_SPACE = 0;
    public static final int SIMPLE_HEURISTIC = 0;
    public static final int MANHATTAN_HEURISTIC = 1;

    private static int heuristicType = 0;
    private static double weight = 1;
    private static Puzzle goal = null;

    private Puzzle puzzle;
    private Point emptySpace;
    private int heuristic;
    private int gValue;
    private int fValue;
    private double fPrimeValue;
    private Node parent;

    // Default constructor sets values to infinity.
    public Node() {
        heuristic = Integer.MAX_VALUE;
        gValue = Integer.MAX_VALUE;
        fValue = Integer.MAX_VALUE;
        fPrimeValue = Double.MAX_VALUE;
    }

    // Creates a new Node copied from another Node.
    public Node(Node node) {
        puzzle = new Puzzle(node.puzzle);
        emptySpace = new Point(node.emptySpace);
        heuristic = node.heuristic;
        gValue = node.gValue;
        fValue = node.fValue;
        fPrimeValue = node.fPrimeValue;
        parent = node.parent;
    }

    // Creates a new Node from a file containing a representation of a 15 puzzle. After creating the 15 puzzle
    // representation, it calculates all the relevant values.
    public Node(Path fileName) {
        puzzle = new Puzzle(fileName);
        emptySpace = puzzle.getPoint(EMPTY_SPACE);
        initializeHeuristic();
        gValue = 0;
        calcfValue();
        calcfPrimeValue();
        parent = null;
    }

    // Finds successors by swapping the empty space with adjacent spaces and checking if that space produces a valid
    // board state.
    protected java.util.List<Node> getSuccessors() {
        java.util.List<Node> ret = new ArrayList<>();
        int emptyRow = (int)emptySpace.getX();
        int emptyCol = (int)emptySpace.getY();

        for (int row = -1; row <= 1; row++) {
            for (int col = -1; col <= 1; col++) {
                Point move = new Point(emptyRow + row, emptyCol + col);

                if (checkValidMove(move)) {
                    Node successor = new Node(this);
                    successor.makeMove(move);

                    successor.initializeHeuristic();
                    successor.parent = this;
                    successor.calcgValue(this);
                    successor.calcfValue();
                    successor.calcfPrimeValue();

                    ret.add(successor);
                }
            }
        }

        return ret;
    }

    // If the point is adjacent to the empty space, the empty space and the point swap locations in the 15 puzzle.
    private void makeMove(Point move) {
        if (checkValidMove(move)) {
            int val = puzzle.getValue(move);

            setValue(emptySpace, val);
            setValue(move, EMPTY_SPACE);

            emptySpace = move;
        }
    }

    // Checks to see if the move would produce a valid board state or not.
    private boolean checkValidMove(Point point) {
        int row = (int)point.getX();
        int col = (int)point.getY();
        int emptyRow = (int)getEmptySpace().getX();
        int emptyCol = (int)getEmptySpace().getY();

        if (row < 0 || row >= puzzle.getDimensions()) {
            return false;
        }

        if (row != emptyRow && row != emptyRow + 1 && row != emptyRow - 1) {
            return false;
        }

        if (col < 0 || col >= puzzle.getDimensions()) {
            return  false;
        }

        if (col != emptyCol && col != emptyCol + 1 && col != emptyCol - 1) {
            return  false;
        }

        if (row != emptyRow && col != emptyCol) {
            return  false;
        }

        if (row == emptyRow && col == emptyCol) {
            return false;
        }

        return  true;
    }

    public int getfValue() {
        return fValue;
    }

    private void calcfValue() {
        fValue = gValue + heuristic;
    }

    public double getfPrimeValue() {
        return fPrimeValue;
    }

    public void calcfPrimeValue() {
        fPrimeValue = gValue + weight * heuristic;
    }

    private void calcgValue(Node node) {
        gValue = node.gValue + 1;
    }

    public int getHeuristic() {
        return heuristic;
    }

    private void initializeHeuristic() {
        if (heuristicType == SIMPLE_HEURISTIC) {
            heuristic = simpleHeuristic();
        }

        if (heuristicType == MANHATTAN_HEURISTIC) {
            heuristic = manhattanHeuristic();
        }
    }

    // Simply counts how many tiles are out of place.
    private int simpleHeuristic() {
        int heuristic = 0;

        for (int row = 0; row < puzzle.getDimensions(); row++) {
            for (int col = 0; col < puzzle.getDimensions(); col++) {
                if (getValue(row, col) != EMPTY_SPACE && getValue(row, col) != goal.getValue(row, col)) {
                    heuristic++;
                }
            }
        }

        return  heuristic;
    }

    // Computes the total manhattan distance for all out of place tiles.
    private int manhattanHeuristic() {
        int heuristic = 0;
        int numTiles = puzzle.getDimensions() * puzzle.getDimensions();

        for (int val = 1; val < numTiles; val++) {
            Point curPoint = puzzle.getPoint(val);
            Point goalPoint = goal.getPoint(val);

            heuristic += Math.abs(curPoint.getX() - goalPoint.getX());
            heuristic += Math.abs(curPoint.getY() - goalPoint.getY());
        }

        return heuristic;
    }

    public Puzzle getPuzzle() {
        return puzzle;
    }

    public Point getEmptySpace() {
        return emptySpace;
    }

    public int getValue(Point point) {
        return puzzle.getValue((int)point.getX(), (int)point.getY());
    }

    public void setValue(Point point, int val) {
        puzzle.setValue((int)point.getX(), (int)point.getY(), val);
    }

    public int getValue(int row, int col) {
        return getValue(new Point(row, col));
    }

    public Node getParent() {
        return parent;
    }

    public boolean isGoal() {
        return Arrays.deepEquals(puzzle.getGrid(), goal.getGrid());
    }

    public static void setGoal(Path fileName) {
        goal = new Puzzle(fileName);
    }

    public static void setHeuristicType(int type) {
        heuristicType = type;
    }

    public static double getWeight() {
        return weight;
    }

    public static void setWeight(double inputWeight) {
        weight = inputWeight;
    }

    @Override
    public String toString() {
        return "fValue = " + fValue +
                "\nfPrimeValue = " + fPrimeValue +
                "\ngValue = " + gValue +
                "\nheuristic = " + heuristic +
                "\nemptySpace = (" + (int)emptySpace.getX() + ", " + (int)emptySpace.getY() + ")" +
                "\npuzzle =\n" + puzzle;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Node)) {
            return false;
        }

        Node node = (Node)obj;

        return puzzle.equals(node.puzzle);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + puzzle.hashCode();
        return result;
    }
}
