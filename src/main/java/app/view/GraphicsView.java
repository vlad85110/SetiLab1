package app.view;

import app.model.Executor;
import app.view.listeners.FrameListener;
import app.view.location.Location;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GraphicsView extends JFrame implements View {
    private final JLabel counter;
    private final JLabel addressesLabel;

    public GraphicsView() {
        super("app");

        setSize(300, 200);
        Location.centreWindow(this);

        var main = new JPanel();
        counter = new JLabel();
        var counterPanel = new JPanel();
        addressesLabel = new JLabel();

        main.setLayout(new BorderLayout());
        main.add(counterPanel, BorderLayout.CENTER);

        counterPanel.setLayout(new BoxLayout(counterPanel, BoxLayout.Y_AXIS));

        counter.setAlignmentX(Component.CENTER_ALIGNMENT);
        addressesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        counterPanel.add(Box.createVerticalStrut(40));
        counterPanel.add(counter, BorderLayout.CENTER);
        counterPanel.add(Box.createVerticalStrut(40));
        counterPanel.add(addressesLabel, BorderLayout.CENTER);

        setContentPane(main);
        setVisible(true);
    }


    @Override
    public void updateOnline(int online) {
        SwingUtilities.invokeLater(() ->  this.counter.setText("Online: " + online));
    }

    @Override
    public void updateAddresses(List<String> addresses) {
        addressesLabel.setText("Addresses: " + addresses.toString());
    }

    @Override
    public void connect(Executor executor) {
        var listener = new FrameListener(executor);
        addWindowListener(listener);
    }
}
