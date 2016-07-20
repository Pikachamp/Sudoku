package sudoku.gui.view;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SudokuCellLabel extends JLabel {
    private final int row;
    private final int col;
    private SudokuPopupMenu popupMenu;

    public SudokuCellLabel(int row, int col, int maxNumber) {
        super();
        setMinimumSize(new Dimension(100, 100));
        setBorder(BorderFactory.createLoweredBevelBorder());
        this.row = row;
        this.col = col;
        popupMenu = new SudokuPopupMenu(maxNumber);
        this.add(popupMenu);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.isPopupTrigger() && popupMenu.isEnabled()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger() && popupMenu.isEnabled()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    public int getRow () {
        return row;
    }

    public int getCol () {
        return col;
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

    public SudokuPopupMenu(int maxNumber) {
        super();
        for (int i = 1; i < maxNumber; i++) {
            final int finalI = i;
            addJMenuItem(Integer.toString(i), "Sets this cell to " + i + ".",
                    e -> {
                        final SudokuCellLabel cell = (SudokuCellLabel)
                            ((JPopupMenu) ((Container) e.getSource())
                                    .getParent()).getInvoker();
                        ((SudokuFrame) cell.getTopLevelAncestor())
                                .setCell(cell.getRow(), cell.getCol(), finalI);
                    });
        }
        addJMenuItem("Remove", "Clears the cell.",
                e -> {
                    final SudokuCellLabel cell = (SudokuCellLabel)
                            ((JPopupMenu) ((Container) e.getSource())
                                    .getParent()).getInvoker();
                    ((SudokuFrame) cell.getTopLevelAncestor())
                            .unsetCell(cell.getRow(), cell.getCol())
;                });
        setEnabled(true);
    }

    private void addJMenuItem (String text, String tooltip,
                               ActionListener listener) {
        JMenuItem entry = new JMenuItem(text);
        entry.setToolTipText(tooltip);
        entry.addActionListener(listener);
        this.add(entry);
    }
}
