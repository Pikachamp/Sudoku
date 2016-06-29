package sudoku.gui.model;

import sudoku.model.Board;

import java.util.Arrays;
import java.util.Observable;

/**
 * The data that is displayed by the GUI with the option to convert it into a
 * {@link sudoku.model.Board} and solve it.
 */
public class DisplayedSudoku extends Observable implements Cloneable {
    private int boxRows;
    private int boxCols;
    private int[][] board;
    private boolean[][] changeable;

    /**
     * The constant to indicate that a cell is not yet set.
     */
    public final int UNSET_CELL = Board.UNSET_CELL;

    /**
     * Creates a new Sudoku board that has {@code boxRows * boxCols} rows and
     * columns.
     *
     * @param boxRows The number of rows per box.
     * @param boxCols The number of columns per box.
     */
    public DisplayedSudoku (int boxRows, int boxCols) {
        if (boxRows < 1 || boxCols < 1) {
            throw new IllegalArgumentException("There must be at least one row "
                    + "and one column per Box of the Sudoku!");
        }
        int cellsPerStructure = boxRows * boxCols;
        this.boxRows = boxRows;
        this.boxCols = boxCols;
        this.board = new int[cellsPerStructure][cellsPerStructure];
        this.changeable = new boolean[cellsPerStructure][cellsPerStructure];
    }

    /**
     * Returns a two-dimensional Array containing the number that is set in the
     * cell of this position or {@link #UNSET_CELL} if the cell is not set.
     *
     * @return The content of the Sudoku cells.
     */
    public int[][] getBoard () {
        int [][] boardClone = new int[boxRows * boxCols][boxRows * boxCols];
        for (int i = 0; i < board.length; i++) {
            boardClone[i] = Arrays.copyOf(board[i], board[i].length);
        }
        return boardClone;
    }
}