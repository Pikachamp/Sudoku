package sudoku.gui.view;

import javax.swing.*;
import java.awt.*;

public class SudokuBoxPanel extends JPanel {

    public SudokuBoxPanel (int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Error! There must be at least"
                    + "one row and column per box!");
        }
        for (int i = 0; i < rows * cols; i++) {
            this.add(new JLabel());
        }
        this.setLayout(new GridLayout(rows, cols));
    }
}