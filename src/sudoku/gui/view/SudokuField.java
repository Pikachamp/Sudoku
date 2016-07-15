package sudoku.gui.view;

import sudoku.gui.model.DisplayedSudoku;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class SudokuField extends JPanel implements Observer {
    private int boxRows = 0;
    private int boxCols = 0;
    private SudokuBoxPanel[] boxes;
    private final DisplayedSudoku sudoku;
    UndoManager undoManager;

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
            SudokuBoxPanel box = new SudokuBoxPanel(boxRows, boxCols,
                    i / boxRows * boxRows, i / boxCols * boxCols);
            this.add(box);
            boxes[i] = box;
        }
        this.sudoku = sudoku;
        sudoku.addObserver(this);
        this.update(sudoku, null);
        this.setVisible(true);
        undoManager = new UndoManager();
    }

    public void setCell (int row, int col, int value) {
        if (row < 0 || col < 0 || value < 1 || sudoku == null
                || row >= sudoku.getNumbers() || col >= sudoku.getNumbers()
                || value > sudoku.getNumbers()) {
            throw new IllegalArgumentException("Error! Tried to set a cell "
                    + "that is not on the sudoku, a value that may not be set "
                    + "or a cell of a not yet initialized sudoku!");
        }
        sudoku.setCell(row, col, value, true);
    }

    public void unsetCell (int row, int col) {
        if (row < 0 || col < 0 || sudoku == null || row >= sudoku.getNumbers()
                || col >= sudoku.getNumbers()) {
            throw new IllegalArgumentException("Error! Tried to unset a cell "
                    + "that is not on the sudoku or a cell of a not yet "
                    + "initialized sudoku!");
        }
        sudoku.unsetCell(row, col);
    }

    @Override
    public void update(Observable o, Object arg) {
        DisplayedSudoku sud = (DisplayedSudoku) o;
        for (int i = 0; i < sud.getNumbers(); i++) {
            for (int j = 0; j < sud.getNumbers(); j++) {
                int boxNumber = this.getBoxNumber(i, j);
                int positionInBox = this.getPositionInBox(i, j);
                boxes[boxNumber].setLabel(positionInBox, sud.getContent(i, j),
                        sud.isChangeable(i, j));
            }
        }
    }

    private int getBoxNumber(int row, int col) {
        return (row / boxRows) * boxRows + col / boxCols;
    }

    private int getPositionInBox(int row, int col) {
        return (row % boxRows) * boxCols + col % boxCols;
    }
}