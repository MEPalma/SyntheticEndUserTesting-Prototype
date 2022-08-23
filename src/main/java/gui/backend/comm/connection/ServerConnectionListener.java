package gui.backend.comm.connection;

import common.alert.Alert;

public interface ServerConnectionListener {
    public void onLoggedIn();
    public void onLoginError(Exception ex);
    public void onAlert(Alert alert);
}
