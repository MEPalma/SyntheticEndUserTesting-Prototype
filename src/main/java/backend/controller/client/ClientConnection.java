package backend.controller.client;

import common.alert.UserLogin;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

import static common.api.serialization.JSONObjectMapper.JSONObjectMapper;

public class ClientConnection {

    public interface ClientConnectionHooks {
        boolean connect(String token, String handle, ClientConnection clientConnection);

        void connected(String token, String handle, ClientConnection clientConnection);

        void disconnect(String token);
    }

    private final ClientConnectionHooks clientConnectionHooks;
    private final Socket socket;
    private final BufferedWriter socketOut;
    private final BufferedReader socketIn;

    private String token;
    private String handle;

    public ClientConnection(ClientConnectionHooks clientConnectionHooks, Socket socket) throws IOException {
        this.clientConnectionHooks = clientConnectionHooks;
        this.socket = socket;
        this.socket.setSoTimeout(2_000);
        this.socketOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.connect();
    }

    public void close() {
        try {
            this.socket.close();
            this.socketOut.close();
            this.socketIn.close();
            this.clientConnectionHooks.disconnect(token);
        } catch (Exception ex) {
            // Ignore, error whilst closing socket.
            ex.printStackTrace();
        }
    }

    private void connect() throws IOException {
        boolean isConnected = false;
        try {
            UserLogin.UserLoginRequest userLoginRequest =
                    JSONObjectMapper.readValue(this.read(), UserLogin.UserLoginRequest.class);
            isConnected = this.clientConnectionHooks.connect(userLoginRequest.token, userLoginRequest.handle, this);
            if (isConnected) {
                this.token = userLoginRequest.token;
                this.handle = userLoginRequest.handle;
                this.send(JSONObjectMapper.writeValueAsString(new UserLogin.UserLoginSuccess()));
            } else {
                this.send(JSONObjectMapper.writeValueAsString(new UserLogin.UserLoginFailed()));
            }
        } finally {
            if (!isConnected)
                close();
            else {
                new Thread(this::monitorConnection).start();
                this.clientConnectionHooks.connected(token, handle, this);
            }
        }
    }

    private void monitorConnection() {
        while (socket.isConnected()) {
            try {
                // Note: never reading except for this very function and login.
                if (socketIn.read() == -1)
                    break;
            } catch (SocketTimeoutException ignore) {
            } catch (Exception e) {
                break;
            }
        }
        close();
    }

    public synchronized void send(String json) {
        try {
            this.socketOut.write(json.strip());
            this.socketOut.write('\n');
            this.socketOut.flush();
        } catch (IOException e) {
            close();
        }
    }

    private synchronized String read() {
        try {
            return this.socketIn.readLine();
        } catch (IOException ex) {
            close();
        }
        return null;
    }

}
