package gameEngine;

/**
 * Represents a cell in the grid.
 * Each cell has a row and column index.
 * Cells are square and have a fixed size, determined by 
 * the size of the game window and the number of rows and columns. 
 */
public class Cell {
    public static final int SQUARE_SIZE = TowerDefenseMain.GAME_WINDOW_SIZE.width / Level.NUM_COLS;

    private int row;
    private int col;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public static Cell getCellFromCoordinates(int pixelX, int pixelY) {
        return new Cell(pixelY / SQUARE_SIZE,pixelX / SQUARE_SIZE);
    }

    public int getPixelX() {
        return this.col * SQUARE_SIZE;
    }

    public int getPixelY() {
        return this.row * SQUARE_SIZE;
    }

    public int getRow() {
        return this.row;
    }

    public int getColumn() {
        return this.col;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Cell)) {
            return false;
        }
        return this.row == ((Cell) other).row && this.col == ((Cell) other).col;
    }
}
