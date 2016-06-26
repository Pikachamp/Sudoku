package sudoku.model;

import java.util.Arrays;
import java.util.StringJoiner;

/**
 * A board consisting of intelligent SudokuFields being able to tell if it still
 * is a valid Sudoku and offering ways to manipulate single cells through the
 * interface Board.
 */
public class SudokuBoard implements Board {
    private IntelligentSudokuCell[][] board;
    private final int boxRows;
    private final int boxCols;
    private final int numberOfFieldsPerStructure;
    private int[] lastCellSet = null;

    /**
     * Constant to indicate, that the content of a cell is not yet set.
     */
    public static final int UNSET_CELL = Board.UNSET_CELL;

    /**
     * Creates a new SudokuBoard that contains {@code boxRows} rows and {@code
     * boxCols} columns resulting in {@code boxRows * boxCols} cells per row and
     * per column.
     *
     * @param boxRows The number of rows of the board.
     * @param boxCols The number of columns of the board.
     */
    public SudokuBoard(int boxRows, int boxCols) {
        this.boxRows = boxRows;
        this.boxCols = boxCols;
        this.numberOfFieldsPerStructure = boxRows * boxCols;
        this.board = new IntelligentSudokuCell[this.numberOfFieldsPerStructure]
                [this.numberOfFieldsPerStructure];
        for (int i = 0; i < this.numberOfFieldsPerStructure; i++) {
            for (int j = 0; j < this.numberOfFieldsPerStructure; j++) {
                board[i][j] = new IntelligentSudokuCell(SudokuBoard.UNSET_CELL,
                        this.numberOfFieldsPerStructure, i, j);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBoxRows() {
        return this.boxRows;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBoxColumns() {
        return this.boxCols;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumbers() {
        return this.numberOfFieldsPerStructure;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCell(Structure struct, int major, int minor, int number)
            throws InvalidSudokuException {
        if (!isValidPosition(major, minor)) {
            throw new IllegalArgumentException("Error! Tried to set a cell "
                    + "that is not on the board!");
        }
        if (number < 1 || number > numberOfFieldsPerStructure) {
            throw new IllegalArgumentException("Error! Tried to set the number "
                    + number + "that is less than 1 or greater than "
                    + numberOfFieldsPerStructure + ", the biggest number "
                    + "that may be set!");
        }
        int row = getRow(struct, major, minor);
        int col = getColumn(struct, major, minor);
        board[row][col].setContent(number);
        for (Structure structure : Structure.values()) {
            for (IntelligentSudokuCell cell
                    : getStructureContainingCell(structure, row, col)) {
                cell.removePossibility(number);
            }
        }
        if (lastCellSet == null) {
            lastCellSet = new int[2];
        }
        lastCellSet[0] = row;
        lastCellSet[1] = col;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getLastCellSet() {
        return lastCellSet == null ? null : Arrays.copyOf(lastCellSet,
                lastCellSet.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCell(Structure struct, int major, int minor) {
        if (!isValidPosition(major, minor)) {
            throw new IllegalArgumentException("Error! Tried to get the "
                    + "content of a cell that is not on the board!");
        }
        return board[getRow(struct, major, minor)]
                [getColumn(struct, major, minor)].getContent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSolution() {
        boolean everyCellIsFixed;
        for (IntelligentSudokuCell[] row : board) {
            for (IntelligentSudokuCell cell : row) {
                everyCellIsFixed = cell.isFixed();
                if (!everyCellIsFixed) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getPossibilities(Structure struct, int major, int minor) {
        if (!isValidPosition(major, minor)) {
            throw new IllegalArgumentException("Error! Tried to get the "
                    + "possibilities of a cell that is not on the board!");
        }
        return board[getRow(struct, major, minor)]
                [getColumn(struct, major, minor)]
                .getPossibleContent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removePossibility(Structure struct, int major, int minor,
                                  int number) throws InvalidSudokuException {
        if (!isValidPosition(major, minor)) {
            throw new IllegalArgumentException("Error! Tried to set a cell "
                    + "that is not on the board!");
        }
        if (number < 1 || number > numberOfFieldsPerStructure) {
            throw new IllegalArgumentException("Error! Tried to remove the "
                    + "possibility to set the number " + number + "that is less"
                    + "than 1 or greater than " + numberOfFieldsPerStructure
                    + ", the biggest number that may be set!");
        }
        board[getRow(struct, major, minor)]
                [getColumn(struct, major, minor)].
                removePossibility(number);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board clone() {
        SudokuBoard clone;
        try {
            clone = (SudokuBoard) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error("Error! Interface Cloneable not implemented in a "
                    + "superclass of SudokuBoard!");
        }
        if (clone.lastCellSet != null) {
            clone.lastCellSet = Arrays.copyOf(this.lastCellSet,
                    lastCellSet.length);
        }
        clone.board = this.board.clone();
        for (int i = 0; i < numberOfFieldsPerStructure; i++) {
            clone.board[i] = this.board[i].clone();
            for (int j = 0; j < numberOfFieldsPerStructure; j++) {
                clone.board[i][j] = this.board[i][j].clone();
            }
        }
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringJoiner boardAsString = new StringJoiner(" ");
        for (IntelligentSudokuCell[] row : this.board) {
            for (IntelligentSudokuCell cell : row) {
                boardAsString.add(cell.prettyPrint());
            }
        }
        return boardAsString.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Board other) {
        int comparisonOfLastCells;
        for (int i = 0; i < numberOfFieldsPerStructure; i++) {
            for (int j = 0; j < numberOfFieldsPerStructure; j++) {
                comparisonOfLastCells = compareCellsByContent(this.getCell(
                        Structure.ROW, i, j),
                                other.getCell(Structure.ROW, i, j));
                if (comparisonOfLastCells != 0) {
                    return comparisonOfLastCells;
                }
            }
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String prettyPrint() {
        StringBuilder boardAsString = new StringBuilder();
        for (IntelligentSudokuCell[] row : this.board) {
            for (IntelligentSudokuCell cell : row) {
                boardAsString.append(cell.prettyPrint());
                boardAsString.append(" ");
            }
            boardAsString.setLength(boardAsString.length() - 1);
            boardAsString.append("\n");
        }
        return boardAsString.substring(0, boardAsString.length() - 1);
    }

    /**
     * Returns whether a position specified by major coordinate {@code pos1} and
     * minor coordinate {@code pos2} is identifying a cell within the Sudoku or
     * not.
     *
     * @param pos1 The major coordinate.
     * @param pos2 The minor coordinate.
     * @return whether pos1 and pos2 specify the position of a cell or not.
     */
    private boolean isValidPosition(int pos1, int pos2) {
        return pos1 >= 0 && pos1 < numberOfFieldsPerStructure && pos2 >= 0
                && pos2 < numberOfFieldsPerStructure;
    }

    /**
     * Returns the position of the cell in the row depending on the given
     * structure.
     *
     * @param struct The structure {@code major} and {@code minor} are given
     * in.
     * @param major The number of the structure.
     * @param minor The position within the structure.
     * @return The row of the position of the cell.
     */
    private int getRow(Structure struct, int major, int minor) {
        assert isValidPosition(major, minor);
        switch (struct) {
            case ROW:
                return major;
            case COL:
                return minor;
            case BOX:
                return (major / boxRows) * boxRows + minor / boxCols;
            default:
                throw new Error("Reached a value that is not within the enum!");
        }
    }

    /**
     * Returns the position of the cell in the column depending on the given
     * structure.
     *
     * @param struct The structure {@code major} and {@code minor} are given
     * in.
     * @param major The number of the structure.
     * @param minor The position within the structure.
     * @return The column of the position of the cell.
     */
    private int getColumn(Structure struct, int major, int minor) {
        assert isValidPosition(major, minor);
        switch (struct) {
            case ROW:
                return minor;
            case COL:
                return major;
            case BOX:
                return (major % boxRows) * boxCols + minor % boxCols;
            default:
                throw new Error("Reached a value that is not within the enum!");
        }
    }

    /**
     * Returns an Array containing all the cells of the given structure that
     * contains the cell specified by {@code row} and {@code col}.
     *
     * @param struct The structure that should be returned.
     * @param row The row of a cell within the structure.
     * @param col The column of a cell within the structure.
     * @return The structure containing the cell in (row, col).
     */
    private IntelligentSudokuCell[] getStructureContainingCell(Structure struct,
                                                               int row,
                                                               int col) {
        assert isValidPosition(row, col);
        IntelligentSudokuCell[] structure = new
                IntelligentSudokuCell[numberOfFieldsPerStructure];
        switch (struct) {
            case ROW:
                structure = Arrays.copyOf(board[row], board[row].length);
                break;
            case COL:
                for (int i = 0; i < numberOfFieldsPerStructure; i++) {
                    structure[i] = board[i][col];
                }
                break;
            case BOX:
                int rowOfFirstCell = (row / boxRows) * boxRows;
                int columnOfFirstCell = (col / boxCols) * boxCols;
                for (int i = 0; i < boxRows; i++) {
                    for (int j = 0; j < boxCols; j++) {
                        structure[i * boxCols + j] = board[rowOfFirstCell + i]
                                [columnOfFirstCell + j];
                    }
                }
                break;
            default:
                throw new Error("Reached a value that is not within the enum!");
        }
        return structure;
    }

    /**
     * Compares two contents of cells according to ascending numbers. {@link
     * #UNSET_CELL} is treated to be larger than any other number.
     *
     * @param content1 The content of the first cell.
     * @param content2 The content of the second cell.
     * @return -1 if content1 is smaller, 0 if equal, and 1 if bigger.
     */
    private static int compareCellsByContent(int content1, int content2) {
        if (content1 == SudokuBoard.UNSET_CELL) {
            return content2 == content1 ? 0 : 1;
        }
        if (content2 == SudokuBoard.UNSET_CELL) {
            return -1;
        }
        if (content1 == content2) {
            return 0;
        }
        return content1 < content2 ? -1 : 1;
    }
}