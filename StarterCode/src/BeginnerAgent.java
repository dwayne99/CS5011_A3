import java.util.Vector;

public class BeginnerAgent extends  Agent{

    private int steps = 0;
    private int maxSteps = 500;

    Vector<int[]> unprobedCells = new Vector<>();
    public BeginnerAgent(Game game, int startX, int startY, boolean verbose) {
        super(game, startX, startY, verbose);
    }

    public void playGame() {

        // probe the initial cell
        char info = probe(this.currentX, this.currentY);
        if (info == '0') { // recursively probe all safe cells
            // first get all the neighbours of the current cell
            recursivelyUnprobeZeros(info);
        }
        //not '0' apply point strategy
        unprobedCells = game.getUnprobedUnFlaggedCells();

        applySPS(info);

        System.out.println("Final map");
        displayGameState();

        if (steps!= maxSteps) {
            this.loss = !game.hasFoundAllTornadoes();
            // print game outcome
            game.printGameOutcome(this.loss);
        } else {
            System.out.println("Result: Agent not terminated");
        }
    }

    private void applySPS(char info) {
        int[] centerxy = game.center();
        while(!unprobedCells.isEmpty()) {
            int[] unprobedCell = unprobedCells.remove(0);
            if (java.util.Arrays.equals(unprobedCell, centerxy)) { // safe clue
                info = probe(unprobedCell[0], unprobedCell[1]);
                if (info == '0') {
                    this.currentX = unprobedCell[0];
                    this.currentY = unprobedCell[1];
                    recursivelyUnprobeZeros(info);
                }
                continue;
            }
            Vector<int[]> probedNeighbours = getProbedNeighbours(unprobedCell);
            for (int[] probedNeighbour: probedNeighbours) {
                int clues = Character.getNumericValue(game.getCell(probedNeighbour).getInfo());
                int flagsCount = getCountNeighbourFlags(probedNeighbour);
                int unprobeUnflagCount = getUnprobedUnflagCount(probedNeighbour);
                if (clues == flagsCount) {
                    info = probe(unprobedCell[0], unprobedCell[1]);
                    if (info == '0') {
                        this.currentX = unprobedCell[0];
                        this.currentY = unprobedCell[1];
                        recursivelyUnprobeZeros(info);
                    }
                    break;
                } else if (clues - flagsCount == unprobeUnflagCount) {
                    setFlag(unprobedCell);
                    break;
                } else {
                    continue;
                }
            }
            if (!game.getCell(unprobedCell).isFlagged() )  {
                if (!game.getCell(unprobedCell).isProbed()) {
                    unprobedCells.add(unprobedCell);
                }
            }
            steps +=1;

            if (steps == maxSteps) {
                break;
            }
        }
    }


    private void setFlag(int [] coord) {
        game.putFlag(coord);
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

    private Vector<int[]> getProbedNeighbours(int[] unprobedCell) {
        Vector<int[]> probedNeighbours = new Vector<>();
        int[][] directions = {{-1,-1}, {-1,0}, {0,-1}, {0,1}, {1,0}, {1,1}};
        for (int i = 0; i < 6; i++) {
            int newX = unprobedCell[0] + directions[i][0];
            int newY = unprobedCell[1] + directions[i][1];
            if (newX >= 0 && newX < game.getSize() && newY >= 0 && newY < game.getSize()) {
                int[] neighbour = {newX, newY};
                if (game.getCell(neighbour[0],neighbour[1]).isProbed()) {
                    probedNeighbours.add(neighbour);
                }
            }
        }
        return probedNeighbours;
    }
}
