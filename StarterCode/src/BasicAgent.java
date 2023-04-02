
/**
 * The BasicAgent class represents a basic Minesweeper agent that plays the game
 * by probing the top left cell and then taking actions based on the information
 * obtained. This agent does not use any advanced techniques to improve its
 * performance.
 */
public class BasicAgent extends Agent {

    /**
     * Constructs a new BasicAgent with the given game, starting coordinates, and
     * verbose mode.
     *
     * @param game    the Minesweeper game to play
     * @param x       the starting x-coordinate
     * @param y       the starting y-coordinate
     * @param verbose whether to display the game state after each action
     */
    public BasicAgent(Game game, int x, int y, boolean verbose) {
        super(game,x,y, verbose);
    }


    /**
     * Plays the Minesweeper game using the basic strategy of probing the top left
     * cell and then taking actions based on the information obtained. This method
     * runs until the game ends, either through a win or a loss.
     */
    public void playGame() {

        if (verbose) { // display initial state of the game
            displayGameState();
        }
        while (true) {
            // Step 1: Probe the cell
            int[] xyTopLeft = game.getTopLeft();
            this.currentX = xyTopLeft[0];
            this.currentY = xyTopLeft[1];
            char info = probe(this.currentX, this.currentY);

            // Step 2: Take action based on the info and perform various actions
            if (info == 't') {
                // game over
                this.loss = game.getGameOutCome();
                if (!this.loss) {
                    game.updateCells(this.currentX,this.currentY,'?');
                }
                break;
            } else if (info >= '1' && info <= '6') {
                // probe the top-left
                xyTopLeft = game.getTopLeft();
                this.currentX = xyTopLeft[0];
                this.currentY = xyTopLeft[1];
            } else { // if a '0' is encountered
                // probe all the adjacent cells recursively
                recursivelyProbeZeros(info);
            }

            if (verbose & !game.hasWon()) {
                // display the state of the game
                displayGameState();
            }
        }

        // print final map
        System.out.println("Final map");
        displayGameState();

        // print game outcome
        game.printGameOutcome(this.loss);

    }
}
