package sudoku.gui.view;

import javax.swing.*;
import java.awt.*;

public class SudokuField extends JPanel {

    public SudokuField (int boxRows, int boxCols) {
        if (boxRows <= 0 || boxCols <= 0) {
            throw new IllegalArgumentException("Error! There must be at least"
                    + "one row and column per box!");
        }
        this.setLayout(new GridLayout(boxCols, boxRows));
        for (int i = 0; i < boxRows * boxCols; i++) {
            this.add(new SudokuBoxPanel(boxRows, boxCols));
        }
    }
}