import java.util.Vector;

/**
 * The Game class represents a game of Tornado Sweeper.
 * It contains the game state, the game board, and methods to interact with the game.
 *
 */
public class Game {
    private int size; // size of the board
    private int numTornadoes; // keeps track of tornado count
    private Cell[][] cells; // Board representation in the form of Cells
    private int numCellsProbed; // Count of cells probed
    private char[][] gameState; // The present state of the board used at the time of printing the board
    private char[][] map; // the map given my the user
    private boolean loss; // outcome of the game
    private int numFlags; // count of flags placed on the board

    /** Constructor
     * Creates a new Game object with the specified game board.
     * The game state is initially unknown.
     *
     * @param map the game board, represented as a 2D array of characters inputed by the user
     */
    public Game(char[][] map) {
        this.map = map;
        this.size = map.length;
        this.numTornadoes = 0;
        this.cells = new Cell[size][size];
        this.numCellsProbed = 0;
        this.numFlags = 0;
        this.gameState = new char[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                char info = map[i][j];
                if (info == 't') {
                    numTornadoes++;
                }
                cells[i][j] = new Cell(i, j);
                gameState[i][j] = '?';
            }
        }
        loss = false;
    }

    /**
     * Returns the game state, represented as a 2D array of characters.
     * Each cell is either unknown ('?'), probed (a digit or '-'), or flagged ('*').
     *
     * @return the game state
     */
    public char[][] getGameState() {
        return gameState;
    }

    /**
     * Returns the size of the game board.
     *
     * @return the size of the game board
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the number of tornadoes on the game board.
     *
     * @return the number of tornadoes
     */
    public int getNumTornadoes() {
        return numTornadoes;
    }

    /**
     * Returns the `Cell` object at the specified position on the game board.
     *
     * @param x the row index
     * @param y the column index
     * @return the `Cell` object at the specified position
     */
    public Cell getCell(int x, int y) {
        return cells[x][y];
    }

    /**
     * Returns the `Cell` object at the specified position on the game board.
     *
     * @param xy an array containing the row and column indices
     * @return the `Cell` object at the specified position
     */
    public Cell getCell(int[] xy) {
        return cells[xy[0]][xy[1]];
    }

    /**
     * Returns a 2D array of all `Cell` objects on the game board.
     *
     * @return a 2D array of all `Cell` objects
     */
    public Cell[][] getCells() {
        return cells;
    }

    /**
     * Returns a vector of all unprobed and unflagged cells on the game board.
     * In other words, returns all the cells that are unmarked and covered
     *
     * @return a vector of all unprobed and unflagged cells
     */
    public Vector<int[]> getUnprobedUnFlaggedCells() {
        Vector<int[]> unprobedUnflaggedcells = new Vector<>();
        for(int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (!cells[x][y].isProbed() && !cells[x][y].isFlagged()) {
                    unprobedUnflaggedcells.add(new int[]{x,y});
                }
            }
        }
        return unprobedUnflaggedcells;
    }

    /**
     * Returns the number of cells that have been probed.
     *
     * @return the number of cells that have been probed
     */
    public int getNumCellsProbed() {
        return numCellsProbed;
    }

    /**
     * Returns the state of the cell at the specified position on the game board.
     * The state is either a digit ('0' to '6'), or a tornado ('t').
     *
     * @param x the row index
     * @param y the column index
     * @return the state of the cell at the specified position
     */
    public char getCellState(int x, int y) {
        return map[x][y];
    }

    /**
     * Returns the game outcome as a boolean value.
     *
     * @return true if the game is lost, false if the game is won.
     */
    public boolean getGameOutCome() {
        if (numCellsProbed - 1 != size * size - numTornadoes) { // you've lost the game
            return true; // loss => true
        } else { // you've won
            return false; // loss => false
        }
    }

    /**
     * Checks if all tornadoes have been flagged.
     *
     * @return true if all tornadoes have been flagged, false otherwise.
     */
    public boolean hasFoundAllTornadoes() {
        if (numFlags == numTornadoes) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Prints the outcome of the game.
     *
     * @param loss a boolean value indicating whether the game was lost or not.
     */
    public void printGameOutcome(boolean loss) {
        if (loss) {
            System.out.println("Result: Agent dead: found mine");
        } else {
            System.out.println("Result: Agent alive: all solved");
        }
    }

    /**
     * Finds the position of the first unprobed cell in the game board.
     * Used by the basic agent
     * @return an array containing the x and y coordinates of the first unprobed cell, or null if no unprobed cells are found.
     */
    public int[] getTopLeft() {
        for (int x = 0; x < size; x++) { // rows of the board
            for (int y = 0; y < size; y++) { // columns of the board
                // Check if the cell is not probed
                if (!cells[x][y].isProbed() && !cells[x][y].isFlagged()) {
                    return new int[] { x, y };// Return the position of the first unprobed cell
                }
            }
        }
        return null;
    }

    /**
     * Updates the state of a cell based on the information obtained by probing it.
     *
     * @param x the x coordinate of the cell.
     * @param y the y coordinate of the cell.
     * @param info the information obtained by probing the cell.
     */
    public void updateCells(int x, int y, char info) {
        numCellsProbed += 1;
        cells[x][y].setProbed(true);
        cells[x][y].setInfo(info);
        if (info != 't') {
            gameState[x][y] = info;
        } else {
            gameState[x][y] = '-';
        }
    }

    /**
     * Checks if all cells except for the tornadoes have been probed.
     *
     * @return true if all cells except for the tornadoes have been probed, false otherwise.
     */
    public boolean hasWon() {
        if (numCellsProbed == size * size - numTornadoes) { // you've lost the game
            return true; // loss => true
        } else { // you've won
            return false; // loss => false
        }
    }

    /**
     * Flags a cell as containing a tornado.
     *
     * @param coord an array containing the x and y coordinates of the cell to be flagged.
     */
    public void putFlag(int[] coord) {
        int x = coord[0];
        int y = coord[1];

        numFlags += 1;
        cells[x][y].setFlagged(true);
        gameState[x][y] = '*';
    }

    /**
     * Returns the coordinates of the center of the game board.
     *
     * @return an array containing the x and y coordinates of the center of the game board.
     */
    public int[] center() {
        return new int[]{size/2,size/2};
    }

    /**
     * Returns a vector containing all cells that have been probed.
     *
     * @return a vector containing all cells that have been probed.
     */
    public Vector<Cell> getProbedCells() {
        Vector<Cell> probedCells = new Vector<>();
        for (int i = 0; i < size; i++) {
            for (int j =0; j < size; j++) {
                if (cells[i][j].isProbed()) {
                    probedCells.add(cells[i][j]);
                }
            }
        }
        return probedCells;
    }

    /**
     * Returns a vector containing all cells that have not been probed or flagged.
     *
     * @return a vector containing all cells that have not been probed or flagged.
     */
    public Vector<Cell> getUnmarkedCells() {
        Vector<Cell> unmarkedCells = new Vector<>();
        for (int i = 0; i < size; i++) {
            for (int j =0; j < size; j++) {
                if (!cells[i][j].isProbed() && !cells[i][j].isFlagged()) {
                    unmarkedCells.add(cells[i][j]);
                }
            }
        }
        return unmarkedCells;
    }
}


