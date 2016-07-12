package sudoku.gui.view;

import sudoku.gui.SudokuFieldFactory;
import sudoku.gui.model.DisplayedSudoku;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Observable;
import java.util.Observer;

public class SudokuField extends JPanel implements Observer {
    private int boxRows = 0;
    private int boxCols = 0;
    private DisplayedSudoku sudoku;
    private SudokuBoxPanel[] boxes;

    public SudokuField (int boxRows, int boxCols, DisplayedSudoku sudoku) {
        if (boxRows <= 0 || boxCols <= 0) {
            throw new IllegalArgumentException("Error! There must be at least"
                    + "one row and column per box!");
        }
        this.boxRows = boxRows;
        this.boxCols = boxCols;
        this.setLayout(new GridLayout(boxCols, boxRows));
        boxes = new SudokuBoxPanel[boxRows * boxCols];
        for (int i = 0; i < boxRows * boxCols; i++) {
            SudokuBoxPanel box = new SudokuBoxPanel(boxRows, boxCols);
            this.add(box);
            boxes[i] = box;
        }
        this.sudoku = sudoku;
        sudoku.addObserver(this);
        this.setVisible(true);
    }

    @Override
    public void update(Observable o, Object arg) {
        for (int i = 0; i < boxRows * boxCols; i++) {
            for (int j = 0; j < boxRows * boxCols; j++) {
                boxes[i].setLabel(j, sudoku.getContent(this.getRow(i, j),
                        this.getColumn(i, j)));
            }
        }
    }

    private int getRow(int box, int positionInBox) {
        return (box / boxRows) * boxRows + positionInBox / boxCols;
    }

    private int getColumn(int box, int positionInBox) {
        return (box % boxRows) * boxCols + positionInBox % boxCols;
    }
}