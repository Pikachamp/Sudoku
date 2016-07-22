package sudoku.gui.view;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.GridLayout;

/**
 * A JPanel representing a box of a sudoku containing its cells making it easier
 * to structure the sudoku. It can change the value of its cells.
 */
final class SudokuBoxPanel extends JPanel {
    private SudokuCellLabel[] cells;

    /**
     * Creates a JPanel representing a box of a sudoku, having the cells already
     * pinned to it.
     *
     * @param rows The number of rows this box has.
     * @param cols The number of columns this box has.
     * @param firstRow The row of the first cell of this box.
     * @param firstCol The column of the first cell of this box.
     */
    SudokuBoxPanel(int rows, int cols, int firstRow, int firstCol) {
        if (rows <= 0 || cols <= 0 || firstRow < 0 || firstCol < 0
                || firstRow > rows * cols - rows
                || firstCol > rows * cols - cols) {
            throw new IllegalArgumentException("Error! There must be at least"
                    + "one row and column per box and the first row and first "
                    + "column of the box must be valid!");
        }
        cells = new SudokuCellLabel[rows * cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                SudokuCellLabel label = new SudokuCellLabel(firstRow + i,
                        firstCol + j, rows * cols);
                add(label);
                cells[i * cols + j] = label;
            }
        }
        setLayout(new GridLayout(rows, cols));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setVisible(true);
    }

    /**
     * Sets the value of the specified cell of this box and makes it
     * unchangeable if necessary.
     *
     * @param position The number of the cell within this box.
     * @param value The value to be shown by the cell label.
     * @param changeable Specifies whether the value may be changed later on.
     */
    void setLabel(int position, String value, boolean changeable) {
        if (position < 0 || position >= cells.length || value == null) {
            throw new IllegalArgumentException("Error! The label tried to set "
            + "does not exist or the value is not null");
        }
        cells[position].set(value, changeable);
    }
}