import org.logicng.datastructures.Tristate;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

public class IntermediateAgent extends Agent {
    String kbString;
    Vector<Cell> unmarkedCells = new Vector<>();
    int steps;
    int maxSteps = 100;

    public IntermediateAgent(Game game, int startX, int startY, boolean verbose) {
        super(game, startX, startY, verbose);
    }

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

//        displayGameState();

        useSat();


    }
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
                updateKB();
                break;
            } else if (clues - flagsCount == unprobeUnflagCount) {
                setFlag(unprobedCell);
                break;
            } else {
                continue;
            }
        }

    }

    private void useSat() {

        FormulaFactory f = new FormulaFactory();
        PropositionalParser p = new PropositionalParser(f);

        updateKB();
        // Get a list of all unmarked cells
        unmarkedCells = game.getUnmarkedCells();

        int i = 0;
        while (true) {
            for (int j = 0; j < unmarkedCells.size(); j++) {

                Cell unmarked = unmarkedCells.get(j);
                if (!unmarked.isProbed() && !unmarked.isFlagged()) {

                    try {
                        Formula formula = p.parse(kbString + "&T" + Integer.toString(unmarked.getX()) + Integer.toString(unmarked.getY()));
                        final SATSolver miniSat = MiniSat.miniSat(f);
                        miniSat.add(formula);
                        final Tristate result = miniSat.sat();

                        switch (result) {
                            case TRUE:
                                applySPS(new int[]{unmarked.getX(), unmarked.getY()});
                                updateKB();
                                break;
                            case FALSE:
                                this.currentX = unmarked.getX();
                                this.currentY = unmarked.getY();
                                char info = probe(unmarked.getX(), unmarked.getY());
                                if (info == '0') {
                                    recursivelyProbeZeros(info);
                                }

                                updateKB();
                                break;
                            case UNDEF:
                                applySPS(new int[]{unmarked.getX(), unmarked.getY()});
                                updateKB();

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

    private void updateKB() {
        // get a list of all probed cells
        Vector<Cell> probedCells = game.getProbedCells();

        // Create the KB from the probed Cells
        kbString = convertKB(probedCells);
    }

    public ArrayList<ArrayList<String>> listPermutations(ArrayList<String> list) {

        if (list.size() == 0) {
            ArrayList<ArrayList<String>> result = new ArrayList<>();
            result.add(new ArrayList<>());
            return result;
        }

        ArrayList<ArrayList<String>> returnMe = new ArrayList<>();

        String firstElement = list.remove(0);

        ArrayList<ArrayList<String>> recursiveReturn = listPermutations(list);
        for (ArrayList<String> li : recursiveReturn) {
            for (int index = 0; index <= li.size(); index++) {
                ArrayList<String> temp = new ArrayList<>(li);
                temp.add(index, firstElement);
                returnMe.add(temp);
            }

        }
        return returnMe;
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
    public String createClause(Cell cell) {

        // get all the neighbours of the cell
        Vector<int[]> neighbours = getNeighbours(cell.getX(), cell.getY());
        // contains unknown neighbours of parameter cell
        ArrayList<Cell> unknownCells = new ArrayList<>();
        // contains marked neighbours of parameter cell
        ArrayList<Cell> markedNeighbours = new ArrayList<>();
        ArrayList<String> markedLiterals = new ArrayList<>();

        // populate the markedNeighbours and unknownCells lists
        for (int[] c : neighbours) {
            if (game.getCell(c[0],c[1]).isFlagged()) {
                markedNeighbours.add(game.getCell(c[0],c[1]));
            } else if (!game.getCell(c[0],c[1]).isProbed()) {
                unknownCells.add(game.getCell(c[0],c[1]));
            }
        }

        // create the literals of each cell
        ArrayList<String> literals = new ArrayList<>();
        for (Cell unknownCell : unknownCells) {
            literals.add("T" + unknownCell.getX() + unknownCell.getY());
        }
        for (Cell markedCell: markedNeighbours) {
            markedLiterals.add("T" + markedCell.getX() + markedCell.getY());
        }

        // number of neighbouring tornado cells
        int nTornadoes = Character.getNumericValue(cell.getInfo());
        // number of neighbouring cells that are unknown
        int nUnknowns = unknownCells.size();
        // number of neihbouring cells marked as dangers i.e. flagged
        int nMarked = getCountNeighbourFlags(new int[] {cell.getX(),cell.getY()});

        // get all the permutations, to be used when adding the negation
        ArrayList<ArrayList<String>> permutedClauses = listPermutations(literals);
        for (int i = 0; i < permutedClauses.size(); i++) {
            ArrayList<String> currentClause = permutedClauses.get(i);
            // nUnknowns - nTornados - nMarked is the number of free/safe cells around cell
            // used to get all possible scenarios
            for (int j = 0; j < nUnknowns - (nTornadoes - nMarked); j++) {
                String clause = currentClause.get(j);
                currentClause.remove(clause);
                clause = "~" + clause;
                currentClause.add(0, clause);
            }
        }

        // build the logical formula string
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < permutedClauses.size(); i++) {
            ArrayList<String> currentClause = permutedClauses.get(i);
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
        return stringBuilder.toString();

    }

    private int getUnprobedUnflagCount(int[] probedNeighbour) {
        int unprobedUnflagCount = 0;
        int[][] directions = {{-1,-1}, {-1,0}, {0,-1}, {0,1}, {1,0}, {1,1}};
        for (int i = 0; i < 6; i++) {
            int newX = probedNeighbour[0] + directions[i][0];
            int newY = probedNeighbour[1] + directions[i][1];
            if (newX >= 0 && newX < game.getSize() && newY >= 0 && newY < game.getSize()) {
                int[] neighbour = {newX, newY};
                if (!game.getCell(neighbour[0],neighbour[1]).isFlagged() && !game.getCell(neighbour[0],neighbour[1]).isProbed()) {
                    unprobedUnflagCount += 1;
                }
            }
        }
        return unprobedUnflagCount;
    }

    /**
     *
     * @param uncoveredCells: list of Cells that are probed
     * @return
     */
    public String convertKB(Vector<Cell> uncoveredCells) {

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < uncoveredCells.size(); i++) {
            Cell cell = uncoveredCells.get(i);
            int[] coord = new int[] {cell.getX(), cell.getY()};
            if (getUnprobedUnflagCount(coord) > 0) {
                // for each cell, get a single clause
                String clause = createClause(cell);
                if (clause != "") {
                    stringBuilder.append("(");
                    stringBuilder.append(clause);
                    stringBuilder.append(")");
                    stringBuilder.append("&");
                }
            }
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        return stringBuilder.toString();
    }
}
