package sudoku.gui.view;

import sudoku.gui.model.DisplayedSudoku;
import sudoku.model.*;

import javax.swing.*;
import javax.swing.undo.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

import static java.lang.Integer.parseInt;

public class SudokuField extends JPanel implements Observer {
    private int boxRows = 0;
    private int boxCols = 0;
    private SudokuBoxPanel[] boxes;
    private final DisplayedSudoku sudoku;

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
                    i / boxRows * boxRows, i % boxCols * boxCols);
            this.add(box);
            boxes[i] = box;
        }
        this.sudoku = sudoku;
        sudoku.addObserver(this);
        this.update(sudoku, null);
        this.setVisible(true);
    }

    public synchronized void setCell (int row, int col, int value,
                                      UndoManager undoManager,
                                      boolean changedByUser) {
        if (row < 0 || col < 0 || value < 1 || sudoku == null
                || row >= sudoku.getNumbers() || col >= sudoku.getNumbers()
                || value > sudoku.getNumbers()) {
            throw new IllegalArgumentException("Error! Tried to set a cell "
                    + "that is not on the sudoku, a value that may not be set "
                    + "or a cell of a not yet initialized sudoku!");
        }
        undoManager.addEdit(new UndoableCellChange(sudoku, row, col));
        sudoku.setCell(row, col, value, true);
        if (changedByUser) {
            try {
                Board board = sudoku.getSudoku();
                if (board.isSolution()) {
                    JOptionPane.showMessageDialog(getParent(),
                            "You have solved the Sudoku!", "Congratulations",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (InvalidSudokuException e) {
            }
        }
    }

    public synchronized void unsetCell (int row, int col,
                                        UndoManager undoManager) {
        if (row < 0 || col < 0 || sudoku == null || row >= sudoku.getNumbers()
                || col >= sudoku.getNumbers()) {
            throw new IllegalArgumentException("Error! Tried to unset a cell "
                    + "that is not on the sudoku or a cell of a not yet "
                    + "initialized sudoku!");
        }
        undoManager.addEdit(new UndoableCellChange(sudoku, row, col));
        sudoku.unsetCell(row, col);
    }

    public void suggestValue(UndoManager undoManager)
            throws InvalidSudokuException, UnsolvableSudokuException {
        Board board = sudoku.getSudoku();
        SudokuSolver solver = new SudokuBoardSolver();
        solver.addSaturator(new EnforcedCellSaturator());
        solver.addSaturator(new EnforcedNumberSaturator());
        board = solver.findFirstSolution(board);
        int[] lastSetCoordinates = board.getLastCellSet();
        int lastSetValue = board.getCell(Structure.ROW, lastSetCoordinates[0],
                lastSetCoordinates[1]);
        setCell(lastSetCoordinates[0], lastSetCoordinates[1],
                lastSetValue, undoManager, false);
    }

    public void solveSudoku(UndoManager undoManager)
            throws InvalidSudokuException, UnsolvableSudokuException {
        Board board = sudoku.getSudoku();
        SudokuSolver solver = new SudokuBoardSolver();
        solver.addSaturator(new EnforcedCellSaturator());
        solver.addSaturator(new EnforcedNumberSaturator());
        board = solver.findFirstSolution(board);
        for (int i = 0; i < board.getNumbers(); i++) {
            for (int j = 0; j < board.getNumbers(); j++) {
                if (sudoku.isChangeable(i, j)) {
                    setCell(i, j, board.getCell(Structure.ROW, i, j),
                            undoManager, false);
                }
            }
        }
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

class UndoableCellChange extends AbstractUndoableEdit {
    private int oldValue;
    private int row;
    private int column;
    private DisplayedSudoku sudoku;

    public UndoableCellChange (DisplayedSudoku sudoku, int row, int column) {
        super();
        this.sudoku = sudoku;
        this.row = row;
        this.column = column;
        String oldContent = sudoku.getContent(row, column);
        if (oldContent.equals("")) {
            oldValue = DisplayedSudoku.UNSET_CELL;
        } else {
            oldValue = parseInt(oldContent);
        }
    }

    @Override
    public void undo() throws CannotUndoException {
        if (oldValue == DisplayedSudoku.UNSET_CELL) {
            sudoku.unsetCell(row, column);
        } else {
            sudoku.setCell(row, column, oldValue, true);
        }
        super.undo();
    }

    @Override
    public boolean canUndo() {
        if (sudoku == null) {
            return false;
        }
        return super.canUndo();
    }
}