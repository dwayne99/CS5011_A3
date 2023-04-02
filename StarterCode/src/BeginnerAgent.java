import java.util.Vector;

/**
 * The BeginnerAgent class represents an agent that applies the single point strategy (SPS) to play the tornado sweeper game.
 * This agent probes cells until it finds a non-zero cell, then applies the point strategy to identify safe cells and flag mines.
 * If it probes a cell with a '0' clue, it recursively probes all neighbouring cells with '0' clues.
 * The agent also probes the center cell of the game board.
 */
public class BeginnerAgent extends  Agent{

    private int steps = 0;
    private int maxSteps = 100; // used to determine if the agent is stuck in a loop
    Vector<int[]> unprobedCells = new Vector<>();

    /**
     * Constructs a BeginnerAgent object.
     * @param game the tornado sweeper game to play
     * @param startX the x-coordinate of the initial cell to probe
     * @param startY the y-coordinate of the initial cell to probe
     * @param verbose whether to print out the state of the game after each move
     */
    public BeginnerAgent(Game game, int startX, int startY, boolean verbose) {
        super(game, startX, startY, verbose);
    }

    /**
     * Plays the game using SPS
     */
    public void playGame() {

        // probe the initial cell
        char info = probe(this.currentX, this.currentY);
        if (info == '0') { // recursively probe all safe cells
            // first get all the neighbours of the current cell
            recursivelyProbeZeros(info);
        }

        // probe center cell
        int[] centerxy = game.center();
        info = probe(centerxy[0], centerxy[1]);
        if (info == '0') {
            this.currentX = centerxy[0];
            this.currentY = centerxy[1];
            recursivelyProbeZeros(info);
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

    /**
     * Applies the singlr point strategy to the unprobed and unflagged cells.
     *    @param info the clue of the last probed cell
     */
    private void applySPS(char info) {
        while(!unprobedCells.isEmpty()) {
            int[] unprobedCell = unprobedCells.remove(0);
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
                        recursivelyProbeZeros(info);
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


    /**
     *  Returents the count of the flags surrounding a cell: probedNeighbour
     * @param probedNeighbour: the center cell for whose neighbours have to be investigated1
     * @return
     */
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

}
