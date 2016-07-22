package sudoku.gui.view;

import sudoku.gui.model.DisplayData;
import sudoku.gui.model.DisplayedSudoku;
import sudoku.model.Board;
import sudoku.model.EnforcedCellSaturator;
import sudoku.model.EnforcedNumberSaturator;
import sudoku.model.InvalidSudokuException;
import sudoku.model.Structure;
import sudoku.model.SudokuBoardSolver;
import sudoku.model.SudokuSolver;
import sudoku.model.UnsolvableSudokuException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import static java.lang.Integer.parseInt;

/**
 * A wrapper combining the data of the sudoku with the components to show it,
 * giving possibilities to change the values of both.
 */
public final class SudokuField extends JPanel implements Observer {
    private final int boxRows;
    private final int boxCols;
    private SudokuBoxPanel[] boxes;
    private DisplayData sudoku;

    /**
     * Creates a new JPanel that holds some panels containing the single cells
     * as well as the data of the sudoku.
     *
     * @param boxRows The number of rows each box has.
     * @param boxCols The number of columns each box has.
     * @param sudoku The displayed data of the sudoku.
     */
    public SudokuField(int boxRows, int boxCols, DisplayedSudoku sudoku) {
        if (boxRows <= 0 || boxCols <= 0 || sudoku == null
                || boxRows * boxCols != sudoku.getNumbers()) {
            throw new IllegalArgumentException("Error! There must be at least"
                    + "one row and column per box, the sudoku may not be null "
                    + "and the cells per structure of the sudoku must match "
                    + "those of this.");
        }
        this.boxRows = boxRows;
        this.boxCols = boxCols;
        this.setLayout(new GridLayout(boxCols, boxRows));
        boxes = new SudokuBoxPanel[boxRows * boxCols];
        for (int i = 0; i < boxRows * boxCols; i++) {
            SudokuBoxPanel box = new SudokuBoxPanel(boxRows, boxCols,
                    i / boxRows * boxRows, i % boxRows * boxCols);
            add(box);
            boxes[i] = box;
        }
        this.sudoku = sudoku;
        sudoku.addObserver(this);
        update(sudoku, null);
        setVisible(true);
    }

    /**
     * Sets the cell in the data, makes it undoable and gives feedback to the
     * user if the sudoku has been filled completely.
     *
     * @param row The row of the cell to be set.
     * @param col The column of the cell to be set.
     * @param value The value to be set.
     * @param undoManager The UndoManager managing the edits of the cells.
     */
    void setCell(int row, int col, int value, UndoManager undoManager) {
        if (row < 0 || col < 0 || value < 1 || row >= sudoku.getNumbers()
                || col >= sudoku.getNumbers() || value > sudoku.getNumbers()
                || !sudoku.isChangeable(row, col) || undoManager == null) {
            throw new IllegalArgumentException("Error! Tried to set a cell "
                    + "that is not on the sudoku or may not be set, a value "
                    + "that may not be set or used null as undoManager!");
        }
        undoManager.addEdit(new UndoableCellChange(sudoku, row, col));
        sudoku.setCell(row, col, value, true);
        if (sudoku.isFull()) {
            try {
                Board board = sudoku.getSudoku();
                if (board.isSolution()) {
                    JOptionPane.showMessageDialog(getParent(),
                            "You have solved the Sudoku!", "Congratulations",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(getParent(),
                            "This is no valid solution!", "Attention",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (InvalidSudokuException e) {
                JOptionPane.showMessageDialog(getParent(),
                        "This is no valid solution!", "Attention",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * Removes the value from the cell of the data and makes that undoable.
     *
     * @param row The row of the cell to be unset.
     * @param col The column of the cell to be unset.
     * @param undoManager The UndoManager managing the edits of the cells.
     */
    void unsetCell(int row, int col, UndoManager undoManager) {
        if (row < 0 || col < 0 || row >= sudoku.getNumbers()
                || col >= sudoku.getNumbers() || !sudoku.isChangeable(row, col)
                || undoManager == null) {
            throw new IllegalArgumentException("Error! Tried to unset a cell "
                    + "that is not on the sudoku or may not be set or used "
                    + "null as undoManager!");
        }
        undoManager.addEdit(new UndoableCellChange(sudoku, row, col));
        sudoku.unsetCell(row, col);
    }

    /**
     * Tries to solve the sudoku and sets an unset cell to a value of the
     * solution if successful. Makes this undoable, too.
     *
     * @param undoManager The UndoManager managing the edits of the cells.
     * @throws InvalidSudokuException If the sudoku is not valid.
     * @throws UnsolvableSudokuException If the sudoku cannot be solved.
     */
    void suggestValue(UndoManager undoManager)
            throws InvalidSudokuException, UnsolvableSudokuException {
        if (undoManager == null) {
            throw new IllegalArgumentException("Error! Used null as "
                    + "undoManager!");
        }
        Board board = sudoku.getSudoku();
        SudokuSolver solver = new SudokuBoardSolver();
        solver.addSaturator(new EnforcedCellSaturator());
        solver.addSaturator(new EnforcedNumberSaturator());
        board = solver.findFirstSolution(board);
        int[] lastSetCoordinates = board.getLastCellSet();
        int lastSetValue = board.getCell(Structure.ROW, lastSetCoordinates[0],
                lastSetCoordinates[1]);
        setCell(lastSetCoordinates[0], lastSetCoordinates[1],
                lastSetValue, undoManager);
    }

    /**
     * Solves the sudoku completely and sets the data to the found solution if
     * possible. Makes this undoable, too.
     *
     * @param undoManager The UndoManager managing the edits of the cells.
     * @throws InvalidSudokuException If the sudoku is not valid.
     * @throws UnsolvableSudokuException If the sudoku cannot be solved.
     */
    void solveSudoku(UndoManager undoManager)
            throws InvalidSudokuException, UnsolvableSudokuException {
        if (undoManager == null) {
            throw new IllegalArgumentException("Error! Used null as "
                    + "undoManager!");
        }
        Board board = sudoku.getSudoku();
        SudokuSolver solver = new SudokuBoardSolver();
        solver.addSaturator(new EnforcedCellSaturator());
        solver.addSaturator(new EnforcedNumberSaturator());
        board = solver.findFirstSolution(board);
        for (int i = 0; i < board.getNumbers(); i++) {
            for (int j = 0; j < board.getNumbers(); j++) {
                if (sudoku.isChangeable(i, j)) {
                    setCell(i, j, board.getCell(Structure.ROW, i, j),
                            undoManager);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o == null) {
            throw new IllegalArgumentException("Error! The observed object may "
                    + "not be null!");
        }
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

    /**
     * Returns the number of the box containing the cell given by {@code (row,
     * col)}.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @return The number of the box of the cell.
     */
    private int getBoxNumber(int row, int col) {
        assert row >= 0 && col >=  0 && row < sudoku.getNumbers()
                && col < sudoku.getNumbers();
            return (row / boxRows) * boxRows + col / boxCols;
    }

    /**
     * Returns the position of the cell within the box given by {@code (row,
     * col)}.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @return The position of the cell within the box.
     */
    private int getPositionInBox(int row, int col) {
        assert row >= 0 && col >=  0 && row < sudoku.getNumbers()
                && col < sudoku.getNumbers();
        return (row % boxRows) * boxCols + col % boxCols;
    }
}

/**
 * A class wrapping the change of a cell making it possible to undo it.
 */
class UndoableCellChange extends AbstractUndoableEdit {
    private final int oldValue;
    private final int row;
    private final int column;
    private DisplayData sudoku;

    /**
     * Saves the current state of the specified cell in the given sudoku to make
     * it possible to undo changes made.
     *
     * @param sudoku The sudoku containing the cell.
     * @param row The row of the cell.
     * @param column The column of the cell.
     */
    UndoableCellChange(DisplayData sudoku, int row, int column) {
        super();
        if (sudoku == null || row >= sudoku.getNumbers()
                || column >= sudoku.getNumbers()) {
            throw new IllegalArgumentException("Error! Null was given a sudoku "
                    + "or the row and the column of the cell are too high!");
        }
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void undo() throws CannotUndoException {
        if (oldValue == DisplayedSudoku.UNSET_CELL) {
            sudoku.unsetCell(row, column);
        } else {
            sudoku.setCell(row, column, oldValue, true);
        }
        super.undo();
    }
}