package sudoku.gui.view;

import sudoku.gui.DisplayedSudokuFactory;
import sudoku.gui.model.DisplayedSudoku;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class SudokuField extends JPanel {
    private DisplayedSudoku sudoku;

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

    public void openFile (File file) throws IOException {
        try {
            sudoku = DisplayedSudokuFactory.loadFromFile(file);
        }  catch (ParseException e) {
            //TODO
        }
    }
}