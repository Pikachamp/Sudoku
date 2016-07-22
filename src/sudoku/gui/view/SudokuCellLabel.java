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

/**
 * An extended JLabel modified to know its position in a table, remove, set and
 * show the number its holding and managing a popup-menu giving the user the
 * opportunity to do so.
 */
final class SudokuCellLabel extends JLabel {
    private final int row;
    private final int col;
    private SudokuPopupMenu popupMenu;

    /**
     * Creates a new SudokuCellLabel with the coordinates {@code (row, col)}
     * containing a popup menu that can set numbers from 1 to maxNumber and
     * remove the content of the cell.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @param maxNumber The highest number that may be set into the cell.
     */
    SudokuCellLabel(int row, int col, int maxNumber) {
        super();
        if (row < 0 || col < 0 || maxNumber < row || maxNumber < col) {
            throw new IllegalArgumentException("Error! The cell may not have a "
                    + "negative row or column and the highest number to be set "
                    + "may not be lower than the row and the column!");
        }
        setPreferredSize(new Dimension(26, 26));
        setBorder(BorderFactory.createLoweredBevelBorder());
        setHorizontalAlignment(JLabel.CENTER);
        setVerticalAlignment(JLabel.CENTER);
        this.row = row;
        this.col = col;
        popupMenu = new SudokuPopupMenu(maxNumber);
        add(popupMenu);
        addMouseListener(new MouseAdapter() {
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

    /**
     * Returns the row of the cell.
     *
     * @return the row of the cell.
     */
    int getRow() {
        return row;
    }

    /**
     * Returns the column of the cell.
     *
     * @return the column of the cell.
     */
    int getCol() {
        return col;
    }

    /**
     * Sets the text {@code this} displays. It additionally disables the popup
     * menu and changes the colour to red if the cell should not be changeable.
     *
     * @param value The String the cell should display.
     * @param changeable Determines whether the value may still be changed.
     */
    void set(String value, boolean changeable) {
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

/**
 * A popup menu giving the options to set a cell or to remove the value from a
 * cell.
 */
final class SudokuPopupMenu extends JPopupMenu {

    /**
     * Creates a new JPopupMenu that can be used to set or remove the value of a
     * {@link SudokuCellLabel}.
     *
     * @param maxNumber The highest number that may be set by this popup menu.
     */
    SudokuPopupMenu(int maxNumber) {
        super();
        if (maxNumber <= 0) {
            throw new IllegalArgumentException("Error! The popup menu must at "
                    + "least have a max number of 1!");
        }
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
                            .unsetCell(cell.getRow(), cell.getCol());
                });
        setEnabled(true);
    }

    /**
     * Creates and adds a JMenuItem with the given functionality.
     *
     * @param text The text of the item.
     * @param tooltip The tooltip text of the item.
     * @param listener The listener the item should
     */
    private void addJMenuItem(String text, String tooltip,
                              ActionListener listener) {
        assert text != null && tooltip != null && listener != null;
        JMenuItem entry = new JMenuItem(text);
        entry.setToolTipText(tooltip);
        entry.addActionListener(listener);
        add(entry);
    }
}
