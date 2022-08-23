package backend;

import backend.controller.Controller;
import backend.httpproxy.HttpProxy;
import backend.persistence.db.Database;

import java.io.IOException;

import static common.comms.HttpApiComms.BACKEND_API_ENDPOINT_PORT;
import static common.comms.HttpApiComms.BACKEND_CONN_ENDPOINT_PORT;

public class Backend {
    private final Controller controller;
    private final HttpProxy httpProxy;

    public Backend() throws IOException {
        if (Database.H_SESSION_FACTORY.isClosed())
            throw new ExceptionInInitializerError("[[ DATABASE SESSION IS CLOSED ]]");
        this.controller = new Controller(BACKEND_CONN_ENDPOINT_PORT);
        this.httpProxy = new HttpProxy(BACKEND_API_ENDPOINT_PORT, this.controller);
    }

    public Controller getController() {
        return controller;
    }

    public HttpProxy getHttpProxy() {
        return httpProxy;
    }
}
