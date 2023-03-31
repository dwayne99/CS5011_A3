public class Game {
    private int size;
    private int numTornadoes;
    private Cell[][] cells;
    private int numCellsProbed;


    private char[][] gameState;
    private char[][] map;

    private boolean loss;

    public Game(char[][] map) {
        this.map = map;
        this.size = map.length;
        this.numTornadoes = 0;
        this.cells = new Cell[size][size];
        this.numCellsProbed = 0;
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

    public char[][] getGameState() {
        return gameState;
    }

    public int getSize() {
        return size;
    }

    public int getNumTornadoes() {
        return numTornadoes;
    }

    public Cell getCell(int x, int y) {
        return cells[x][y];
    }

    public Cell[][] getCells() {
        return cells;
    }

    public int getNumCellsProbed() {
        return numCellsProbed;
    }

    public char getCellState(int x, int y) {
        return map[x][y];
    }

    public boolean getGameOutCome() {
        if (numCellsProbed - 1 != size * size - numTornadoes) { // you've lost the game
            return true; // loss => true
        } else { // you've won
            return false; // loss => false
        }
    }

    public void printGameOutcome(boolean loss) {
        if (loss) {
            System.out.println("Result: Agent dead: found mine");
        } else {
            System.out.println("Result: Agent alive: all solved");
        }
    }

    public int[] getTopLeft() {
        for (int x = 0; x < size; x++) { // rows of the board
            for (int y = 0; y < size; y++) { // columns of the board
                // Check if the cell is not probed
                if (!cells[x][y].isProbed()) {
                    return new int[] { x, y };// Return the position of the first unprobed cell
                }
            }
        }
        return null;
    }

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

    public boolean hasWon() {
        if (numCellsProbed == size * size - numTornadoes) { // you've lost the game
            return true; // loss => true
        } else { // you've won
            return false; // loss => false
        }
    }
}