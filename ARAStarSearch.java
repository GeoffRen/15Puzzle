import java.nio.file.Path;
import java.util.*;

// Implements the Anytime Repairing A* search algorithm.
public class ARAStarSearch extends Search{

    private double weightDecrease;

    // ARA* requires files containing the 15 puzzle to be solved, what the solved state looks like, what heuristic to
    // use, what weight to use for the heuristic, and how much to decrease the weight by after each call to
    // improvePath().
    public ARAStarSearch(Path fileName, Path goal, int heuristicType, double weight, double weightDecrease) {
        super();

        this.weightDecrease = weightDecrease;

        Node.setGoal(goal);
        Node.setHeuristicType(heuristicType);
        Node.setWeight(weight);

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(Node::getfPrimeValue));
        open.add(new Node(fileName));
        setOpen(open);

        setIncons(new HashMap<>());
        setClosed(new HashMap<>());

        setIncumbent(new Node());
    }

    // Until the terminationWeight drops to 1 or lower, calls to improvePath() are made in an effort to refine the
    // search.
    @Override
    public Node search() {
        long startTime = System.currentTimeMillis();

        improvePath();

        double terminationWeight = Math.min(Node.getWeight(), getIncumbent().getfValue() / minInconsistentVal());

        while (terminationWeight > 1) {
            Node.setWeight(Node.getWeight() - weightDecrease);

            mergeInconsToOpen();
            updateOpen();

            getClosed().clear();
            getIncons().clear();

            improvePath();

            terminationWeight = Math.min(Node.getWeight(), getIncumbent().getfValue() / minInconsistentVal());
        }

        long endTime = System.currentTimeMillis();
        setRunTime(startTime, endTime);
        return getIncumbent();
    }

    // Until we shouldn't search anymore, we get the node from open with the lowest f' value, visit it, then process
    // each of its successors.
    private void improvePath() {
        while (!terminate()) {
            Node node = getOpen().poll();

            if (node.isGoal()) {
                setIncumbent(node);
            }

            getClosed().put(node.getPuzzle(), node);

            incrementSizeClosed();

            for (Node successor : node.getSuccessors()) {
                processSuccessor(successor);

                setMaxIncons();
                setMaxOpenClosedIncons();
            }
        }
    }

    // If closed nor open contain the successor, just add the node to open. If closed doesn't contain the successor but
    // open does, update open if the successor has a smaller f' value than the node in open. If closed contains the
    // successor but incons doesn't, add the node to incons. If closed and incons contain the successor, update incons
    // if the successor has a smaller f' value than the node in incons.
    @Override
    protected void processSuccessor(Node node) {
        if (!getClosed().containsKey(node.getPuzzle())) {
            if (!getOpen().contains(node)) {
                getOpen().add(node);

                incrementSizeOpen();

            } else if (node.getfPrimeValue() < getFromOpen(node).getfPrimeValue()){
                getOpen().remove(node);
                getOpen().add(node);
            }

        } else {
            if (!getIncons().containsKey(node.getPuzzle()) &&
                    node.getfPrimeValue() < getFromClosed(node).getfPrimeValue()) {
                getIncons().put(node.getPuzzle(), node);

            } else if (getIncons().containsKey(node.getPuzzle()) &&
                    node.getfPrimeValue() < getFromIncons(node).getfPrimeValue()) {
                getIncons().put(node.getPuzzle(), node);
            }
        }
    }

    // If open becomes empty then we have nothing left to search. So we stop. We also stop searching if the current
    // solution has an f' value that is less than or equal to the smallest f prime value in our open list. This means
    // that we can't find a better solution so we should stop searching.
    @Override
    protected boolean terminate() {
        return getOpen().isEmpty() || (getIncumbent().getfPrimeValue() <= getOpen().peek().getfPrimeValue());
    }

    // Finds the node with the minimum f value in incons and open. An iterative search has to be used for incons
    // because it is a hashMap. An iterative search has to be used for open because open is sorted by f' value, not
    // f value.
    private double minInconsistentVal() {
        double min = Integer.MAX_VALUE;
        for (Node cur : getIncons().values()) {
            if (cur.getfValue() < min) {
                min = cur.getfValue();
            }
        }

        for (Iterator it = getOpen().iterator(); it.hasNext();) {
            Node cur = (Node)it.next();
            if (cur.getfValue() < min) {
                min = cur.getfValue();
            }
        }

        return min;
    }

    // Updates all the nodes' f' values in open according to the new weight value.
    private void updateOpen() {
        PriorityQueue<Node> newQueue = new PriorityQueue<>(Comparator.comparingDouble(Node::getfPrimeValue));
        while (!getOpen().isEmpty()) {
            Node node = getOpen().poll();
            node.calcfPrimeValue();
            newQueue.add(node);
        }

        setOpen(newQueue);
    }

    // Moves all the nodes in incons to open.
    private void mergeInconsToOpen() {
        for (Node node : getIncons().values()) {
            getOpen().add(node);

            incrementSizeOpen();
        }
    }
}
