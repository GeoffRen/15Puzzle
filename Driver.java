import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

// Provides the player with a means to solve 15 puzzles
public class Driver {

    // Constant representing whether the player wants to continue playing or not
    private static final int RUN = 1;

    // Enum representing which search algorithm the player wants to use
    public enum Decisions {
        A_STAR(0), ANYTIME_WA_STAR(1), ARA_STAR(2), IDA_STAR(3);

        private int value;

        Decisions(int value) {
            this.value = value;
        }

        public static Decisions fromInteger(int x) {
            switch(x) {
                case 0:
                    return A_STAR;
                case 1:
                    return ANYTIME_WA_STAR;
                case 2:
                    return ARA_STAR;
                case 3:
                    return IDA_STAR;
            }
            return null;
        }

        public static boolean contains(Decisions test) {

            for (Decisions decisions : Decisions.values()) {
                if (decisions.equals(test)) {
                    return true;
                }
            }

            return false;
        }
    }

    private static Scanner console = new Scanner(System.in);

    // While the player still wants to solve 15 puzzles, the program will ask for the filenames of the 15 puzzle to be
    // solved, what the goal configuration should be, which algorithm to use, and which heuristic to use. If the player
    // decides to use AWA* or ARA* then the player will be asked what weight to use. If the player decides to use ARA*
    // then the player will be asked what weight decrease value to use. A search for the solution to the inputted 15
    // puzzle will then commence and the solution plus all the relevant statistics will be printed. Finally, the player
    // will be asked whether they want to play again.
    public static void main(String args[]) {
        System.out.println("----------Welcome to the 15 puzzle solver!----------");

        boolean running = true;
        while (running) {
            System.out.println("Enter the name of the file with the puzzle: ");
            Path puzzlePath = getPath();

            System.out.println("Enter the name of the file with the goal configuration: ");
            Path goalPath = getPath();

            decisionText();
            Decisions searchDecision = chooseDecision();

            heuristicText();
            int heuristicType = chooseHeuristic();

            double weight = 0;
            if (searchDecision.equals(Decisions.ANYTIME_WA_STAR) || searchDecision.equals(Decisions.ARA_STAR)) {
                weightText();
                weight = validDouble();
            }

            double weightDecrease = 0;
            if (searchDecision.equals(Decisions.ANYTIME_WA_STAR.ARA_STAR)) {
                weightDecreaseText();
                weightDecrease = validDouble();
            }

            Search search = createSearch(puzzlePath, goalPath, searchDecision, heuristicType, weight, weightDecrease);
            Node solution = search.search();
            printStats(searchDecision, search, solution);

            Search.clear();

            running = runAgain();
            console.nextLine();
        }

        System.out.println("Thank you for playing!");
        System.out.println("----------Ending Program----------");
    }

    // Returns a Path if the file exists
    private static Path getPath() {
        Path path = Paths.get(console.nextLine());
        while (!Files.exists(path)) {
            System.out.println("ERROR: File does not exist. Try again: ");

            path = Paths.get(console.nextLine());
        }

        return path;
    }

    // Returns a Decision correlating to a supported search algorithm
    private static Decisions chooseDecision() {
        Decisions searchDecision = Decisions.fromInteger(validInt());
        while (!Decisions.contains(searchDecision)) {
            System.out.println("ERROR: Enter a number that appears in the left column.");
            decisionText();
            searchDecision = Decisions.fromInteger(validInt());
        }

        return searchDecision;
    }

    private static void decisionText() {
        System.out.println("Select the solver method from the following: ");
        System.out.println("0 | A* Search");
        System.out.println("1 | Anytime WA* Search");
        System.out.println("2 | ARA* Search");
        System.out.println("3 | IDA* Search");
    }

    // Returns a valid heuristic
    private static int chooseHeuristic() {
        int heuristicType = validInt();
        while (heuristicType != Node.SIMPLE_HEURISTIC && heuristicType != Node.MANHATTAN_HEURISTIC) {
            System.out.println("ERROR: Enter a number that appears in the left column.");
            heuristicText();
            heuristicType = validInt();
        }

        return heuristicType;
    }

    private static void heuristicText() {
        System.out.println("Select the heuristic from the following: ");
        System.out.println("0 | Simple Heuristic");
        System.out.println("1 | Manhattan Heuristic");
    }

    // Returns a valid integer
    private static int validInt() {
        while (!console.hasNextInt()) {
            System.out.println("ERROR: Please enter an integer.");
            console.next();
            console.nextLine();
        }

        return console.nextInt();
    }

    private static void weightText() {
        System.out.println("Anytime WA* and ARA* require a weighted heuristic.");
        System.out.println("Enter a weight now: ");
    }

    private static void weightDecreaseText() {
        System.out.println("ARA* decreases the weight after each iteration.");
        System.out.println("Enter how much to decrease the weight by now: ");
    }

    // Returns a valid double
    private static double validDouble() {
        while (!console.hasNextDouble()) {
            System.out.println("ERROR: Please enter a number.");
            console.nextDouble();
            console.nextLine();
        }

        return console.nextDouble();
    }

    // Creates the search object. Which algorithm is used depends on which algorithm the user selected.
    private static Search createSearch(Path puzzlePath, Path goalPath, Decisions searchDecision,
                                       int heuristicType, double weight, double weightDecrease) {
        switch (searchDecision) {
            case A_STAR:
                return new AStarSearch(puzzlePath, goalPath, heuristicType);
            case ANYTIME_WA_STAR:
                return new AnytimeWAStarSearch(puzzlePath, goalPath, heuristicType, weight);
            case ARA_STAR:
                return new ARAStarSearch(puzzlePath, goalPath, heuristicType, weight, weightDecrease);
            case IDA_STAR:
                return new IDAStarSearch(puzzlePath, goalPath, heuristicType);
            default:
                throw new IllegalArgumentException("Unknown search decision: " + searchDecision);
        }
    }

    // Prints the appropriate statistics depending on which search algorithm was used.
    private static void printStats(Decisions searchDecision, Search search, Node solution) {
        search.printSolution(solution);
        System.out.println("Solution length: " + search.lengthSolution(solution));

        switch (searchDecision) {
            case A_STAR:
                System.out.println("Size of closed list: " + search.getSizeClosed());
                System.out.println("Size of open list: " + search.getSizeOpen());
                System.out.println("Max size of open & closed lists: " + search.getMaxOpenClosed());
                break;
            case ARA_STAR:
                System.out.println("Size of closed list: " + search.getSizeClosed());
                System.out.println("Size of open list: " + search.getSizeOpen());
                System.out.println("Max size of incons list: " + search.getMaxIncons());
                System.out.println("Max size of open, closed, & incons lists: " + search.getMaxOpenClosed());
                break;
            case ANYTIME_WA_STAR:
                System.out.println("Size of closed list: " + search.getSizeClosed());
                System.out.println("Size of open list: " + search.getSizeOpen());
                System.out.println("Max size of open & closed lists: " + search.getMaxOpenClosed());
                System.out.println("Number nodes moved from closed -> open: " + search.getNumMoved());
                break;
            case IDA_STAR:
                break;
            default:
                throw new IllegalArgumentException("Unknown search decision: " + searchDecision);
        }

        System.out.println("Run time of program: " + search.getRunTime());
        System.out.println();
    }

    // Returns whether the player wants to play again.
    private static boolean runAgain() {
        System.out.println("Do you want to run the program again?");
        System.out.println("not 1 | Exit Program");
        System.out.println("1 | Run Program Again");

        return validInt() == RUN;
    }
}
