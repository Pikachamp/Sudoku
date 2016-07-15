package sudoku.gui.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SudokuCellLabel extends JLabel {
    int row;
    int col;
    SudokuPopupMenu popupMenu;

    public SudokuCellLabel(int row, int col, int maxNumber) {
        super();
        setMinimumSize(new Dimension(40, 40));
        this.row = row;
        this.col = col;
        popupMenu = new SudokuPopupMenu(maxNumber, this);
        this.add(popupMenu);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    public void set (String value, boolean changeable) {
        if (value == null) {
            throw new IllegalArgumentException("Error! Tried to set null into"
                    + "a cell!");
        }
        setText(value);
        if (!changeable) {
            setForeground(Color.RED);
            popupMenu.setEnabled(false);
        }
    }
}

class SudokuPopupMenu extends JPopupMenu {
    private SudokuCellLabel cell;

    public SudokuPopupMenu(int maxNumber, SudokuCellLabel cell) {
        super();
        this.cell = cell;
        for (int i = 1; i < maxNumber; i++) {
            final int finalI = i;
            addJMenuItem(Integer.toString(i), "Sets this cell to " + i + ".",
                    e -> ((SudokuFrame) this.cell.getTopLevelAncestor())
                            .setCell(this.cell.row, this.cell.col, finalI));
        }
        addJMenuItem("Remove", "Clears the cell.",
                e -> ((SudokuFrame) this.cell.getTopLevelAncestor()).unsetCell(
                        this.cell.row, this.cell.col));
        this.setEnabled(true);
    }

    private void addJMenuItem (String text, String tooltip,
                               ActionListener listener) {
        JMenuItem entry = new JMenuItem(text);
        entry.setToolTipText(tooltip);
        entry.addActionListener(listener);
        this.add(entry);
    }
}
