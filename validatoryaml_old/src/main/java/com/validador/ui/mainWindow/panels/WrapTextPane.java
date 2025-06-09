package com.validador.ui.mainWindow.panels;

import javax.swing.*;
import java.awt.*;

public class WrapTextPane extends JTextPane {
    @Override
    public boolean getScrollableTracksViewportWidth() {
        if (getParent() instanceof JViewport) {
            return getUI().getPreferredSize(this).width <= getParent().getSize().width;
        }
        return super.getScrollableTracksViewportWidth();
    }

    @Override
    public void setSize(Dimension d) {
        if (d.width < getParent().getSize().width) {
            d.width = getParent().getSize().width;
        }
        super.setSize(d);
    }
}

