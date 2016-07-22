package sudoku.gui.view;

import sudoku.gui.SudokuFieldFactory;
import sudoku.model.InvalidSudokuException;
import sudoku.model.UnsolvableSudokuException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.undo.UndoManager;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

/**
 * A JFrame holding a sudoku that can be manipulated via various functions like
 * being loaded from a file, undoing edits and setting and unsetting cells.
 */
public class SudokuFrame extends JFrame {
    private JMenuItem undoMenuEntry;
    private SudokuField field;
    private UndoManager undoManager;

    /**
     * Creates a new SudokuFrame, its menu bar, initializes an UndoManager and
     * the data representing the sudoku as well as the components displaying
     * those.
     */
    private SudokuFrame() {
        super("Sudoku");
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createTheFileMenu());
        menuBar.add(createTheEditMenu());
        menuBar.add(createTheSolveMenu());
        add(menuBar);
        setJMenuBar(menuBar);
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setVisible(true);
        undoManager = new UndoManager();
    }

    /**
     * Sets the value of the specified cell.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @param value The value to be set.
     */
    void setCell(int row, int col, int value) {
        if (row < 0 || col < 0) {
            throw new IllegalArgumentException("Error! Tried to set a cell "
                    + "that is not on the board!");
        }
        field.setCell(row, col, value, undoManager);
        updateUndoMenuEntry();
    }

    /**
     * Removes the value of the specified cell.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     */
    void unsetCell(int row, int col) {
        if (row < 0 || col < 0) {
            throw new IllegalArgumentException("Error! Tried to unset a cell "
                    + "that is not on the board!");
        }
        field.unsetCell(row, col, undoManager);
        updateUndoMenuEntry();
    }

    /**
     * Creates and returns the JMenu that holds the options to open a sudoku
     * from a file and to end the program.
     *
     * @return the menu "File" with its entries.
     */
    private JMenu createTheFileMenu() {
        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.setToolTipText("Open a file or stop the program.");
        JMenuItem entry = new JMenuItem("Open");
        entry.setMnemonic(KeyEvent.VK_O);
        entry.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                InputEvent.CTRL_MASK));
        entry.setToolTipText("Opens a Sudoku from a file.");
        entry.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fileChooser.getSelectedFile();
                    if (file != null && file.exists() && file.isFile()
                            && file.canRead()) {
                        SudokuField old = null;
                        if (field != null) {
                            old = field;
                        }
                        field = SudokuFieldFactory.loadFromFile(file);
                        if (old != null) {
                            getContentPane().remove(old);
                        }
                        undoManager.discardAllEdits();
                        undoMenuEntry.setEnabled(false);
                        getContentPane().add(field);
                        pack();
                    } else {
                        showErrorPopup("An error occured when trying to"
                                + "open the chosen file! Check its status and"
                                + "your permissions to read it!", "Error!");
                    }
                } catch (FileNotFoundException f) {
                    showErrorPopup("Couldn't find the specified file!",
                            "Error!");
                } catch (IOException f) {
                    showErrorPopup("An error occured when trying to open "
                            + "and read the file!", "I/O-Error!");
                } catch (ParseException f) {
                    showErrorPopup("The chosen file is not formatted "
                            + "properly!", "Error!");
                }
            }
        });
        menu.add(entry);
        entry = new JMenuItem("Exit");
        entry.setMnemonic(KeyEvent.VK_X);
        entry.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                InputEvent.CTRL_MASK));
        entry.setToolTipText("Ends the program and closes the window.");
        entry.addActionListener(e -> dispose());
        menu.add(entry);
        return menu;
    }

    /**
     * Creates and returns the JMenu that holds the ability to undo edits.
     *
     * @return the menu "Edit" with its entries.
     */
    private JMenu createTheEditMenu() {
        JMenu menu = new JMenu("Edit");
        menu.setMnemonic(KeyEvent.VK_E);
        menu.setToolTipText("Undo the latest actions.");
        JMenuItem entry = new JMenuItem("Undo");
        entry.setEnabled(false);
        entry.setMnemonic(KeyEvent.VK_U);
        entry.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
                InputEvent.CTRL_MASK));
        entry.setToolTipText("Undo the latest change.");
        entry.addActionListener(e -> {
            undoManager.undo();
            updateUndoMenuEntry();
        });
        undoMenuEntry = entry;
        menu.add(entry);
        return menu;
    }

    /**
     * Creates and returns the JMenu that holds the the ability to fill a single
     * cell or solve the whole sudoku.
     *
     * @return the menu "Solve" with its entries.
     */
    private JMenu createTheSolveMenu() {
        JMenu menu = new JMenu("Solve");
        menu.setMnemonic(KeyEvent.VK_S);
        menu.setToolTipText("Suggest a value or solve the Sudoku.");
        JMenuItem entry = new JMenuItem("Suggest Value");
        entry.setMnemonic(KeyEvent.VK_V);
        entry.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
                InputEvent.CTRL_MASK));
        entry.setToolTipText("Fill a cell with a value retrieved from a"
                + "possible solution.");
        entry.addActionListener(e -> {
            try {
                field.suggestValue(undoManager);
                updateUndoMenuEntry();
            } catch (InvalidSudokuException f) {
                showErrorPopup("The current Sudoku is no valid Sudoku!",
                        "Error");
            } catch (UnsolvableSudokuException f) {
                showErrorPopup("The current Sudoku is not solvable!",
                        "Unsolvable Sudoku");
            }
        });
        menu.add(entry);
        entry = new JMenuItem("Solve");
        entry.setMnemonic(KeyEvent.VK_S);
        entry.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                InputEvent.CTRL_MASK));
        entry.setToolTipText("Solve the Sudoku.");
        entry.addActionListener(e -> {
            try {
                field.solveSudoku(undoManager);
                updateUndoMenuEntry();
            } catch (InvalidSudokuException f) {
                showErrorPopup("The current Sudoku is no valid Sudoku!",
                        "Error");
            } catch (UnsolvableSudokuException f) {
                showErrorPopup("The current Sudoku is not solvable!",
                        "Unsolvable Sudoku");
            }
        });
        menu.add(entry);
        return menu;
    }

    /**
     * Shows an error popup dialog showing the specified title and text.
     *
     * @param message The text of the popup dialog.
     * @param title The title of the popup dialog.
     */
    private void showErrorPopup(String message, String title) {
        assert message != null && title != null;
        JOptionPane.showMessageDialog(this, message, title,
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Enables or disables the "Undo" menu entry depending on the status of the
     * UndoManager.
     */
    private void updateUndoMenuEntry() {
        if (undoManager.canUndo()) {
            undoMenuEntry.setEnabled(true);
        } else {
            undoMenuEntry.setEnabled(false);
        }
    }

    /**
     * Initializes a new SudokuFrame and manages the user's interactions with
     * it.
     *
     * @param args Currently unused.
     */
    public static void main(String[] args) {
        new SudokuFrame();
    }
}