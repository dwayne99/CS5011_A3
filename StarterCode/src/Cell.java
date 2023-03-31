/**
 * The Cell Class represents the Cell on the game board
 */
public class Cell {
    private int x;
    private int y;
    private boolean isProbed;
    private char info;
    private boolean isFlagged;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.isProbed = false;
        this.info = ' ';
        this.isFlagged = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isProbed() {
        return isProbed;
    }

    public void setProbed(boolean isProbed) {
        this.isProbed = isProbed;
    }

    public char getInfo() {
        return info;
    }

    public void setInfo(char info) {
        this.info = info;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void setFlagged(boolean isFlagged) {
        this.isFlagged = isFlagged;
    }
}
