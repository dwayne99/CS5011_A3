import java.util.Vector;

public class Agent {
    protected Game game;
    protected int currentX;
    protected int currentY;

    protected boolean verbose;

    protected boolean loss;
    protected Vector<int[]> cellsToProbe = new Vector<>();

    public Agent(Game game, int startX, int startY, boolean verbose) {
        this.game = game;
        this.currentX = startX;
        this.currentY = startY;
        this.verbose = verbose;
    }

//    public void probeCell(int x, int y) {
//        // Probe the cell at (x,y)
//        game.getCell(x, y).setProbed(true);
//
//        // Update the game state
//        char info = game.getCell(x, y).getInfo();
//        if (info == 't') {
//            game.setGameState(x, y, 't');
//            game.setNumTornadoes(game.getNumTornadoes() - 1);
//        } else {
//            game.setGameState(x, y, Character.forDigit(info, 10));
//        }
//
//        game.setNumCellsProbed(game.getNumCellsProbed() + 1);
//    }

    public void displayGameState() {
        A3main.printBoard(game.getGameState());
    }

    public Vector<int[]> getNeighbours() {
        Vector<int[]> neighbours = new Vector<>();
        int[][] directions = {{-1,-1}, {-1,0}, {0,-1}, {0,1}, {1,0}, {1,1}};
        for (int i = 0; i < 6; i++) {
            int newX = currentX + directions[i][0];
            int newY = currentY + directions[i][1];
            if (newX >= 0 && newX < game.getSize() && newY >= 0 && newY < game.getSize()) {
                int[] neighbour = {newX, newY};
                neighbours.add(neighbour);
            }
        }
        return neighbours;
    }

    protected void addCellstoProbeQueue() {
        Vector<int[]> neighbours = getNeighbours();
        for (int[] coord: neighbours) {
            if (!containsArray(cellsToProbe, coord) && !game.getCell(coord[0], coord[1]).isProbed()) {
                cellsToProbe.add(coord);
            }
        }
    }

    protected char probe(int x, int y) {
        char info = game.getCellState(x, y);
        game.updateCells(x, y, info);
        return info;
    }

    protected static boolean containsArray(Vector<int[]> vec, int[] arr) {
        for (int[] vecArr : vec) {
            if (java.util.Arrays.equals(vecArr, arr)) {
                return true;
            }
        }
        return false;
    }
}
