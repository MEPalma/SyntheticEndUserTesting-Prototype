package backend.controller.alert;

import backend.controller.client.ClientSessionManager;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class AlertDispatcher {
    public static final int NUM_REPORTERS = 2;
    private final ClientSessionManager clientSessionManager;
    private final PriorityBlockingQueue<AlertRef> alertQueue;
    private final List<AlertReporter> alertReporters;

    public AlertDispatcher(ClientSessionManager clientSessionManager) {
        this.clientSessionManager = clientSessionManager;
        this.alertQueue = new PriorityBlockingQueue<>(100);

        this.alertReporters = new LinkedList<>();
        for (int i = 0; i < NUM_REPORTERS; ++i) {
            var alertReporter = new AlertReporter(this.clientSessionManager, this);
            new Thread(alertReporter).start();
            this.alertReporters.add(alertReporter);
        }
    }

    public synchronized void injectAlert(AlertRef alert) {
        if (alert != null && alert.getAlert() != null && !this.alertQueue.contains(alert))
            this.alertQueue.put(alert);
    }

    public AlertRef waitUtilPoll() throws InterruptedException {
        return this.alertQueue.take();
    }
}
