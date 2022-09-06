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

        try {
            executor = new Executor(view);
        } catch (ParseConfigException e) {
            throw new RuntimeException(e);
        }

        view.connect(executor);
        executor.execute();
    }
}
