package sudoku.gui.view;

import javax.swing.*;
import java.awt.*;

public class SudokuBoxPanel extends JPanel {
    private JLabel[] cells;

    public SudokuBoxPanel (int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Error! There must be at least"
                    + "one row and column per box!");
        }
        cells = new JLabel[rows * cols];
        for (int i = 0; i < rows * cols; i++) {
            JLabel label = new JLabel();
            this.add(label);
            cells[i] = label;
        }
        this.setLayout(new GridLayout(rows, cols));
        this.setVisible(true);
    }

    public void setLabel (int position, String value) {
        if (cells == null || position >= cells.length) {
            throw new IllegalArgumentException("Error! The label tried to set "
            + "does not exist!");
        }
        cells[position].setText(value);
    }
}