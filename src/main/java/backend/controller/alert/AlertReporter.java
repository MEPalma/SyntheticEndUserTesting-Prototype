package backend.controller.alert;

import backend.controller.client.ClientConnection;
import backend.controller.client.ClientSessionManager;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static common.api.serialization.JSONObjectMapper.JSONObjectMapper;

public class AlertReporter implements Runnable {
    private final ClientSessionManager clientSessionManager;
    private final AlertDispatcher alertDispatcher;
    private final AtomicBoolean running;

    public AlertReporter(ClientSessionManager clientSessionManager, AlertDispatcher alertDispatcher) {
        this.clientSessionManager = clientSessionManager;
        this.alertDispatcher = alertDispatcher;
        this.running = new AtomicBoolean(false);
    }

    @Override
    public void run() {
        this.running.set(true);
        while (this.running.get()) {
            String clientHandle = null;
            try {
                AlertRef alert = this.alertDispatcher.waitUtilPoll();
                if (alert != null && alert.getHandle() != null) {
                    clientHandle = alert.getHandle();
                    ClientConnection cc = this.clientSessionManager.connOfHandle(clientHandle);
                    if (cc != null) {
                        cc.send(JSONObjectMapper.writeValueAsString(alert.getAlert().toApiAlert()));
                    }
                }
            } catch (InterruptedException e) {
                // Ignore: notified to continue.
            } catch (IOException ignore) {
                if (clientHandle != null)
                    clientSessionManager.removeLoggedInSession(clientHandle);
            } catch (Exception ignore) {
            }
        }
    }
}
