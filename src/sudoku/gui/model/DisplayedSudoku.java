package sudoku.gui.model;

import sudoku.model.Board;
import sudoku.model.InvalidSudokuException;
import sudoku.model.Structure;
import sudoku.model.SudokuBoard;

import java.util.Observable;

/**
 * The data that is displayed by the GUI with the option to convert it into a
 * {@link sudoku.model.Board} and solve it.
 */
public class DisplayedSudoku extends Observable implements DisplayData {

    /**
     * {@inheritDoc}
     */
    public static final int UNSET_CELL = DisplayData.UNSET_CELL;
    private final int boxRows;
    private final int boxCols;
    private final int cellsPerStructure;
    private int[][] board;
    private boolean[][] changeable;

    /**
     * Creates a new Sudoku board that has {@code boxRows * boxCols} rows and
     * columns.
     *
     * @param boxRows The number of rows per box.
     * @param boxCols The number of columns per box.
     */
    public DisplayedSudoku(int boxRows, int boxCols) {
        if (boxRows < 1 || boxCols < 1) {
            throw new IllegalArgumentException("There must be at least one row "
                    + "and one column per Box of the Sudoku!");
        }
        this.boxRows = boxRows;
        this.boxCols = boxCols;
        cellsPerStructure = boxRows * boxCols;
        board = new int[cellsPerStructure][cellsPerStructure];
        changeable = new boolean[cellsPerStructure][cellsPerStructure];
        for (int i = 0; i < cellsPerStructure; i++) {
            for (int j = 0; j < cellsPerStructure; j++) {
                board[i][j] = DisplayedSudoku.UNSET_CELL;
                changeable[i][j] = true;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumbers() {
        return cellsPerStructure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isChangeable(int row, int col) {
        if (row < 0 || col < 0 || row >= cellsPerStructure
                || col >= cellsPerStructure) {
            throw new IllegalArgumentException("Error! Tried to acces a cell "
                    + "that does not exist!");
        }
        return changeable[row][col];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCell(int row, int col, int number, boolean isChangeable) {
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
        notifyObservers(board);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unsetCell(int row, int col) {
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
        notifyObservers(board);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFull() {
        for (int[] row : board) {
            for (int cell : row) {
                if (cell == DisplayedSudoku.UNSET_CELL) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board getSudoku() throws InvalidSudokuException {
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
     * {@inheritDoc}
     */
    @Override
    public String getContent(int row, int col) {
        return board[row][col] == DisplayedSudoku.UNSET_CELL ? ""
                : Integer.toString(board[row][col]);
    }
}