package sudoku.gui.view;

import sudoku.gui.DisplayedSudokuFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Observable;
import java.util.Observer;

public class SudokuFrame extends JFrame implements Observer {
    private JMenuBar menuBar;
    private SudokuField field;

    public SudokuFrame () {
        super("Sudoku");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setVisible(true);
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
                    field.openFile(fileChooser.getSelectedFile());
                } catch (IOException f) {

                }
            }
        });
        menu.add(entry);
        entry = new JMenuItem("Exit");
        entry.setMnemonic(KeyEvent.VK_X);
        entry.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                ActionEvent.CTRL_MASK));
        entry.setToolTipText("Ends the program and closes the window.");
        entry.addActionListener(e -> {
            this.dispose();
            }
        );
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
        entry.addActionListener(e -> {});
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
        entry.addActionListener(e -> {});
        menu.add(entry);
        entry = new JMenuItem("Solve");
        entry.setMnemonic(KeyEvent.VK_S);
        entry.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                ActionEvent.CTRL_MASK));
        entry.setToolTipText("Solve the Sudoku.");
        entry.addActionListener(e -> {});
        menu.add(entry);
        menuBar.add(menu);
        add(menuBar);
        setJMenuBar(menuBar);
    }

    public void update (Observable obv, Object args) {
        this.pack();
    }

    public static void main (String[] args) {
        new SudokuFrame();
    }
}
