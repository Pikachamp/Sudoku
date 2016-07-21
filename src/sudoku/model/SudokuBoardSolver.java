package sudoku.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * A class to administrate solution strategies and apply them to Sudokus.
 */
public class SudokuBoardSolver implements SudokuSolver {
    private List<Saturator> solutionStrategies = new ArrayList<>();

    /**
     * Creates a new SudokuBoardSolver with no solution strategy.
     */
    public SudokuBoardSolver() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSaturator(Saturator saturator) {
        if (saturator == null) {
            throw new IllegalArgumentException("Error! Null has been "
                    + "given as a Saturator!");
        }
        solutionStrategies.add(0, saturator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board saturate(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("Error! Null has been "
                    + "given to the Sudoku solver as the board!");
        }
        boolean boardHasChanged = true;
        Board clone = board.clone();
        while (boardHasChanged) {
            boardHasChanged = false;
            try {
                for (Saturator strategy : solutionStrategies) {
                    boardHasChanged |= strategy.saturate(clone);
                }
            } catch (UnsolvableSudokuException e) {
                return null;
            }
            board = clone;
        }
        return board;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board findFirstSolution(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("Null has been given to the "
                    + "Sudoku solver as a board!");
        }
        List<Board> solution = getSolvedSudokus(board, true);
        return solution.isEmpty() ? null : solution.get(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Board> findAllSolutions(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("Null has been given to the "
                    + "Sudoku solver as a board!");
        }
        List<Board> solutions = getSolvedSudokus(board, false);
        Collections.sort(solutions);
        return solutions;
    }

    /**
     * Returns the first cell going from upper left corner right and down in
     * {@code board} that is not set yet and has the least numbers that it can
     * be set with.
     *
     * @param board The board to search through.
     * @return The coordinates of the cell with the least possibilities.
     */
    private int[] getCellWithLeastPossibilities(Board board) {
        assert board != null && !board.isSolution();
        int numberOfCellsPerStructure = board.getNumbers();
        int leastPossibilities = board.getNumbers() + 1;
        int[] coordinatesOfCellWithLeastPossibilities = new int[2];
        for (int i = 0; i < numberOfCellsPerStructure; i++) {
            for (int j = 0; j < numberOfCellsPerStructure; j++) {
                int[] possibilities = board.getPossibilities(Structure.ROW, i,
                        j);
                if (possibilities != null) {
                    if (possibilities.length < leastPossibilities) {
                        leastPossibilities = possibilities.length;
                        coordinatesOfCellWithLeastPossibilities[0] = i;
                        coordinatesOfCellWithLeastPossibilities[1] = j;
                    }
                }
            }
        }
        return coordinatesOfCellWithLeastPossibilities;
    }

    /**
     * Adds all boards to the stack {@code boards} that come from filling the
     * cell with the least possibilities with all the numbers that still may be
     * inserted into them.
     *
     * @param boards The stack the boards should be pushed onto.
     * @param currentBoard The board that should be processed.
     */
    private void addPossibleBoards(Stack<Board> boards, Board currentBoard) {
        assert boards != null && currentBoard != null;
        int[] cellWithLeastPossibilities =
                getCellWithLeastPossibilities(currentBoard);
        int row = cellWithLeastPossibilities[0];
        int col = cellWithLeastPossibilities[1];
        int[] possibleContent = currentBoard.getPossibilities(Structure.ROW,
                row, col);
        for (int i = possibleContent.length - 1; i >= 0; i--) {
            Board newBoard = currentBoard.clone();
            try {
                newBoard.setCell(Structure.ROW, row, col, i);
            } catch (InvalidSudokuException e) {
                continue;
            }
            boards.push(newBoard);
        }
    }

    /**
     * Returns a list containing either just the first found or all solutions
     * depending on {@code firstOnly}
     *
     * @param board The board to be solved
     * @param firstOnly Indicator if only the first solution is needed.
     * @return A list containing solved boards.
     */
    private List<Board> getSolvedSudokus(Board board, boolean firstOnly) {
        assert board != null;
        List<Board> solutions = new ArrayList<>();
        Stack<Board> trackedBoards = new Stack<>();
        trackedBoards.push(board.clone());
        while (!trackedBoards.isEmpty()) {
            Board saturatedBoard = saturate(trackedBoards.pop());
            if (saturatedBoard == null) {
                continue;
            }
            if (saturatedBoard.isSolution()) {
                solutions.add(saturatedBoard);
                if (firstOnly) {
                    return solutions;
                }
            }
            if (!saturatedBoard.isSolution()) {
                addPossibleBoards(trackedBoards, saturatedBoard);
            }
        }
        return solutions;
    }
}