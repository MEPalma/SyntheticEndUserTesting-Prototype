package app;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        gui.frontend.Main.main(args);
        backend.Main.main(args);
    }
}
