package agent;

import gui.frontend.MainFrame;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class FrequencyAgentRunner {
    public static void main(String[] args) throws InterruptedException, IOException {
        CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
            latch.countDown();
        });
        latch.await();
//        args = new String[] {"s", "d", "./LCs/LC-retweet1.jsonl"};
        new FrequencyAgent(new SignupLoginAgent.SignupLoginAgentCredential(args[0], args[1]), new File(args[2])).run();
    }
}
