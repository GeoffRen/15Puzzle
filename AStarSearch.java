import java.nio.file.Path;
import java.util.*;

// Implements the A* search algorithm
public class AStarSearch extends Search {

    // A* requires files containing the 15 puzzle to be solved, what the solved state looks like, and what heuristic to
    // use.
    public AStarSearch(Path fileName, Path goal, int heuristicType) {
        super();

        Node.setGoal(goal);
        Node.setHeuristicType(heuristicType);

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingInt(Node::getfValue));
        open.add(new Node(fileName));
        setOpen(open);

        setClosed(new HashMap<>());
    }

    // Until the goal state is found, the node with the lowest f value is visited. The successors to that node are
    // then found and added to the open list if they weren't visited already.
    @Override
    public Node search() {
        long startTime = System.currentTimeMillis();

        while (!terminate()) {
            Node node = getOpen().poll();

            if (node.isGoal()) {
                long endTime = System.currentTimeMillis();
                setRunTime(startTime, endTime);

                return node;
            }

            getClosed().put(node.getPuzzle(), node);

            incrementSizeClosed();

            for (Node successor : node.getSuccessors()) {
                processSuccessor(successor);

                setMaxOpenClosed();
            }
        }

        // Will probably not be reached even if an unsolvable puzzle was entered due to how many permutations there are
        throw new RuntimeException("ERROR: No solution found");
    }

    // Determines what to do with each successor node.
    @Override
    protected void processSuccessor(Node node) {
        // Checks to see if open contains the successor. If so, then if the successor's f value is smaller than the
        // node in open's f value, the node in open will be updated. This is slower, but more memory efficient.
//        if (!getClosed().containsKey(node.getPuzzle())) {
//            if (!getOpen().contains(node)) {
//                getOpen().add(node);
//
//                incrementSizeOpen();
//
//            } else if (node.getfValue() < getFromOpen(node).getfValue()){
//                System.out.println(node.getfValue() + " " + getFromOpen(node).getfValue());
//                getOpen().remove(node);
//                getOpen().add(node);
//            }
//        }
        // Adds the successor to open as long as it isn't in closed. This is faster but less memory efficient.
        if (!getClosed().containsKey(node.getPuzzle())) {
            getOpen().add(node);
            incrementSizeOpen();
        }
    }

    // A* will stop searching when open is empty. That means every possible permutation of the 15 puzzle was searched
    // and a solution was not found.
    @Override
    protected boolean terminate() {
        return getOpen().isEmpty();
    }

}
