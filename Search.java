import java.util.*;

// The class all the other search algorithms derive from. It includes the open, closed, and incons lists and getter and
// setter methods for them. It also includes functions used to measure statistics and three methods each algorithm
// needs to implement. These are the search(), processSuccessor(), and terminate() methods.
abstract class Search {

    protected static Node incumbent = null;

    private PriorityQueue<Node> open;
    private Map<Puzzle, Node> incons;
    private Map<Puzzle, Node> closed;

    private int sizeOpen;
    private int sizeClosed;
    private int maxIncons;
    private int maxOpenClosed;
    private int numMoved;
    private double runTime;

    // Default constructor sets all statistics to 0.
    public Search() {
        sizeOpen = 0;
        sizeClosed = 0;
        maxIncons = 0;
        maxOpenClosed = 0;
        numMoved = 0;
        runTime = 0;
    }

    // The main search algorithm. Contains the logic.
    public abstract Node search();

    // Determines what to do with the successors. i.e., put in closed, move from closed to open, put in incons, etc.
    protected abstract void processSuccessor(Node node);

    // Determines when to stop searching.
    protected abstract boolean terminate();

    // Acts as a 'get' method for open. This is needed to compare the f and f' values of nodes found in open.
    protected Node getFromOpen(Node node) {
        Iterator it = open.iterator();
        while (it.hasNext()) {
            Node test = (Node)it.next();

            if (test.equals(node)) {
                return test;
            }
        }

        return node;
    }

    // Makes the 'get' method for open, closed, and incons similar.
    protected Node getFromClosed(Node node) {
        return closed.get(node.getPuzzle());
    }

    // Makes the 'get' method for open, closed, and incons similar.
    protected Node getFromIncons(Node node) {
        return incons.get(node.getPuzzle());
    }

    public void printSolution(Node solution) {
        if (solution.getParent() == null) {
            System.out.println(solution.getPuzzle());
            return;
        }

        printSolution(solution.getParent());
        System.out.println(solution.getPuzzle());
    }

    public int lengthSolution(Node solution) {
        if (solution.getParent() == null) {
            return 0;
        }

        return 1 + lengthSolution(solution.getParent());
    }

    protected void setOpen(PriorityQueue<Node> open) {
        this.open = open;
    }

    protected PriorityQueue<Node> getOpen() {
        return open;
    }

    protected void setClosed(Map<Puzzle, Node> closed) {
        this.closed = closed;
    }

    protected Map<Puzzle, Node> getClosed() {
        return closed;
    }

    protected void setIncons(Map<Puzzle, Node> incons) {
        this.incons = incons;
    }

    protected Map<Puzzle, Node> getIncons() {
        return incons;
    }

    public int getSizeOpen() {
        return sizeOpen;
    }

    protected void incrementSizeOpen() {
        sizeOpen++;
    }

    public int getSizeClosed() {
        return sizeClosed;
    }

    protected void incrementSizeClosed() {
        sizeClosed++;
    }

    public int getMaxIncons() {
        return maxIncons;
    }

    protected void setMaxIncons() {
        int cur = getIncons().size();
        if (cur > maxIncons) {
            maxIncons = cur;
        }
    }

    public int getMaxOpenClosed() {
        return maxOpenClosed;
    }

    protected void setMaxOpenClosed() {
        int cur = getOpen().size() + getClosed().size();
        if (cur > maxOpenClosed) {
            maxOpenClosed = cur;
        }
    }

    public void setMaxOpenClosedIncons() {
        int cur = getOpen().size() + getClosed().size() + getIncons().size();
        if (cur > maxOpenClosed) {
            maxOpenClosed = cur;
        }
    }

    public int getNumMoved() {
        return numMoved;
    }

    protected void incrementNumMoved() {
        numMoved++;
    }

    public double getRunTime() {
        return runTime;
    }

    protected void setRunTime(long startTime, long endTime) {
        runTime = (endTime - startTime) / 1000.0;
    }

    protected static Node getIncumbent() {
        return incumbent;
    }

    protected static void setIncumbent(Node newIncumbent) {
        incumbent = newIncumbent;
    }

    protected static void clear() {
        incumbent = null;
    }
}
