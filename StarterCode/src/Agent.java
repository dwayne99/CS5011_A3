import java.util.Vector;

/**
 * Represents an agent that plays the Minesweeper game. The agent can probe cells, set flags, and recursively probe adjacent cells when a zero is found.
 * This is inherrited by the Basic, Beginner and Intermediate agents
 */
public class Agent {
    protected Game game; // game being played
    protected int currentX; // the X position of the agent
    protected int currentY; // the Y position of the agent

    protected boolean verbose; // used for printing the game state at various iterations
    protected boolean loss; // check if the agent has lost or not
    protected Vector<int[]> cellsToProbe = new Vector<>(); // list of all the cells that are needed to be probed

    /**
     * Creates a new instance of the Agent class.
     * @param game The Minesweeper game being played.
     * @param startX The starting X coordinate of the agent.
     * @param startY The starting Y coordinate of the agent.
     * @param verbose Whether the agent should print out information during the game.
     */
    public Agent(Game game, int startX, int startY, boolean verbose) {
        this.game = game;
        this.currentX = startX;
        this.currentY = startY;
        this.verbose = verbose;
    }

    /**
     * Prints out the current state of the game.
     */
    public void displayGameState() {
        A3main.printBoard(game.getGameState());
    }

    /**
     * Gets the neighbouring cells of the current cell.
     * @return A vector of int arrays representing the neighbouring cells.
     */
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

    /**
     * Gets the neighbouring cells of a specific cell.
     * @param X The X coordinate of the cell.
     * @param Y The Y coordinate of the cell.
     * @return A vector of int arrays representing the neighbouring cells.
     */
    public Vector<int[]> getNeighbours(int X, int Y) {
        Vector<int[]> neighbours = new Vector<>();
        int[][] directions = {{-1,-1}, {-1,0}, {0,-1}, {0,1}, {1,0}, {1,1}};
        for (int i = 0; i < 6; i++) {
            int newX = X + directions[i][0];
            int newY = Y + directions[i][1];
            if (newX >= 0 && newX < game.getSize() && newY >= 0 && newY < game.getSize()) {
                int[] neighbour = {newX, newY};
                neighbours.add(neighbour);
            }
        }
        return neighbours;
    }

    /**
     * Adds neighbouring unprobed cells to the probe queue.
     */
    protected void addCellstoProbeQueue() {
        Vector<int[]> neighbours = getNeighbours();
        for (int[] coord: neighbours) {
            if (!containsArray(cellsToProbe, coord) && !game.getCell(coord[0], coord[1]).isProbed()) {
                cellsToProbe.add(coord);
            }
        }
    }


    /**
     * Returns the cell state at the specified (x,y) coordinate and updates the game board accordingly.
     * @param x the x-coordinate of the cell to probe
     * @param y the y-coordinate of the cell to probe
     * @return the current state of the cell at the specified (x,y) coordinate
     */
    protected char probe(int x, int y) {
        char info = game.getCellState(x, y);
        game.updateCells(x, y, info);
        return info;
    }

    /**
     * Recursively probes all the adjacent cells to the current cell that have a state of '0'.
     * Since all neighbours of 0 are safe
     * @param info the state of the current cell
     */
    protected void recursivelyProbeZeros(char info) {
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

    /**
     * Determines whether a vector of integer arrays contains a specified array.
     * @param vec the vector of integer arrays to search
     * @param arr the array to search for in the vector
     * @return true if the vector contains the specified array, false otherwise
     */
    protected static boolean containsArray(Vector<int[]> vec, int[] arr) {
        for (int[] vecArr : vec) {
            if (java.util.Arrays.equals(vecArr, arr)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Flags the specified cell on the game board.
     * @param coord the (x,y) coordinates of the cell to flag
     */
    protected void setFlag(int [] coord) {
        game.putFlag(coord);
    }

    /**
     * Returns a vector of all neighbouring cells that are probed of the specified unprobed cell.
     * @param unprobedCell the (x,y) coordinates of the unprobed cell
     * @return a vector containing the (x,y) coordinates of all the probed neighboring cells
     */
    protected Vector<int[]> getProbedNeighbours(int[] unprobedCell) {
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
