package sudoku.gui.view;

import javax.swing.*;
import java.awt.*;

public class SudokuBoxPanel extends JPanel {
    private SudokuCellLabel[] cells;

    public SudokuBoxPanel (int rows, int cols, int firstRow, int firstCol) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Error! There must be at least"
                    + "one row and column per box!");
        }
        cells = new SudokuCellLabel[rows * cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j ++) {
                SudokuCellLabel label = new SudokuCellLabel(firstRow + i,
                        firstCol + j, rows * cols);
                this.add(label);
                cells[i * rows + j] = label;
            }
        }
        this.setLayout(new GridLayout(rows, cols));
        this.setVisible(true);
    }

    public void setLabel (int position, String value, boolean changeable) {
        if (cells == null || position < 0 || position >= cells.length) {
            throw new IllegalArgumentException("Error! The label tried to set "
            + "does not exist!");
        }
        cells[position].set(value, changeable);
    }
}