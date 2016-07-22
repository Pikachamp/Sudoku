package sudoku.gui;

import sudoku.gui.model.DisplayedSudoku;
import sudoku.gui.view.SudokuField;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Scanner;

/**
 * This class can be used to read a Sudoku from a given file.
 */
public final class SudokuFieldFactory {

    /**
     * The String used to separate the numbers within the file.
     */
    private static final String DELIMITER = " ";

    /**
     * Utility class constructor preventing instantiation.
     */
    private SudokuFieldFactory() {
        throw new UnsupportedOperationException(
                "Illegal call of utility class constructor.");
    }

    /**
     * Loads a Sudoku from a given file and creates the data representing it.
     *
     * @param file The input file.
     * @return the Sudoku given by the file.
     * @throws FileNotFoundException If the input file could not be found.
     * @throws IOException If an IO error occurs.
     * @throws ParseException If the file is not using the expected format.
     */
    public static SudokuField loadFromFile(File file)
            throws FileNotFoundException, IOException, ParseException {
        if (file == null || !file.exists() || !file.canRead()) {
            throw new IllegalArgumentException("Error! The given file is "
                    + "either null, does not exist anymore or the program lacks"
                    + "the permission to read it!");
        }
        SudokuField field;
        DisplayedSudoku sudoku;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Line line = readLine(reader, 0);
            if (line == null) {
                invalidFileError(1);
            }
            int[] numbersOfRowsAndColumns =
                    parseRowsPerBoxAndColumnsPerBox(line.text, 1);
            int rowsPerBox = numbersOfRowsAndColumns[0];
            int columnsPerBox = numbersOfRowsAndColumns[1];
            sudoku = new DisplayedSudoku(rowsPerBox, columnsPerBox);
            line = readLine(reader, line.number);
            for (int currentRow = 0; currentRow < rowsPerBox * columnsPerBox;
                 currentRow++) {
                if (line == null) {
                    invalidFileError(currentRow + 1);
                }
                parseRow(line.text, sudoku, currentRow, line.number);
                line = readLine(reader, line.number);
            }
            field = new SudokuField(rowsPerBox, columnsPerBox, sudoku);
        }
        return field;
    }

    /**
     * Reads a line from a BufferedReader, and returns it with its number within
     * the file. Treats lines that start with {@code '#'} as commentaries.
     * Returns null after the end of the file has been reached.
     *
     * @param reader The Reader containing the line(s) to be read.
     * @param lineNumber The number of the last read line.
     * @return the next line of the reader that is no comment with its number.
     * @throws IOException If an I/O Error occurs.
     */
    private static Line readLine(BufferedReader reader, int lineNumber)
            throws IOException {
        assert reader != null && lineNumber >= 0;
        Line line = new Line();
        line.number = lineNumber;
        do {
            ++line.number;
            line.text = reader.readLine();
            if (line.text == null) {
                return null;
            }
        } while (line.text.trim().startsWith("#"));  // Optional.
        return line;
    }

    /**
     * Takes a String, tries to translate it into a Sudoku row and adds it to
     * {@code sudoku} if possible as row {@code currentRow}.
     *
     * @param line The line representing a row of a SudokuBoard.
     * @param sudoku The Sudoku that the row should be added to.
     * @param currentRow The position of the row in the sudoku.
     * @param lineNumber The number of the line within the Reader.
     * @throws ParseException If the file has not the expected format.
     */
    private static void parseRow(String line, DisplayedSudoku sudoku,
                                 int currentRow, int lineNumber)
            throws ParseException {
        assert line != null && sudoku != null && currentRow > 0
                && currentRow <= sudoku.getNumbers() && lineNumber >= 0;
        Scanner rowScanner = new Scanner(line);
        int columnsPerRow = sudoku.getNumbers();
        int[] row = new int[columnsPerRow];
        rowScanner.useDelimiter(DELIMITER);
        for (int currentColumn = 0; currentColumn < columnsPerRow;
             currentColumn++) {
            row[currentColumn] = parseCellContent(rowScanner, columnsPerRow,
                    lineNumber);

        }
        if (rowScanner.hasNext()) {
            invalidFileError(lineNumber);
        }
        for (int i = 0; i < row.length; i++) {
            if (row[i] != DisplayedSudoku.UNSET_CELL) {
                sudoku.setCell(currentRow, i, row[i], false);
            }
        }
    }

    /**
     * Parses the content of a Sudoku cell within a row and returns either the
     * number or {@code SudokuBoard.UNSET_CELL} if the entry is {@code "."}.
     *
     * @param scanner The scanner containing the row.
     * @param maxNumber The maximal number that may be inserted into a cell.
     * @param lineNumber The current line within the file.
     * @return The content of the cell.
     * @throws ParseException If the entry has not the expected format.
     */
    private static int parseCellContent(Scanner scanner, int maxNumber,
                                        int lineNumber) throws ParseException {
        assert scanner != null && maxNumber > 0 && lineNumber >= 0;
        int content = 0;
        if (scanner.hasNextInt()) {
            content = scanner.nextInt();
            if (content > 0 && content <= maxNumber) {
                return content;
            } else {
                invalidFileError(lineNumber);
            }
        }
        if (scanner.hasNext()) {
            if (scanner.next().equals(".")) {
                return DisplayedSudoku.UNSET_CELL;
            }
        }
        invalidFileError(lineNumber);
        return content;
    }

    /**
     * Reads the number of rows and the number of columns of the sudoku from
     * {@code args} and returns the two numbers within an array with the number
     * of rows at position 0 and number of columns at position 1.
     *
     * @param arg The String containing the numbers.
     * @param lineNumber The current line.
     * @return the number of rows and the number of columns of Sudoku.
     * @throws ParseException If the file does not have the expected format.
     */
    private static int[] parseRowsPerBoxAndColumnsPerBox(String arg,
                                                         int lineNumber)
            throws ParseException {
        assert arg != null && lineNumber >= 0;
        Scanner scanner = new Scanner(arg);
        scanner.useDelimiter(SudokuFieldFactory.DELIMITER);
        int[] result = new int[2];
        if (scanner.hasNextInt()) {
            result[0] = scanner.nextInt();
        } else {
            invalidFileError(lineNumber);
        }
        if (scanner.hasNextInt()) {
            result[1] = scanner.nextInt();
        } else {
            invalidFileError(lineNumber);
        }
        if (result[0] < 0 || result[1] < 0 || scanner.hasNext()) {
            invalidFileError(lineNumber);
        }
        return result;
    }

    /**
     * Reports a parse error within the input file.
     *
     * @param lineNumber The line in the file which contains an error.
     * @throws ParseException If the file is not using the expected format.
     */
    private static void invalidFileError(int lineNumber)
            throws ParseException {
        throw new ParseException(
                "Malformed file at line: " + lineNumber + "!", lineNumber);
    }

    /**
     * Combines a text line of the file with its line number.
     */
    private static class Line {
        private String text;
        private int number;
    }
}