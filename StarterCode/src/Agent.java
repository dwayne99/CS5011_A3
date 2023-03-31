import java.util.Vector;

public class Agent {
    protected Game game;
    protected int currentX;
    protected int currentY;

    protected boolean verbose;

    protected boolean loss;

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

//    public int[][] getNeighbours() {
//        int[][] neighbours = new int[6][2];
//        int[][] directions = {{-1,-1}, {-1,0}, {0,-1}, {0,1}, {1,0}, {1,1}};
//        for (int i = 0; i < 6; i++) {
//            int newX = currentX + directions[i][0];
//            int newY = currentY + directions[i][1];
//            if (newX >= 0 && newX < game.getSize() && newY >= 0 && newY < game.getSize()) {
//                neighbours[i][0] = newX;
//                neighbours[i][1] = newY;
//            }
//        }
//        return neighbours;
//    }
}
