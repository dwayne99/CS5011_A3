/**
 * IntermediateAgent is a Tornado sweeper game-playing agent that uses a combination of
 * SPS and a SAT-based solver to determine which cells to probe and flag in order
 * to win the game.
 *
 * This class uses the following external libraries:
 *  - org.logicng:logicng (v2.0.0)
 *
 */

import org.logicng.datastructures.Tristate;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;

import java.util.ArrayList;
import java.util.Vector;

public class IntermediateAgent extends Agent {
    String knowledgeString; // the representation of the world with clues to be processed by a SAT solver
    Vector<Cell> unmarkedCells = new Vector<>(); // Vector of all cells that are covered or unflagged
    int steps;
    int maxSteps = 100;

    /**
     * Constructor for creating an instance of IntermediateAgent.
     *
     * @param game    The Tornado sweeper game instance to be played.
     * @param startX  The x-coordinate of the starting cell to probe.
     * @param startY  The y-coordinate of the starting cell to probe.
     * @param verbose A boolean flag that indicates whether to display verbose output during game play.
     */
    public IntermediateAgent(Game game, int startX, int startY, boolean verbose) {
        super(game, startX, startY, verbose);
    }

    /**
     * Method that plays the game with both the SATS and SPS
     */
    public void playGame() {

        // probe the initial cell
        char info = probe(this.currentX, this.currentY);
        if (info == '0') { // recursively probe all safe cells
            // first get all the neighbours of the current cell
            recursivelyProbeZeros(info);
        }

        // do the same for the center cell  as its safe
        int[] centerxy = game.center();
        this.currentX = centerxy[0];
        this.currentY = centerxy[1];
        info = probe(this.currentX, this.currentY);
        if (info == '0') { // recursively probe all safe cells
            // first get all the neighbours of the current cell
            recursivelyProbeZeros(info);
        }

        useSat();
    }

    /**
     * this applies the SPS to a given unprobed cell
     * @param unprobedCell
     */
    private void applySPS(int[] unprobedCell) {

        Vector<int[]> probedNeighbours = getProbedNeighbours(unprobedCell);
        for (int[] probedNeighbour: probedNeighbours) {
            int clues = Character.getNumericValue(game.getCell(probedNeighbour).getInfo());
            int flagsCount = getCountNeighbourFlags(probedNeighbour);
            int unprobeUnflagCount = getUnprobedUnflagCount(probedNeighbour);
            if (clues == flagsCount) {
                char info = probe(unprobedCell[0], unprobedCell[1]);
                if (info == '0') {
                    this.currentX = unprobedCell[0];
                    this.currentY = unprobedCell[1];
                    recursivelyProbeZeros(info);
                }
                updateKnowledgeBase();
                break;
            } else if (clues - flagsCount == unprobeUnflagCount) {
                setFlag(unprobedCell);
                break;
            } else {
                continue;
            }
        }

    }

    /**
     * Use the SATS
     */
    private void useSat() {

        FormulaFactory f = new FormulaFactory();
        PropositionalParser p = new PropositionalParser(f);

        updateKnowledgeBase();
        // Get a list of all unmarked cells
        unmarkedCells = game.getUnmarkedCells();

        int i = 0;
        while (true) {
            for (int j = 0; j < unmarkedCells.size(); j++) {

                Cell unmarked = unmarkedCells.get(j);
                if (!unmarked.isProbed() && !unmarked.isFlagged()) {

                    try {
                        // create the knowledge string with the addition of the unmarked or unflagged cell
                        Formula formula = p.parse(knowledgeString + "&T" + Integer.toString(unmarked.getX()) + Integer.toString(unmarked.getY()));
                        final SATSolver miniSat = MiniSat.miniSat(f);
                        miniSat.add(formula);
                        final Tristate result = miniSat.sat();

                        // on the basis of the outcome of the solver perform either of the three actions
                        switch (result) {
                            case TRUE:
                                applySPS(new int[]{unmarked.getX(), unmarked.getY()});
                                updateKnowledgeBase();
                                break;
                            case FALSE:
                                // no tornado, cell is safe to probe
                                this.currentX = unmarked.getX();
                                this.currentY = unmarked.getY();
                                char info = probe(unmarked.getX(), unmarked.getY());
                                if (info == '0') {
                                    recursivelyProbeZeros(info);
                                }
                                updateKnowledgeBase();
                                break;
                            case UNDEF:
                                applySPS(new int[]{unmarked.getX(), unmarked.getY()});
                                updateKnowledgeBase();

                                break;
                        }
                    } catch (ParserException pe) {
                        System.out.println(pe);
                    }
                }
            }
            if (game.getUnmarkedCells().isEmpty()) {
                break;
            }
            steps += 1;
            if (steps > maxSteps) {
                // agent can't solve so exit the loop
                break;
            }
        }

        System.out.println("Final map");
        displayGameState();

        if (!loss) {}

        this.loss = !game.hasFoundAllTornadoes();
        if (this.loss && steps > maxSteps) {
            System.out.println("Result: Agent not terminated");
            // print game outcome
        } else if (!this.loss) {
            System.out.println("Result: Agent alive: all solved");
        } else {
            System.out.println("Result: Agent dead: found mine");

        }

    }

    /**
     * After ever probe its necessary to add the new clues to the knowledge base.
     * Hence update this function updates the knowledgebase
     */
    private void updateKnowledgeBase() {
        // get a list of all probed cells
        Vector<Cell> probedCells = game.getProbedCells();

        // Create the KB from the probed Cells
        knowledgeString = convertKB(probedCells);
    }

    /**
     *This method returns all permutations of a given ArrayList of strings.
     * @param list the ArrayList of strings to be permuted
     * @return an ArrayList of ArrayList of strings containing all possible permutations of the input list
     */
    public ArrayList<ArrayList<String>> makePermutations(ArrayList<String> list) {
        if (list.size() == 0) {
            ArrayList<ArrayList<String>> result = new ArrayList<>();
            result.add(new ArrayList<>());
            return result;
        }
        ArrayList<ArrayList<String>> list2 = new ArrayList<>();
        String firstElement = list.remove(0);
        ArrayList<ArrayList<String>> recursiveReturn = makePermutations(list);
        for (ArrayList<String> li : recursiveReturn) {
            for (int index = 0; index <= li.size(); index++) {
                ArrayList<String> temp = new ArrayList<>(li);
                temp.add(index, firstElement);
                list2.add(temp);
            }
        }
        return list2;
    }

    private int getCountNeighbourFlags(int[] probedNeighbour) {
        int neighbourFlagsCount = 0;
        int[][] directions = {{-1,-1}, {-1,0}, {0,-1}, {0,1}, {1,0}, {1,1}};
        for (int i = 0; i < 6; i++) {
            int newX = probedNeighbour[0] + directions[i][0];
            int newY = probedNeighbour[1] + directions[i][1];
            if (newX >= 0 && newX < game.getSize() && newY >= 0 && newY < game.getSize()) {
                int[] neighbour = {newX, newY};
                if (game.getCell(neighbour[0],neighbour[1]).isFlagged()) {
                    neighbourFlagsCount += 1;
                }
            }
        }
        return neighbourFlagsCount;
    }

    /**
     *
     * Constructs a clause from all the discovered cells surrounding the given cell.
     * A clause is a disjunction of literals, where a literal is either a cell marked as danger,
     * represented by "Txy"
     * @param cell the cell for which the clause is being constructed
     * @return a string representing the clause constructed from the discovered cells around the given cell
     */
    public String makeClause(Cell cell) {
        // get all the neighbours of the cell
        Vector<int[]> neighbours = getNeighbours(cell.getX(), cell.getY());
        // contains unknown neighbours of parameter cell
        ArrayList<Cell> unknownCells = new ArrayList<>();
        // contains marked neighbours of parameter cell
        ArrayList<Cell> flaggedNeighbours = new ArrayList<>();
        ArrayList<String> flaggedLiterals = new ArrayList<>();

        // add the flaggedNeighbours and unmarked unflagged cells lists
        for (int[] c : neighbours) {
            if (game.getCell(c[0],c[1]).isFlagged()) {
                flaggedNeighbours.add(game.getCell(c[0],c[1]));
            } else if (!game.getCell(c[0],c[1]).isProbed()) {
                unknownCells.add(game.getCell(c[0],c[1]));
            }
        }

        // for each of the cell create a literal
        ArrayList<String> literals = new ArrayList<>();
        for (Cell unknownCell : unknownCells) {
            literals.add("T" + unknownCell.getX() + unknownCell.getY());
        }
        for (Cell markedCell: flaggedNeighbours) {
            flaggedLiterals.add("T" + markedCell.getX() + markedCell.getY());
        }

        // get the clues on neighbouring tornadoes
        int nTornadoes = Character.getNumericValue(cell.getInfo());
        int nUnknowns = unknownCells.size(); // number of neighbouring cells that are unknown
        // number of neihbouring cells marked as flagged
        int nMarked = getCountNeighbourFlags(new int[] {cell.getX(),cell.getY()});

        // generate all the permutations used for negation
        ArrayList<ArrayList<String>> clauseCombinations = makePermutations(literals);
        for (int i = 0; i < clauseCombinations.size(); i++) {
            ArrayList<String> presentClause = clauseCombinations.get(i);
            for (int j = 0; j < nUnknowns - (nTornadoes - nMarked); j++) {
                String clause = presentClause.get(j);
                presentClause.remove(clause);
                clause = "~" + clause;
                presentClause.add(0, clause);
            }
        }

        // build the logical formula string
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < clauseCombinations.size(); i++) {
            ArrayList<String> currentClause = clauseCombinations.get(i);
            stringBuilder.append("(");
            for (int j = 0; j < currentClause.size(); j++) {
                String clause = currentClause.get(j);
                stringBuilder.append(clause);
                stringBuilder.append("&");
            }
            // delete trailing &
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.append(")");
            stringBuilder.append("|");
        }

        // delete trailing |
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        String finalString =stringBuilder.toString();
        return finalString;
    }


    /**
     * Function that takes all uncovered cells and forms a knowledge base string
     * from the discovered clues
     * @param probedCells: list of Cells that are probed
     * @return
     */
    public String convertKB(Vector<Cell> probedCells) {

        // instantiate a string builder for your knowledge string
        StringBuilder stringBuilder = new StringBuilder();
        String finalString;
        
        // iterate over all probed cells
        for (int i = 0; i < probedCells.size(); i++) {
            Cell cell = probedCells.get(i);
            int[] coord = new int[] {cell.getX(), cell.getY()};
            if (getUnprobedUnflagCount(coord) > 0) { // check if count of unmarked unflagged > 0
                // for each cell, get a single clause
                String kClause = makeClause(cell); // clause for your Knowlegede base
                if (kClause != "") {
                    stringBuilder.append("(");
                    stringBuilder.append(kClause);
                    stringBuilder.append(")");
                    stringBuilder.append("&");
                }
            }
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        finalString = stringBuilder.toString();

        return finalString;
    }
}
