package app;

import app.exceptions.ParseConfigException;
import app.model.Executor;
import app.view.GraphicsView;
import app.view.View;

import java.io.IOException;

public class Main {
    public static void main(String [] args) throws IOException {
        View view = new GraphicsView();
        Executor executor;

        String address = null;
        try {
            address = args[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            //
        }

        try {
            executor = new Executor(view, address);
        } catch (ParseConfigException e) {
            throw new RuntimeException(e);
        }

        view.connect(executor);
        executor.execute();
    }
}
