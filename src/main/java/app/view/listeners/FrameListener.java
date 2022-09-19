package app.view.listeners;


import app.model.Executor;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class FrameListener extends WindowAdapter {
    private final Executor executor;

    public FrameListener(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        executor.exit();
        System.exit(0);
    }
}
