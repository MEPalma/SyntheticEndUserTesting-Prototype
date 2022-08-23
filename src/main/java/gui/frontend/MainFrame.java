package gui.frontend;

import gui.backend.controller.Controller;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final Controller controller;

    public MainFrame() {
        this.controller = new Controller();

        getContentPane().setLayout(new GridLayout(1, 1, 0, 0));
        getContentPane().add(this.controller.getMainView());

        setMinimumSize(new Dimension(300, 300));
        setSize(new Dimension(1000, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setVisible(true);
    }

    @Override
    public void setVisible(boolean isVisible) {
        setLocationRelativeTo(null);
        super.setVisible(isVisible);
    }

}
