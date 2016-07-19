package sudoku.gui.view;

import sudoku.gui.SudokuFieldFactory;
import sudoku.model.InvalidSudokuException;
import sudoku.model.UnsolvableSudokuException;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class SudokuFrame extends JFrame {
    private JMenuItem undoMenuEntry;
    private JMenuBar menuBar;
    private SudokuField field;
    private UndoManager undoManager;

    public SudokuFrame () {
        super("Sudoku");
        menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.setToolTipText("Open a file or stop the program.");
        JMenuItem entry = new JMenuItem("Open");
        entry.setMnemonic(KeyEvent.VK_O);
        entry.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                ActionEvent.CTRL_MASK));
        entry.setToolTipText("Opens a Sudoku from a file.");
        entry.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fileChooser.getSelectedFile();
                    if (file != null && file.exists() && file.isFile() &&
                            file.canRead()) {
                        field = SudokuFieldFactory.loadFromFile(file);
                        this.getContentPane().add(field);
                        this.pack();
                    }
                    else {
                        this.showErrorPopup("An error occured when trying to"
                                + "open the chosen file! Check its status and"
                                + "your permissions to read it!", "Error!");
                    }
                } catch (IOException f) {
                    this.showErrorPopup("An error occured when trying to open "
                            + "and read the file!", "I/O-Error!");
                } catch (ParseException f) {
                    this.showErrorPopup("The chosen file is not formatted "
                            + "properly!", "Error!");
                }
            }
        });
        menu.add(entry);
        entry = new JMenuItem("Exit");
        entry.setMnemonic(KeyEvent.VK_X);
        entry.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                ActionEvent.CTRL_MASK));
        entry.setToolTipText("Ends the program and closes the window.");
        entry.addActionListener(e -> this.dispose());
        menu.add(entry);
        menuBar.add(menu);
        menu = new JMenu("Edit");
        menu.setMnemonic(KeyEvent.VK_E);
        menu.setToolTipText("Undo the latest actions.");
        entry = new JMenuItem("Undo");
        entry.setEnabled(false);
        entry.setMnemonic(KeyEvent.VK_U);
        entry.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
                ActionEvent.CTRL_MASK));
        entry.setToolTipText("Undo the latest change.");
        entry.addActionListener(e -> undoManager.undo());
        undoMenuEntry = entry;
        menu.add(entry);
        menuBar.add(menu);
        menu = new JMenu("Solve");
        menu.setMnemonic(KeyEvent.VK_S);
        menu.setToolTipText("Suggest a value or solve the Sudoku.");
        entry = new JMenuItem("Suggest Value");
        entry.setMnemonic(KeyEvent.VK_V);
        entry.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
                ActionEvent.CTRL_MASK));
        entry.setToolTipText("Fill a cell with a value retrieved from a"
                + "possible solution.");
        entry.addActionListener(e -> {
            try {
                field.suggestValue();
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
                ActionEvent.CTRL_MASK));
        entry.setToolTipText("Solve the Sudoku.");
        entry.addActionListener(e -> {
            try {
                field.solveSudoku();
            } catch (InvalidSudokuException f) {
                showErrorPopup("The current Sudoku is no valid Sudoku!",
                        "Error");
            } catch (UnsolvableSudokuException f) {
                showErrorPopup("The current Sudoku is not solvable!",
                        "Unsolvable Sudoku");
            }
        });
        menu.add(entry);
        menuBar.add(menu);
        add(menuBar);
        setJMenuBar(menuBar);
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setVisible(true);
        undoManager = new UndoManager();
    }

    public void setCell (int row, int col, int value) {
        field.setCell(row, col, value, undoManager);
        updateUndoButton();
    }

    public void unsetCell (int row, int col) {
        field.unsetCell(row, col, undoManager);
        updateUndoButton();
    }

    private void showErrorPopup (String message, String title) {
        JOptionPane.showMessageDialog(this, message, title,
                JOptionPane.ERROR_MESSAGE);
    }

    private void updateUndoButton() {
        if (undoManager.canUndoOrRedo()) {
            undoMenuEntry.setEnabled(true);
        } else {
            undoMenuEntry.setEnabled(false);
        }
    }

    public static void main (String[] args) {
        new SudokuFrame();
    }
}