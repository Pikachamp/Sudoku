package sudoku.model;

/**
 * A class holding the algorithm to solve a Sudoku by the enforced cell
 * strategy. Therefore each cell is checked for its remaining possibilities and
 * is set if only one is remaining.
 */
public class EnforcedCellSaturator implements Saturator {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean saturate(Board board) throws UnsolvableSudokuException {
        if (board == null) {
            throw new IllegalArgumentException("Error! Null has been given"
                    + "as the board to saturate!");
        }
        boolean boardHasChanged = false;
        int numberOfCellsPerStructure = board.getNumbers();
        for (int i = 0; i < numberOfCellsPerStructure; i++) {
            for (int j = 0; j < numberOfCellsPerStructure; j++) {
                int[] possibleContentInCell = board.getPossibilities(
                        Structure.ROW, i, j);
                if (possibleContentInCell != null) {
                    if (possibleContentInCell.length == 1) {
                        try {
                            board.setCell(Structure.ROW, i, j,
                                    possibleContentInCell[0]);
                        } catch (InvalidSudokuException e) {
                            throw new UnsolvableSudokuException();
                        }
                        boardHasChanged = true;
                    }
                }
            }
        }
        return boardHasChanged;
    }
}
