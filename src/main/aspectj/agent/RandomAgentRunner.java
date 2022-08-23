package agent;


import gui.frontend.MainFrame;

import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class RandomAgentRunner {
    public static void main(String[] args) throws InterruptedException, IOException {
        CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
            latch.countDown();
        });
        latch.await();
        new RandomAgent(new SignupLoginAgent.SignupLoginAgentCredential(args[0], args[1])).run();
    }
}
