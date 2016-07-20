package sudoku.gui.view;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.GridLayout;

/**
 * A box of a sudoku containing its cells making it easier to structure the
 * sudoku. It can change the value of its cells.
 */
class SudokuBoxPanel extends JPanel {
    private SudokuCellLabel[] cells;

    SudokuBoxPanel (int rows, int cols, int firstRow, int firstCol) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Error! There must be at least"
                    + "one row and column per box!");
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

    void setLabel (int position, String value, boolean changeable) {
        if (cells == null || position < 0 || position >= cells.length) {
            throw new IllegalArgumentException("Error! The label tried to set "
            + "does not exist!");
        }
        cells[position].set(value, changeable);
    }
}