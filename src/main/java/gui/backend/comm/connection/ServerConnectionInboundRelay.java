package gui.backend.comm.connection;

import common.alert.Alert;
import common.alert.UserLogin;
import common.api.twitteruser.postuser.UserSignin;
import org.apache.http.auth.InvalidCredentialsException;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static common.api.serialization.JSONObjectMapper.JSONObjectMapper;

public class ServerConnectionInboundRelay implements Runnable {
    private final BufferedReader bufferedReader;
    private final List<ServerConnectionListener> listeners;
    private final AtomicBoolean isActive;

    public ServerConnectionInboundRelay(BufferedReader bufferedReader, List<ServerConnectionListener> listeners) {
        this.bufferedReader = bufferedReader;
        this.listeners = listeners;
        this.isActive = new AtomicBoolean(false);
    }

    @Override
    public void run() {
        this.isActive.set(true);
        while (this.isActive.get()) {
            Alert alert = this.readAlert();
            if (alert != null)
                new Thread(() -> multiplexRelay(alert)).start();
        }
    }

    public void stop() {
        this.isActive.set(false);
    }

    private Alert readAlert() {
        Alert alert = null;
        try {
            String txt = this.bufferedReader.readLine();
            if (txt != null && !txt.isBlank()) {
                alert = JSONObjectMapper.readValue(txt, Alert.class);
            }
        } catch (SocketTimeoutException ignore) {
        } catch (IOException ex) {
            if (ex.getMessage().contains("Stream closed") && this.isActive.get())
                ex.printStackTrace();
        } catch (Exception ex) {
            // TODO log.
            ex.printStackTrace();
        }
        return alert;
    }

    private void multiplexRelay(Alert alert) {
        if (alert instanceof UserLogin.UserLoginSuccess) {
            // Await for login procedure to terminate.
            CountDownLatch countDownLatch = new CountDownLatch(listeners.size());
            for (var listener : listeners)
                new Thread(() -> {
                    listener.onLoggedIn();
                    countDownLatch.countDown();
                }).start();
            try {
                countDownLatch.await();
            } catch (InterruptedException ex) {
                // TODO: log this should not interrupt here.
                ex.printStackTrace();
            }
        } else if (alert instanceof UserLogin.UserLoginFailed){
            // TODO.
        } else if (alert instanceof UserSignin.UserSigninFailed) {
            var ex = new InvalidCredentialsException(); // TODO?
            for (var listener : this.listeners)
                new Thread(() -> listener.onLoginError(ex)).start();
        } else
            for (var listener : this.listeners)
                new Thread(() -> listener.onAlert(alert)).start();
    }

}
