import java.nio.file.Path;
import java.util.HashMap;

// Implements the Iterative Deepening A* search algorithm.
public class IDAStarSearch extends Search {

    private Node root;

    private int threshold;

    // IDA* requires files containing the 15 puzzle to be solved, what the solved state looks like, and what heuristic
    // to use.
    public IDAStarSearch(Path fileName, Path goal, int heuristicType) {
        super();

        Node.setGoal(goal);
        Node.setHeuristicType(heuristicType);

        root = new Node(fileName);
        threshold = root.getHeuristic();

        setClosed(new HashMap<>());
    }

    // Until a goal node is found, do a depth first search based on the f value of the node.
    public Node search() {
        long startTime = System.currentTimeMillis();

        while (true) {
            Node result = searchHelper(root);

            if (result.isGoal()) {
                long endTime = System.currentTimeMillis();
                setRunTime(startTime, endTime);

                return result;
            }

            threshold = result.getfValue();
        }
    }

    // If the current node's f value exceeds that of threshold, return. If the current node is a goal node, then stop
    // searching and return it as the solution. Otherwise make a recursive call on each successor to this node. This
    // also keeps track of the node with the minimum f value that exceeds the threshold so that the threshold can be
    // increased in the smallest increment possible after all searches at this threshold have been made.
    private Node searchHelper(Node node) {
        if (node.getfValue() > threshold) {
            return node;
        }

        if (node.isGoal()) {
            return node;
        }

        Node min = new Node();
        for (Node successor : node.getSuccessors()) {
            Node searchNode = searchHelper(successor);

            if (searchNode.isGoal()) {
                return searchNode;
            }

            if (searchNode.getfValue() < min.getfValue() && searchNode.getfValue() > threshold) {
                min = searchNode;
            }
        }

        return min;
    }

    // Neither of these inherited functions need to be used. It's bad style but necessary for the Driver to be written
    // elegantly.
    @Override
    protected void processSuccessor(Node node) {
    }

    @Override
    protected boolean terminate() {
        return false;
    }
}
