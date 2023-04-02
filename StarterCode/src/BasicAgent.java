public class BasicAgent extends Agent {



    public BasicAgent(Game game, int x, int y, boolean verbose) {
        super(game,x,y, verbose);
    }

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

            // based on the info perform various actions
            if (info == 't') {
                this.loss = game.getGameOutCome();
                if (!this.loss) {
                    game.updateCells(this.currentX,this.currentY,'?');
                }
                break;
            } else if (info >= '1' && info <= '6') {
                xyTopLeft = game.getTopLeft();
                this.currentX = xyTopLeft[0];
                this.currentY = xyTopLeft[1];
            } else { // if a '0' is encountered
                recursivelyProbeZeros(info);
            }

            if (verbose & !game.hasWon()) { // display initial state of the game
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
