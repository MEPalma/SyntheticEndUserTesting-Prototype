package layer.state;

import lifecycle.lci.lcistate.LCIState;
import lifecycle.lci.lcistate.LCIStateFiniteRef;

import java.awt.*;

public aspect FiniteRefStateLayer implements StateLayer {
    private String lastViewId;

    pointcut isUpdateViewCall(Component view):
            execution(void gui.frontend.views.MainView.updateView(Component+)) && args(view);

    before (Component view): isUpdateViewCall(view) {
        synchronized (this) {
            lastViewId = view.getClass().getSimpleName();
        }
    }

    @Override
    public LCIState getState() {
        synchronized (this) {
            return new LCIStateFiniteRef(lastViewId);
        }
    }
}
