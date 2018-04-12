import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

// Implements the Anytime Weighted A* search algorithm.
public class AnytimeWAStarSearch extends Search {

    // Anytime WA* requires files containing the 15 puzzle to be solved, what the solved state looks like, what
    // heuristic to use, and what weight to use for the heuristic.
    public AnytimeWAStarSearch(Path fileName, Path goal, int heuristicType, double weight) {
        super();

        Node.setGoal(goal);
        Node.setHeuristicType(heuristicType);
        Node.setWeight(weight);

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(Node::getfPrimeValue));
        open.add(new Node(fileName));
        setOpen(open);

        setClosed(new HashMap<>());
    }

    // Until open is empty, the node with the least f' value is visited if its f value is less than the current
    // solution's f value (so if the lower bound of the node is less than the absolute upper bound). The successors
    // to that node are then processed accordingly.
    @Override
    public Node search() {
        long startTime = System.currentTimeMillis();

        while (!terminate()) {
            Node node = getOpen().poll();

            if (getIncumbent() == null || node.getfValue() < getIncumbent().getfValue()) {
                getClosed().put(node.getPuzzle(), node);

                incrementSizeClosed();

                for (Node successor : node.getSuccessors()) {
                    processSuccessor(successor);

                    setMaxOpenClosed();
                }

            }
        }

        long endTime = System.currentTimeMillis();
        setRunTime(startTime, endTime);
        return getIncumbent();
    }

    // If the successor's f value (lower bound) is larger than the absolute upper bound, then we don't want to do
    // anything with that node. Otherwise if the node is the goal that means we have found a better solution so we
    // set the incumbent node to that node. If it isn't a goal node then we have to determine what to do with it. If
    // we already visited a node like that before, we need to see if this version is inconsistent. If so, we move it
    // from closed back to open. If we haven't visited it yet, but open contains that node, we update the value of the
    // node in open if this version has a lower f value. If neither open nor closed contain the node we just add it to
    // the open list.
    @Override
    protected void processSuccessor(Node node) {
        if (getIncumbent() != null && node.getfValue() >= getIncumbent().getfValue()) {
            return;
        }

        if (node.isGoal()) {
            setIncumbent(node);

        } else if (getClosed().containsKey(node.getPuzzle()) &&
                node.getfValue() < getFromClosed(node).getfValue()) {
            getOpen().add(node);
            getClosed().remove(node.getPuzzle());

            incrementNumMoved();

        } else if (getOpen().contains(node) &&
                node.getfValue() < getFromOpen(node).getfValue())  {
            getOpen().remove(node);
            getOpen().add(node);

        } else if (!getOpen().contains(node) && !getClosed().containsKey(node.getPuzzle())) {
            getOpen().add(node);

            incrementSizeOpen();
        }
    }

    // If open is empty, that means there are no more nodes to visit that have a f value less than the absolute
    // upper bound. So we finish searching as we have found the optimal solution.
    @Override
    protected boolean terminate() {
        return getOpen().isEmpty();
    }
}
