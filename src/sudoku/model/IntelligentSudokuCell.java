package sudoku.model;

import java.util.BitSet;

/**
 * An intelligent cell of a Sudoku board that can tell its remaining
 * possibilities, if it's set or not, its content to other objects and can be
 * cloned.
 */
class IntelligentSudokuCell implements Cloneable {
    private int content;
    private boolean isFixed;
    private final int positionInRow;
    private final int positionInColumn;
    private final int maxNumber;
    private BitSet positionInSetIsPossibleContent;

    /**
     * Creates a new cell of a Sudoku board with content {@code content} that
     * may range from 1 to {@code maxNumber} having the given position.
     *
     * @param content The content of the cell.
     * @param maxNumber The greatest number a cell might contain.
     * @param positionInRow The position in the row of the Sudoku.
     * @param positionInColumn The position in the colum of the Sudoku.
     */
    IntelligentSudokuCell(int content, int maxNumber, int positionInRow,
                          int positionInColumn) {
        if ((content < 1 || content > maxNumber)
                && content != SudokuBoard.UNSET_CELL) {
            throw new IllegalArgumentException("Error! " + content
                    + " is no valid content for a Sudoku Cell!");
        }
        if (maxNumber < 1) {
            throw new IllegalArgumentException("You cannot initialize a cell"
                    + "in which no cells can be set, maxNumber must be at least"
                    + "1 but is " + maxNumber + "!");
        }
        if (positionInRow < 0 || positionInRow > maxNumber - 1
                || positionInColumn < 0 || positionInColumn > maxNumber - 1) {
            throw new IllegalArgumentException("Error! The position of a cell"
                    + " in a row and in a column must be greater than 0 and "
                    + "less than " + maxNumber);
        }
        this.content = content;
        positionInSetIsPossibleContent = new BitSet(maxNumber);
        if (this.content == SudokuBoard.UNSET_CELL) {
            this.isFixed = false;
            positionInSetIsPossibleContent.set(0, maxNumber);
        } else {
            this.isFixed = true;
        }
        this.positionInRow = positionInRow;
        this.positionInColumn = positionInColumn;
        this.maxNumber = maxNumber;
    }

    /**
     * Returns the number that is set in the cell or {@code UNSET_CELL} if it is
     * not yet set.
     *
     * @return the content of the cell.
     */
    int getContent() {
        return this.content;
    }

    /**
     * Sets the content of the cell if it's not fixed yet and if this number may
     * still be inserted without making the Sudoku invalid. Nothing happens if
     * the cell is set already.
     *
     * @param number The number that should be set into this cell.
     * @throws InvalidSudokuException if {@code number} may not be set.
     */
    void setContent(int number) throws InvalidSudokuException {
        if (number < 1 || number > maxNumber) {
            throw new IllegalArgumentException("Error! " + number + "is no "
                    + "valid number for a Sudoku cell!");
        }
        if (isFixed) {
            return;
        }
        if (positionInSetIsPossibleContent.get(number - 1)) {
            content = number;
            isFixed = true;
            positionInSetIsPossibleContent.clear();
        } else {
            throw new InvalidSudokuException("Error! Tried to set a number "
                    + "in a cell that is not possible there anymore!");
        }
    }

    /**
     * Returns {@code true} if the cell is fixed and {@code false} if not.
     *
     * @return whether the cell is fixed or not.
     */
    boolean isFixed() {
        return isFixed;
    }

    /**
     * Returns the number of the row in which the cell is.
     *
     * @return the number of the row of the cell.
     */
    int getPositionInRow() {
        return positionInRow;
    }

    /**
     * Returns the number of the column in which the cell is.
     *
     * @return the number of the column of the cell.
     */
    int getPositionInColumn() {
        return positionInColumn;
    }

    /**
     * Returns an array holding the numbers that may still be inserted into this
     * cell or null if the cell is already set.
     *
     * @return the numbers that may be inserted into this cell.
     */
    int[] getPossibleContent() {
        if (isFixed) {
            return null;
        }
        int[] possibilities = new
                int[positionInSetIsPossibleContent.cardinality()];
        int currentPositionInArray = 0;
        for (int i = 0; i < positionInSetIsPossibleContent.length(); i++) {
            if (positionInSetIsPossibleContent.get(i)) {
                possibilities[currentPositionInArray] = i + 1;
                ++currentPositionInArray;
            }
        }
        return possibilities;
    }

    /**
     * Removes the possibility to insert {@code number} into {@code this}.
     *
     * @param number The number that should no longer be able to be inserted.
     */
    void removePossibility(int number) throws InvalidSudokuException {
        if (number < 1 || number > maxNumber) {
            throw new IllegalArgumentException("Error! " + number + "is no "
                    + "valid number for a Sudoku cell!");
        }
        positionInSetIsPossibleContent.clear(number - 1);
        if (!isFixed && positionInSetIsPossibleContent.isEmpty()) {
            throw new InvalidSudokuException("Error! All possibilities "
                    + "would be removed from an unset cell!");
        }
    }

    /**
     * Returns the number that is present within this cell or {@code "."} if the
     * content of this cell is not yet set.
     *
     * @return the content of this cell in textual representation.
     */
    String prettyPrint() {
        return content == SudokuBoard.UNSET_CELL ? "."
                : Integer.toString(content);
    }

    /**
     * Returns the number that is present within this cell or {@code "."} if the
     * content of this cell is not yet set and its coordinates on the board.
     *
     * @return The content of this cell and its position.
     */
    @Override
    public String toString() {
        StringBuilder cellAsString = new StringBuilder();
        if (isFixed) {
            cellAsString.append(content);
        } else {
            cellAsString.append('.');
        }
        cellAsString.append(" (");
        cellAsString.append(positionInRow);
        cellAsString.append(", ");
        cellAsString.append(positionInColumn);
        cellAsString.append(")");
        return cellAsString.toString();
    }

    /**
     * Creates and returns a deep copy of the IntelligentSudokuCell {@code
     * this}.
     *
     * @return A clone of {@code this}.
     */
    @Override
    protected IntelligentSudokuCell clone() {
        IntelligentSudokuCell clone;
        try {
            clone = (IntelligentSudokuCell) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error("Error! Interface Cloneable not implemented in a "
                    + "superclass of IntelligentSudokuCell!");
        }
        clone.positionInSetIsPossibleContent =
                (BitSet) this.positionInSetIsPossibleContent.clone();
        return clone;
    }
}