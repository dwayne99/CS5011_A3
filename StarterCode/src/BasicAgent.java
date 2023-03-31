import java.awt.*;
import java.util.Vector;

public class BasicAgent extends Agent {


    private Vector<int[]> cellsToProbe = new Vector<>();

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

                // first get all the neighbours of the current cell
                addCellstoProbeQueue();

                while (!cellsToProbe.isEmpty()) {
                    int[] coord = cellsToProbe.remove(0);
                    this.currentX = coord[0];
                    this.currentY = coord[1];
                    info = probe(this.currentX,this.currentY);
                    if (info == '0') {
                        addCellstoProbeQueue();
                    }

                }
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

    private void addCellstoProbeQueue() {
        Vector<int[]> neighbours = getNeighbours();
        for (int[] coord: neighbours) {
            if (!containsArray(cellsToProbe, coord) && !game.getCell(coord[0], coord[1]).isProbed()) {
                cellsToProbe.add(coord);
            }
        }
    }

    private char probe(int currentX, int currentY) {
        char info = game.getCellState(this.currentX, this.currentY);
        game.updateCells(currentX, currentY, info);
        return info;
    }

    public static boolean containsArray(Vector<int[]> vec, int[] arr) {
        for (int[] vecArr : vec) {
            if (java.util.Arrays.equals(vecArr, arr)) {
                return true;
            }
        }
        return false;
    }
}
