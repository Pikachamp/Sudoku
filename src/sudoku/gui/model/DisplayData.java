package sudoku.gui.model;

import sudoku.model.Board;
import sudoku.model.InvalidSudokuException;

public interface DisplayData {

    /**
     * The constant to indicate that a cell is not yet set.
     */
    int UNSET_CELL = Board.UNSET_CELL;

    /**
     * Returns the number of cells each structure has.
     *
     * @return the number of cells per structure.
     */
    int getNumbers();

    /**
     * Returns whether the cell specified by {@code row} and {@code col} is
     * changeable or not.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @return whether the cell may be changed or not.
     */
    boolean isChangeable(int row, int col);

    /**
     * Sets the cell specified by its coordinates to the given value. Specifies
     * if the cell is going to be changeable or not.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @param number The number to be set into the cell.
     * @param isChangeable Specifies whether the cell will be changeable or
     * not.
     */
    void setCell(int row, int col, int number, boolean isChangeable);

    /**
     * Removes the content of the specified cell.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     */
    void unsetCell(int row, int col);

    /**
     * Converts this into a Board and returns the result or throws an exception
     * if the current Sudoku is invalid.
     *
     * @return a Board containing the values saved by {@code this}
     * @throws InvalidSudokuException If the Sudoku represented is invalid.
     */
    Board getSudoku() throws InvalidSudokuException;

    /**
     * Returns the String representation of the content of the cell specified.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @return the content of the Cell as a String.
     */
    String getContent(int row, int col);
}
