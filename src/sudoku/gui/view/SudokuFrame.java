package sudoku.gui.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Observer;

public class SudokuFrame extends JFrame implements Observer {
    private JMenuBar menuBar;
    private SudokuField field;

    public SudokuFrame () {
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
            SudokuField field = ((SudokuFrame) ((Container) e.getSource())
                    .getParent().getParent().getParent()).field;
            try {
                field.openFile();
            } catch (IOException f) {
                //TODO
            }
        });
        menu.add(entry);
        /*entry = new JMenu("Exit");
        entry.setMnemonic(KeyEvent.VK_X);
        entry.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                ActionEvent.CTRL_MASK));
        entry.setToolTipText("Ends the program and closes the window.");
        entry.addActionListener();
        menu.add(entry);
        menuBar.add(menu);
        menu = new JMenu("Edit");
        menu.setMnemonic(KeyEvent.VK_E);
        menu.setToolTipText("Undo the latest actions.");
        entry = new JMenuItem("Undo");
        entry.setMnemonic(KeyEvent.VK_U);
        entry.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
                ActionEvent.CTRL_MASK));
        entry.setToolTipText("Undo the latest change.");
        entry.addActionListener();
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
        entry.addActionListener();
        menu.add(entry);
        entry = new JMenuItem("Solve");
        entry.setMnemonic(KeyEvent.VK_S);
        entry.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                ActionEvent.CTRL_MASK));
        entry.setToolTipText("Solve the Sudoku.");
        entry.addActionListener();
        menu.add(entry);
        menuBar.add(menu);*/
        this.setJMenuBar(menuBar);
    }

    public static void main (String[] args) {
        SudokuFrame frame = new SudokuFrame();
        frame.pack();
        frame.setVisible(true);
    }
}
