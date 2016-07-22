package sudoku.model;

/**
 * A class holding the algorithm to solve a Sudoku by the enforced cell
 * strategy. Therefore for every number it is checked for every structure
 * if it can only be set into one cell. If so the number is set into this cell.
 */
public class EnforcedNumberSaturator implements Saturator {

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
        int[] lastOccurrence = new int[board.getNumbers()];

        /*
         * The position in this is indexing a counter that gives the number of
         * times position + 1 may be inserted into the cells of the structure.
         */
        int[] possibleContentCounters;
        for (Structure structure : Structure.values()) {
            for (int i = 0; i < numberOfCellsPerStructure; i++) {
                possibleContentCounters =
                        getPossibilityCounters(structure, i, board,
                                lastOccurrence);
                for (int j = 0; j < numberOfCellsPerStructure; j++) {
                    if (possibleContentCounters[j] == 1) {
                        try {
                            board.setCell(structure, i, lastOccurrence[j],
                                    j + 1);
                        } catch (InvalidSudokuException e) {
                            throw new UnsolvableSudokuException("Error! "
                                    + "Setting a cell made the Sudoku "
                                    + "invalid!");
                        }
                        boardHasChanged = true;
                    }
                }

            }
        }
        return boardHasChanged;
    }

    /**
     * Returns the possibility counters for all numbers that may be filled into
     * the Sudoku and saves their last occurrence into {@code lastOccurrence}
     * for the specified structure.
     *
     * @param struct The structure that should be analyzed.
     * @param major The number of the structure.
     * @param board The board the structure is on.
     * @param lastOccurence The array the last occurrences should be saved to.
     * @return The possibility counters.
     */
    private int[] getPossibilityCounters(Structure struct, int major,
                                        Board board, int[] lastOccurence) {
        assert board != null && major >= 0 && major < board.getNumbers()
                && lastOccurence != null;
        int[] counters = new int[board.getNumbers()];
        for (int i = 0; i < board.getNumbers(); i++) {
            int[] possibilities = board.getPossibilities(struct, major, i);
            if (possibilities == null) {
                continue;
            }
            for (int possibility : possibilities) {
                ++counters[possibility - 1];
                lastOccurence[possibility - 1] = i;
            }
        }
        return counters;
    }
}
