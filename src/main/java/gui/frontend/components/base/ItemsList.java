package gui.frontend.components.base;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ItemsList<T extends JComponent> extends Div {
    public static final int PADDING_SIZE = 10;

    private static final Dimension padDim = new Dimension(PADDING_SIZE, PADDING_SIZE);

    private List<T> items;

    private final Div listDiv;

    public ItemsList() {
        listDiv = new Div();
        BoxLayout boxLayout = new BoxLayout(listDiv, BoxLayout.PAGE_AXIS);
        listDiv.setLayout(boxLayout);
        add(listDiv, BorderLayout.NORTH);
    }

    public synchronized List<T> getItems() {
        return items;
    }

    public synchronized void setItems(List<T> items) {
        this.items = items;
        listDiv.removeAll();
        for (var item : items) {
            listDiv.add(item);
            listDiv.add(Box.createRigidArea(padDim));
        }
        listDiv.revalidate();
        listDiv.repaint();
        revalidate();
        repaint();
    }

}
