package backend.controller.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientSessionManager implements Runnable {

    public interface ClientSessionManagerListener {
        void connected(String token, String handle);
    }

    private final ClientSessionManagerListener clientSessionManagerListener;
    private final AtomicBoolean isRunning;
    private final Map<String, ClientConnection> handleToConns;
    private final Map<String, String> tokenToHandle;
    private final ServerSocket serverSocket;
    private final ClientConnection.ClientConnectionHooks clientConnectionHooks;

    public ClientSessionManager(int port, ClientSessionManagerListener clientSessionManagerListener) throws IOException {
        this.clientSessionManagerListener = clientSessionManagerListener;
        this.isRunning = new AtomicBoolean(false);
        this.tokenToHandle = Collections.synchronizedMap(new HashMap<>());
        this.handleToConns = Collections.synchronizedMap(new HashMap<>());
        this.serverSocket = new ServerSocket(port);
        this.serverSocket.setSoTimeout(2_000);
        this.clientConnectionHooks = new ClientConnection.ClientConnectionHooks() {
            @Override
            public synchronized boolean connect(String token, String handle, ClientConnection clientConnection) {
                if (!canLogin(token, handle)) return false;
                else {
                    handleToConns.put(handle, clientConnection);
                    return true;
                }
            }

            @Override
            public void connected(String token, String handle, ClientConnection clientConnection) {
                new Thread(() -> clientSessionManagerListener.connected(token, handle)).start();
            }

            @Override
            public synchronized void disconnect(String token) {
                removeLoginToken(token);
            }
        };
    }

    @Override
    public void run() {
        this.isRunning.set(true);
        while (this.isRunning.get()) {
            try {
                spinupClientConnection(this.serverSocket.accept());
            } catch (SocketTimeoutException e) {
                // Ignore
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void spinupClientConnection(Socket clientSocket) {
        new Thread(() -> {
            try {
                new ClientConnection(clientConnectionHooks, clientSocket);
            } catch (IOException e) {
                // TODO
            }
        }).start();
    }

    public String handleOfToken(String token) {
        return this.tokenToHandle.getOrDefault(token, null);
    }

    public void addLoginToken(String handle, String token) {
        removeLoginToken(token);
        this.tokenToHandle.put(token, handle);
        this.handleToConns.put(handle, null);
    }

    public boolean canSignIn(String handle) {
        return !handleToConns.containsKey(handle);
    }

    public boolean canLogin(String token, String handle) {
        return tokenToHandle.containsKey(token) && handleToConns.containsKey(handle) && handleToConns.get(handle) == null;
    }

    public void removeLoginToken(String token) {
        var handle = this.tokenToHandle.remove(token);
        ClientConnection clientConnection = this.handleToConns.getOrDefault(handle, null);
        handleToConns.remove(handle);
        if (clientConnection != null) clientConnection.close();
    }

    public void removeLoggedInSession(String handle) {
        var maybeSess = tokenToHandle.entrySet().stream().filter(stringStringEntry -> stringStringEntry.getValue().equals(handle)).findFirst();
        if (maybeSess.isPresent()) {
            String token = maybeSess.get().getKey();
            removeLoginToken(token);
        }
    }

    public boolean isLoggedInSession(String handle, String token) {
        return tokenToHandle.containsKey(token) && tokenToHandle.get(token).equals(handle) && handleToConns.getOrDefault(handle, null) != null;
    }

    public ClientConnection connOfHandle(String handle) {
        return handleToConns.getOrDefault(handle, null);
    }
}
