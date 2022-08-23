package gui.backend.comm.connection;

import com.fasterxml.jackson.core.JsonProcessingException;
import common.alert.UserLogin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

import static common.api.serialization.JSONObjectMapper.JSONObjectMapper;
import static common.comms.HttpApiComms.API_ENDPOINT;
import static common.comms.HttpApiComms.BACKEND_CONN_ENDPOINT_PORT;

public class ServerConnection {
    private final List<ServerConnectionListener> listeners;
    private Socket socket;
    private PrintWriter socketOut;
    private BufferedReader socketIn;
    private ServerConnectionInboundRelay inboundRelay;

    public ServerConnection() {
        this.listeners = Collections.synchronizedList(new LinkedList<>());
    }

    public synchronized void addServerConnectionListener(ServerConnectionListener listener) {
        if (!this.listeners.contains(listener))
            this.listeners.add(listener);
    }

    public synchronized void removeServerConnectionListener(ServerConnectionListener listener) {
        this.listeners.remove(listener);
    }

    public synchronized void close() {
        try {
            if (socket != null)
                socket.close();
            if (socketOut != null)
                socketOut.close();
            if (socketIn != null)
                socketIn.close();
            if (inboundRelay != null)
                inboundRelay.stop();
        } catch (Exception ex) {
            // ignore TODO: log
            ex.printStackTrace();
        } finally {
            socket = null;
            socketOut = null;
            socketIn = null;
            inboundRelay = null;
        }
    }

    private boolean connect() {
        try {
            socket = new Socket(API_ENDPOINT, BACKEND_CONN_ENDPOINT_PORT);
            socket.setSoTimeout(2_000);
            socketOut = new PrintWriter(socket.getOutputStream(), false);
            socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            inboundRelay = new ServerConnectionInboundRelay(socketIn, listeners);
            new Thread(inboundRelay).start();
            return true;
        } catch (Exception ex) {
            close();
            for (var listener : this.listeners)
                listener.onLoginError(ex);
            return false;
        }
    }

    public synchronized void signin(String token, String handle) {
        if (this.socket != null || !this.connect())
            return;
        this.sendJson(new UserLogin.UserLoginRequest(token, handle));
    }

    private void send(String message) {
        socketOut.println(message);
        socketOut.flush();
    }

    private void sendJson(Object o) {
        try {
            String req = JSONObjectMapper.writeValueAsString(o);
            this.send(req);
        } catch (JsonProcessingException e) {
            // TODO: log this should never happen.
            e.printStackTrace();
        }
    }

}
