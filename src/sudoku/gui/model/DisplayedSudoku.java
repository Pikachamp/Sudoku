package sudoku.gui.model;

import sudoku.model.Board;
import sudoku.model.InvalidSudokuException;
import sudoku.model.Structure;
import sudoku.model.SudokuBoard;

import java.util.Arrays;
import java.util.Observable;

/**
 * The data that is displayed by the GUI with the option to convert it into a
 * {@link sudoku.model.Board} and solve it.
 */
public class DisplayedSudoku extends Observable implements Cloneable {
    private final int boxRows;
    private final int boxCols;
    private final int cellsPerStructure;
    private int[][] board;
    private boolean[][] changeable;

    /**
     * The constant to indicate that a cell is not yet set.
     */
    public static final int UNSET_CELL = Board.UNSET_CELL;

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
        this.cellsPerStructure = boxRows * boxCols;
        this.board = new int[cellsPerStructure][cellsPerStructure];
        this.changeable = new boolean[cellsPerStructure][cellsPerStructure];
        for (int i = 0; i < cellsPerStructure; i++) {
            for (int j = 0; j < cellsPerStructure; j++) {
                board[i][j] = DisplayedSudoku.UNSET_CELL;
                changeable[i][j] = true;
            }
        }
    }

    /**
     * Creates a deep copy of {@code original}
     *
     * @param original The DisplayedSudoku to be cloned.
     */
    private DisplayedSudoku (DisplayedSudoku original) {
        this.boxRows = original.boxRows;
        this.boxCols = original.boxCols;
        this.cellsPerStructure = original.cellsPerStructure;
        this.board = new int[cellsPerStructure][cellsPerStructure];
        for (int i = 0; i < cellsPerStructure; i++) {
            this.board[i] = Arrays.copyOf(original.board[i], cellsPerStructure);
        }
        this.changeable = new boolean[cellsPerStructure][cellsPerStructure];
        for (int i = 0; i < cellsPerStructure; i++) {
            this.changeable[i] = Arrays.copyOf(original.changeable[i],
                    cellsPerStructure);
        }
    }

    /**
     * Returns the number of cells per structure of this Sudoku.
     *
     * @return the number of cells per structure of this Sudoku.
     */
    public int getNumbers () {
        return cellsPerStructure;
    }

    /**
     * Returns a two-dimensional Array containing the number that is set in the
     * cell of this position or {@link #UNSET_CELL} if the cell is not set.
     *
     * @return The content of the Sudoku cells.
     */
    public int[][] getBoard () {
        int [][] boardClone = new int[cellsPerStructure][cellsPerStructure];
        for (int i = 0; i < board.length; i++) {
            boardClone[i] = Arrays.copyOf(board[i], board[i].length);
        }
        return boardClone;
    }

    /**
     * Sets the cell specified by its coordinates to the given value. Specifies
     * if the cell is going to be changeable or not.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @param number The number to be set into the cell.
     * @param isChangeable Specifies whether the cell will be changeable or not.
     */
    public void setCell (int row, int col, int number, boolean isChangeable) {
        if (row < 0 || col < 0 || row >= cellsPerStructure
                || col >= cellsPerStructure || number < 1
                || number > cellsPerStructure) {
            throw new IllegalArgumentException("Error! Tried to set an invalid "
                    + "number or a cell outside of the board. Watch out that "
                    + "the cells are indexed beginning with 0!");
        }
        if (!changeable[row][col]) {
            throw new IllegalArgumentException("Error! Tried to overwrite the "
                    + "value of a cell given by the file!");
        }
        board[row][col] = number;
        changeable[row][col] = isChangeable;
        setChanged();
        notifyObservers(getBoard());
    }

    /**
     * Unsets the specified cell.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     */
    public void unsetCell (int row, int col) {
        if (row < 0 || col < 0 || row >= cellsPerStructure
                || col >= cellsPerStructure) {
            throw new IllegalArgumentException("Error! Tried to unset a cell "
                    + "that is not on the board! Watch out that the cells are "
                    + "indexed beginning with 0!");
        }
        if (!changeable[row][col]) {
            throw new IllegalArgumentException("Error! Tried to overwrite the "
                    + "value of a cell given by the file!");
        }
        board[row][col] = DisplayedSudoku.UNSET_CELL;
        setChanged();
        notifyObservers(getBoard());
    }

    /**
     * Converts this into a Board and returns the result or throws an exception
     * if the current Sudoku is invalid.
     *
     * @return a Board containing the values saved by {@code this}
     * @throws InvalidSudokuException If the Sudoku represented is invalid.
     */
    public Board getSudoku () throws InvalidSudokuException {
        Board sudoku = new SudokuBoard(boxRows, boxCols);
        for (int i = 0; i < cellsPerStructure; i++) {
            for (int j = 0; j < cellsPerStructure; j++) {
                if (board[i][j] != Board.UNSET_CELL) {
                    sudoku.setCell(Structure.ROW, i, j, board[i][j]);
                }
            }
        }
        return sudoku;
    }

    /**
     * Returns a deep copy of {@code this}.
     *
     * @return a deep copy of {@code this}.
     */
    @Override
    public DisplayedSudoku clone () {
        return new DisplayedSudoku(this);
    }

    public String getContent(int row, int col) {
        return board[row][col] == DisplayedSudoku.UNSET_CELL ? ""
                : Integer.toString(board[row][col]);
    }
}