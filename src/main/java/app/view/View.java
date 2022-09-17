package app.view;


import app.model.Executor;

import java.util.List;

public interface View {
    void updateOnline(int online);

    void updateAddresses(List<String> addresses);

    void connect(Executor executor);
}
